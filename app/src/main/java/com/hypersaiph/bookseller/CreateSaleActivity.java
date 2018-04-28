package com.hypersaiph.bookseller;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.hypersaiph.bookseller.HttpClient.HttpTask;
import com.hypersaiph.bookseller.HttpClient.ResponseInterface;
import com.hypersaiph.bookseller.Models.Account;
import com.hypersaiph.bookseller.Models.Customer;
import com.hypersaiph.bookseller.Models.Outflow;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class CreateSaleActivity extends AppCompatActivity implements ResponseInterface{
    public static ArrayList<Outflow> cart;
    public static ArrayList<Account> accounts;
    public static Customer customer;
    public static boolean hasBeenPayed;
    public static boolean hasBeenReScheduled;

    private LayoutInflater layoutInflater;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Toolbar toolbar;
    private Spinner spinner;
    private RadioGroup radioGroup;
    private RadioButton sale_type_1;
    private int saleType, sale_position;
    private TextInputLayout input_layout_client, input_layout_months, input_layout_anual_interest;
    private EditText client, months, anual_interest, form_quantity, form_selling_price;
    private TextInputLayout input_layout_quantity, input_layout_selling_price;
    private TextView form_book_type, total_sale;
    private FancyButton search_customer;
    private FloatingActionButton add_product, create_sale, watch_accounts;
    private Button select_date;
    private String access_token, initial_date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sale);
        layoutInflater = getLayoutInflater();
        SharedPreferences sharedPref = this.getSharedPreferences(Globals.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        access_token = sharedPref.getString("access_token",null);
        initial_date = "";
        saleType = 2;
        sale_position = -1;
        hasBeenPayed = false;
        hasBeenReScheduled = false;
        cart = new ArrayList<Outflow>();
        //parse variables
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        input_layout_months = (TextInputLayout) findViewById(R.id.input_layout_months);
        input_layout_anual_interest = (TextInputLayout) findViewById(R.id.input_layout_anual_interest);
        input_layout_client = (TextInputLayout) findViewById(R.id.input_layout_client);
        search_customer = (FancyButton) findViewById(R.id.form_search_customer);
        add_product = (FloatingActionButton) findViewById(R.id.add_product);
        create_sale = (FloatingActionButton) findViewById(R.id.create_sale);
        watch_accounts = (FloatingActionButton) findViewById(R.id.accounts);
        client = (EditText) findViewById(R.id.client);
        months = (EditText) findViewById(R.id.months);
        anual_interest = (EditText) findViewById(R.id.anual_interest);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        total_sale = (TextView) findViewById(R.id.sale_total);
        select_date = (Button) findViewById(R.id.select_date);
        sale_type_1 = (RadioButton) findViewById(R.id.sale_type_1);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        mAdapter = new adapter(cart);
        recyclerView.setAdapter(mAdapter);
        //Your toolbar is now an action bar and you can use it like you always do, for example:
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle b = getIntent().getExtras();
        if(b != null){
            sale_position = b.getInt("position");
            String code = b.getString("code");
            getSupportActionBar().setTitle("Venta: "+code);
            create_sale.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.amber)));
            create_sale.setImageResource(R.mipmap.ic_edit_white_24dp);
            watch_accounts.setVisibility(View.VISIBLE);
            getSale(sale_position);
            sale_type_1.setEnabled(false);
            select_date.setEnabled(false);
        }else{
            watch_accounts.setVisibility(View.GONE);
            getSupportActionBar().setTitle("Nueva Venta");
        }
        //updateTotals();
        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);
                int month = mcurrentTime.get(Calendar.MONTH);
                int year = mcurrentTime.get(Calendar.YEAR);
                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(CreateSaleActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = year + "-" + (month+1) + "-" + dayOfMonth;
                        initial_date = date;
                        select_date.setText(getResources().getString(R.string.form_sale_date)+": "+date);
                    }
                }, year, month, day);
                mDatePicker.setTitle("Seleccionar Fecha");
                mDatePicker.show();
            }
        });
        search_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(client.getText().toString().length() > 0){
                    input_layout_client.setErrorEnabled(false);
                    getClient(client.getText().toString());
                }else{
                    input_layout_client.setError("Este campo es necesario");
                }
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.sale_type_1:
                        saleType = 1;
                        input_layout_months.setVisibility(View.GONE);
                        input_layout_anual_interest.setVisibility(View.GONE);
                        break;
                    case R.id.sale_type_2:
                        saleType = 2;
                        input_layout_months.setVisibility(View.VISIBLE);
                        input_layout_anual_interest.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CreateSaleActivity.this, AddProductActivity.class);
                startActivity(i);
            }
        });
        create_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cart.size() > 0 && customer != null){
                    if(saleType == 2){
                        if(months.getText().toString().length()>0 && anual_interest.getText().toString().length()>0 && !initial_date.equals("")){
                            int cmonths = Integer.parseInt(months.getText().toString());
                            Double xanual_interest = Double.parseDouble(anual_interest.getText().toString());
                            if(months.getText().toString().length() > 0 &&
                                    anual_interest.getText().toString().length() > 0 &&
                                    cmonths > 0 && cmonths <= 36 && xanual_interest > 0 && xanual_interest <= 500){
                                input_layout_months.setErrorEnabled(false);
                                input_layout_anual_interest.setErrorEnabled(false);
                                input_layout_client.setErrorEnabled(false);
                                postSale();
                            }else{
                                if(cmonths == 0 || cmonths > 36){
                                    input_layout_months.setError("Se requiere un valor entre 1 y 36 cuotas.");
                                }else{
                                    input_layout_months.setErrorEnabled(false);
                                }
                                if(xanual_interest == 0 || xanual_interest > 500){
                                    input_layout_anual_interest.setError("Se requiere un valor entre 1 y 500 porciento.");
                                }else{
                                    input_layout_anual_interest.setErrorEnabled(false);
                                }
                            }
                        }else{
                            if(months.getText().toString().equals("")){
                                input_layout_months.setError("Este campo es requerido.");
                            }
                            if(anual_interest.getText().toString().equals("")) {
                                input_layout_anual_interest.setError("Este campo es requerido.");
                            }
                            if(initial_date.equals("")){
                                Toast.makeText(CreateSaleActivity.this,"Falta seleccionar la fecha inicial de cobro.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else{
                        input_layout_client.setErrorEnabled(false);
                        postSale();
                    }
                }else{
                    if(customer == null){
                        input_layout_client.setError("Falta seleccionar un cliente");
                    }
                    if(cart.size() == 0){
                        Toast.makeText(CreateSaleActivity.this,"Falta agregar productos a la venta",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        watch_accounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreateSaleActivity.this, AccountActivity.class);
                Bundle b = new Bundle();
                b.putString("code", bnav_salesFragment.Sales.get(sale_position).getCode());
                i.putExtras(b);
                startActivity(i);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
        updateTotals();
        if(hasBeenReScheduled){
            finish();
        }
        if(hasBeenPayed){
            create_sale.setVisibility(View.GONE);
            add_product.setVisibility(View.GONE);
            client.setEnabled(false);
            months.setEnabled(false);
            anual_interest.setEnabled(false);
            search_customer.setEnabled(false);
            bnav_salesFragment.Sales.get(sale_position).getAccounts().clear();
            bnav_salesFragment.Sales.get(sale_position).getAccounts().addAll(accounts);
            hasBeenPayed = false;
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
        if(method.equals("GET") && flag.equals("customer")){
            parseCustomerResult(result);
        }
        if(method.equals("POST") && flag.equals("create_customer")){
            Log.e("result ", result);
            parseCreateCustomerResult(result);
        }
        if(method.equals("PATCH") && flag.equals("create_customer")){
            Log.e("result patch ", result);
            parseCreateCustomerResult(result);
        }
    }
    private void getClient(String nit){
        HttpTask task = new HttpTask(null, "GET","customer", access_token, this);
        task.execute(Globals.API_URL + "/customers?nit="+nit);

    }
    private void postSale() {
        Map<String, String> postData = new HashMap<>();
        if(sale_position != -1){
            postData.put("_method", "PATCH");
        }
        postData.put("customer_id", customer.getCustomer_id()+"");
        postData.put("sale_type_id", saleType+"");
        if(saleType == 2){
            postData.put("months", months.getText().toString());
            postData.put("anual_interest", anual_interest.getText().toString());
        }
        postData.put("initial_date", initial_date);
        try {
            JSONArray jsonArray = new JSONArray();
            for(int i=0; i<cart.size(); i++){
                JSONObject o = new JSONObject("{ \"book_type_id\": "+cart.get(i).getBookType().getType_id()+", \"quantity\": "+cart.get(i).getQuantity()+", \"selling_price\": "+cart.get(i).getSelling_price()+"}");
                jsonArray.put(o);
            }
            postData.put("outflows", jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("map", postData.toString());
        if(sale_position != -1) {
            HttpTask task = new HttpTask(postData, "PATCH","create_customer", access_token, this);
            task.execute(Globals.API_URL + "/sales/"+bnav_salesFragment.Sales.get(sale_position).getSale_id());
        }else{
            HttpTask task = new HttpTask(postData, "POST","create_customer", access_token, this);
            task.execute(Globals.API_URL + "/sales");
        }
    }
    private void getSale(int position) {
        customer = bnav_salesFragment.Sales.get(position).getCustomer();
        initial_date = bnav_salesFragment.Sales.get(position).getAccounts().get(0).getPayment_date();
        cart.clear();
        cart.addAll(bnav_salesFragment.Sales.get(position).getOutflows());
        mAdapter.notifyDataSetChanged();
        //set client data
        client.setText(bnav_salesFragment.Sales.get(position).getCustomer().getNit()+" ("+bnav_salesFragment.Sales.get(position).getCustomer().getSurname()+")");
        months.setText(bnav_salesFragment.Sales.get(position).getMonths()+"");
        anual_interest.setText((bnav_salesFragment.Sales.get(position).getAnual_interest()*100)+"");
        select_date.setText(getResources().getString(R.string.form_sale_date)+": "+bnav_salesFragment.Sales.get(position).getAccounts().get(0).getPayment_date());
        //control accounts
        accounts = new ArrayList<>(bnav_salesFragment.Sales.get(sale_position).getAccounts());
        if(bnav_salesFragment.Sales.get(sale_position).hasAtLeastOnePaymentCompleted()){
            create_sale.setVisibility(View.GONE);
            add_product.setVisibility(View.GONE);
            client.setEnabled(false);
            months.setEnabled(false);
            anual_interest.setEnabled(false);
            search_customer.setEnabled(false);
        }

    }
    private void parseCustomerResult(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = new JSONArray(jsonObject.get("data").toString());
            if(jsonObject.get("data").toString().equals("[]")){
                input_layout_client.setError("No hay cliente con este NIT.");
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                Iterator<String> keyItr = jsonArray.getJSONObject(i).keys();
                //hashmap
                Map<String, String> map = new HashMap<>();
                while (keyItr.hasNext()) {
                    String key = keyItr.next();
                    map.put(key, jsonArray.getJSONObject(i).getString(key));
                }
                customer = new Customer(
                        map.get("surname"),
                        map.get("nit"),
                        Integer.parseInt(map.get("customer_id"))
                );

                client.clearFocus();
                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                client.setText(map.get("nit")+" ("+map.get("surname")+")");
                input_layout_client.setErrorEnabled(false);
                break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("error ", e.getMessage());
        }
    }
    private void parseCreateCustomerResult(String result) {
        try {
            JSONArray jsonArray = new JSONArray(result);
            JSONObject jsonObject = new JSONObject(jsonArray.get(0).toString());
            Toast.makeText(CreateSaleActivity.this,jsonObject.get("message").toString(),Toast.LENGTH_SHORT).show();
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void updateTotals() {
        Double total = 0.0;
        for(int i=0; i<cart.size();i++){
            total += (cart.get(i).getQuantity()*cart.get(i).getSelling_price());
        }
        total_sale.setText("Total: "+total+" Bs.");
    }
    private void outflowDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateSaleActivity.this);
        if(cart.get(position).getBookType().getBook() == null){
            builder.setTitle(cart.get(position).getBook().getTitle());
        }else{
            builder.setTitle(cart.get(position).getBookType().getBook().getTitle());
        }
        View view= layoutInflater.inflate(R.layout.form_sale_product, null);
        builder.setView(view);
        form_book_type= (TextView) view.findViewById(R.id.form_book_type);
        form_quantity = (EditText) view.findViewById(R.id.quantity);
        form_selling_price = (EditText) view.findViewById(R.id.selling_price);
        input_layout_quantity = (TextInputLayout) view.findViewById(R.id.input_layout_quantity);
        input_layout_selling_price = (TextInputLayout) view.findViewById(R.id.input_layout_selling_price);
        form_quantity.setText(cart.get(position).getQuantity()+"");
        form_selling_price.setText(cart.get(position).getSelling_price()+"");
        form_book_type.setText(cart.get(position).getBookType().getType());
        builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(form_quantity.getText().toString().length() > 0 && form_selling_price.getText().toString().length() >0){
                    int q = Integer.parseInt(form_quantity.getText().toString());
                    Double ai = Double.parseDouble(form_selling_price.getText().toString());
                    if( q > 0 && q <= 1000 && ai > 0 && ai <= 100000){
                        input_layout_quantity.setErrorEnabled(false);
                        input_layout_selling_price.setErrorEnabled(false);
                        cart.get(position).setQuantity(Integer.parseInt(form_quantity.getText().toString()));
                        cart.get(position).setSelling_price(Double.parseDouble(form_selling_price.getText().toString()));
                        updateTotals();
                        mAdapter.notifyDataSetChanged();
                    }else{
                        if(q==0 || q > 1000){
                            input_layout_quantity.setError("Se requiere un valor entre 1 y 1000 unidades.");
                        }else{ input_layout_quantity.setErrorEnabled(false); }
                        if(q==0 || q > 1000000){
                            input_layout_selling_price.setError("Se requiere un valor entre 1 y 1000000.");
                        }else{ input_layout_selling_price.setErrorEnabled(false); }
                    }
                }else{
                    if(form_quantity.getText().toString().equals("")){
                        input_layout_quantity.setError("Este campo es requerido.");
                    }
                    if(form_selling_price.getText().toString().equals("")){
                        input_layout_selling_price.setError("Este campo es requerido.");
                    }
                }
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
    public class adapter extends RecyclerView.Adapter<CreateSaleActivity.adapter.ViewHolder> {
        private ArrayList<Outflow> outflows;
        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            TextView title, type, quantity, selling_price;
            FancyButton edit, delete;
            ViewHolder(View view) {
                super(view);
                title = view.findViewById(R.id.title);
                type = view.findViewById(R.id.type);
                quantity = view.findViewById(R.id.quantity);
                selling_price = view.findViewById(R.id.selling_price);
                edit = (FancyButton) view.findViewById(R.id.edit);
                delete = (FancyButton) view.findViewById(R.id.delete);

            }
        }
        // Provide a suitable constructor (depends on the kind of dataset)
        adapter(ArrayList<Outflow> outflows) {
            this.outflows = outflows;
        }
        // Create new views (invoked by the layout manager)
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sale_product, parent, false);
            // set the view's size, margins, paddings and layout parameters
            return new ViewHolder(v);
        }
        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            holder.title.setText(outflows.get(position).getBook().getTitle());
            holder.type.setText(outflows.get(position).getBookType().getType());
            holder.quantity.setText("Cantidad: "+outflows.get(position).getQuantity());
            holder.selling_price.setText(outflows.get(position).getSelling_price()+" Bs.");
            if(bnav_salesFragment.Sales.get(sale_position).hasAtLeastOnePaymentCompleted()){
                holder.edit.setVisibility(View.GONE);
                holder.delete.setVisibility(View.GONE);
            }
            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    outflowDialog(position);
                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateSaleActivity.this);
                    builder.setTitle("Está segur@ de eliminar este registro?");
                    builder.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            outflows.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, outflows.size());
                            updateTotals();
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
            });
        }
        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return outflows.size();
        }
    }
}
