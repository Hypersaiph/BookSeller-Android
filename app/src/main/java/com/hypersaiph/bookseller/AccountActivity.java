package com.hypersaiph.bookseller;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.hypersaiph.bookseller.HttpClient.HttpTask;
import com.hypersaiph.bookseller.HttpClient.ResponseInterface;
import com.hypersaiph.bookseller.Models.Account;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class AccountActivity extends AppCompatActivity implements ResponseInterface {
    private LayoutInflater layoutInflater;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Toolbar toolbar;
    private int position;
    private String date;
    private String access_token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        SharedPreferences sharedPref = this.getSharedPreferences(Globals.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        access_token = sharedPref.getString("access_token",null);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        mAdapter = new adapter(CreateSaleActivity.accounts);
        recyclerView.setAdapter(mAdapter);
        //Your toolbar is now an action bar and you can use it like you always do, for example:
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle b = getIntent().getExtras();
        if(b != null){
            String code = b.getString("code");
            getSupportActionBar().setTitle("Cuentas de la Venta: "+code);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void requestFinished(String result, String method, String flag) {
        if(method.equals("PATCH") && flag.equals("update_account")){
            parseUpdateAccountResult(result);
        }
        if(method.equals("POST") && flag.equals("update_account_date")){
            parseUpdateDateAccountResult(result);
        }
    }
    private void postAccountPayment() {
        Map<String, String> postData = new HashMap<>();
        postData.put("_method", "PATCH");
        postData.put("is_active", "0");
        HttpTask task = new HttpTask(postData, "PATCH","update_account", access_token, this);
        task.execute(Globals.API_URL + "/accounts/"+CreateSaleActivity.accounts.get(position).getAccount_id());
    }
    private void postDatePayment() {
        Map<String, String> postData = new HashMap<>();
        postData.put("selected_account_id", CreateSaleActivity.accounts.get(position).getAccount_id()+"");
        postData.put("date", date);
        HttpTask task = new HttpTask(postData, "POST","update_account_date", access_token, this);
        task.execute(Globals.API_URL + "/accounts");
    }
    private void paymentDialog(final int position) {
        this.position = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
        builder.setTitle("Está segur@ de registrar este pago?");
        builder.setPositiveButton(R.string.register_payment, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                postAccountPayment();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        builder.show();
    }
    private void dateDialog(String date, int position) {
        this.date = date;
        this.position = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
        builder.setTitle("Se modificará la fecha de los siguientes pagos");
        builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                postDatePayment();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        builder.show();
    }
    private void parseUpdateAccountResult(String result) {
        try {
            JSONArray jsonArray = new JSONArray(result);
            JSONObject jsonObject = new JSONObject(jsonArray.get(0).toString());
            CreateSaleActivity.hasBeenPayed = true;
            CreateSaleActivity.accounts.get(position).setIs_active(false);
            Toast.makeText(AccountActivity.this,jsonObject.get("message").toString(),Toast.LENGTH_SHORT).show();
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void parseUpdateDateAccountResult(String result) {
        try {
            JSONArray jsonArray = new JSONArray(result);
            JSONObject jsonObject = new JSONObject(jsonArray.get(0).toString());
            CreateSaleActivity.hasBeenReScheduled = true;
            Toast.makeText(AccountActivity.this,jsonObject.get("message").toString(),Toast.LENGTH_SHORT).show();
            finish();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public class adapter extends RecyclerView.Adapter<AccountActivity.adapter.ViewHolder> {
        private ArrayList<Account> accounts;
        private boolean schedule;
        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            TextView code, payment_date, status, total;
            FancyButton reSchedule, payment;
            ViewHolder(View view) {
                super(view);
                code = view.findViewById(R.id.code);
                payment_date = view.findViewById(R.id.payment_date);
                status = view.findViewById(R.id.status);
                total = view.findViewById(R.id.total);
                reSchedule = (FancyButton) view.findViewById(R.id.reSchedule);
                payment = (FancyButton) view.findViewById(R.id.payment);

            }
        }
        // Provide a suitable constructor (depends on the kind of dataset)
        adapter(ArrayList<Account> accounts) {
            this.accounts = accounts;
            this.schedule = true;
        }
        // Create new views (invoked by the layout manager)
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
            // set the view's size, margins, paddings and layout parameters
            return new ViewHolder(v);
        }
        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            holder.code.setText(accounts.get(position).getCode());
            holder.payment_date.setText(accounts.get(position).getPayment_date());
            holder.total.setText((accounts.get(position).getAmount()+accounts.get(position).getPenalty())+" Bs.");
            if(accounts.get(position).isIs_active()){
                if(schedule){
                    holder.payment.setVisibility(View.VISIBLE);
                    holder.reSchedule.setVisibility(View.VISIBLE);
                }else{
                    holder.payment.setVisibility(View.GONE);
                    holder.reSchedule.setVisibility(View.GONE);
                }
                schedule = false;
            }else{
                holder.payment.setVisibility(View.GONE);
                holder.reSchedule.setVisibility(View.GONE);
            }
            if(accounts.get(position).getPenalty() == 0){
                holder.total.setTextColor(getResources().getColor(R.color.okay));
            }else{
                holder.total.setTextColor(getResources().getColor(R.color.wrong));
            }
            //setting status
            if(accounts.get(position).isIs_active() && accounts.get(position).getPenalty() != 0){
                holder.status.setText(R.string.account_penalty);
                holder.status.setTextColor(getResources().getColor(R.color.wrong));
            }else{
                if(!accounts.get(position).isIs_active()){
                    holder.status.setText(R.string.account_canceled);
                    holder.status.setTextColor(getResources().getColor(R.color.secondary_text));
                }else{
                    holder.status.setText(R.string.account_wait);
                    holder.status.setTextColor(getResources().getColor(R.color.secondary_text));
                }
            }
            holder.reSchedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar mcurrentTime = Calendar.getInstance();
                    int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);
                    int month = mcurrentTime.get(Calendar.MONTH);
                    int year = mcurrentTime.get(Calendar.YEAR);
                    DatePickerDialog mDatePicker;
                    mDatePicker = new DatePickerDialog(AccountActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            String date = year + "-" + (month+1) + "-" + dayOfMonth;
                            dateDialog(date, position);
                        }
                    }, year, month, day);
                    mDatePicker.setTitle("Seleccionar Nueva Fecha de Pago");
                    mDatePicker.show();
                }
            });
            holder.payment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paymentDialog(position);
                }
            });
        }
        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return accounts.size();
        }
    }
}
