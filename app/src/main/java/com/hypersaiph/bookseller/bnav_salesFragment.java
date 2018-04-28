package com.hypersaiph.bookseller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.hypersaiph.bookseller.HttpClient.HttpTask;
import com.hypersaiph.bookseller.HttpClient.ResponseInterface;
import com.hypersaiph.bookseller.Models.Account;
import com.hypersaiph.bookseller.Models.Book;
import com.hypersaiph.bookseller.Models.BookType;
import com.hypersaiph.bookseller.Models.Customer;
import com.hypersaiph.bookseller.Models.Outflow;
import com.hypersaiph.bookseller.Models.Sale;
import com.hypersaiph.bookseller.Models.SaleType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class bnav_salesFragment extends Fragment implements ResponseInterface {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public static ArrayList<Sale> Sales = new ArrayList<>();
    private FloatingActionButton createSale;
    private Toolbar mActionBarToolbar;
    public bnav_salesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bnav_sales, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActionBarToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mActionBarToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Ventas");
        createSale = (FloatingActionButton) view.findViewById(R.id.create_sale);
        createSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CreateSaleActivity.class);
                startActivity(i);
            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        mAdapter = new adapter(Sales, getActivity());
        recyclerView.setAdapter(mAdapter);
    }
    @Override
    public void requestFinished(String result, String method, String flag) {
        if(method.equals("GET")){
            Sales.clear();
            parseResult(result);
            mAdapter.notifyDataSetChanged();
        }
    }
    public void getSales(){
        SharedPreferences sharedPref = Objects.requireNonNull(this.getActivity()).getSharedPreferences(Globals.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String access_token = sharedPref.getString("access_token",null);
        HttpTask task = new HttpTask(null, "GET", access_token, this);
        task.execute(Globals.API_URL + "/sales"+"?include=accounts,outflows.type.book,customer");
    }
    @Override
    public void onResume() {
        super.onResume();
        getSales();
    }
    public void parseResult(String result){
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = new JSONArray(jsonObject.get("data").toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                ArrayList<Account> accounts = new ArrayList<Account>();
                ArrayList<Outflow> outflows = new ArrayList<Outflow>();
                Customer customer = null;
                Iterator<String> keyItr = jsonArray.getJSONObject(i).keys();
                //hashmap
                Map<String, String> map = new HashMap<>();while(keyItr.hasNext()) { String key = keyItr.next();map.put(key, jsonArray.getJSONObject(i).getString(key)); }
                // accounts
                JSONObject accountsJO = new JSONObject(map.get("accounts"));
                JSONArray accountsJArray = new JSONArray(accountsJO.get("data").toString());
                for (int index = 0; index < accountsJArray.length(); index++) {
                    accounts.add(new Account(
                            accountsJArray.getJSONObject(index).get("code").toString(),
                            Double.parseDouble(accountsJArray.getJSONObject(index).get("amount").toString()),
                            Double.parseDouble(accountsJArray.getJSONObject(index).get("penalty").toString()),
                            accountsJArray.getJSONObject(index).get("payment_date").toString(),
                            accountsJArray.getJSONObject(index).get("limit_payment_date").toString(),
                            accountsJArray.getJSONObject(index).get("is_active").toString().equals("1"),
                            Integer.parseInt(accountsJArray.getJSONObject(index).get("account_id").toString())
                    ));
                }
                //customer
                JSONObject customerMap = new JSONObject(map.get("customer"));
                if(!customerMap.get("data").toString().equals("[]")){
                    JSONObject customerJO = new JSONObject(customerMap.get("data").toString());
                    customer = new Customer(
                            customerJO.getString("name"),
                            customerJO.getString("surname"),
                            customerJO.getString("nit"),
                            customerJO.getString("email"),
                            customerJO.getString("address"),
                            customerJO.getString("phone"),
                            customerJO.getString("note"),
                            Double.parseDouble(customerJO.getString("latitude")),
                            Double.parseDouble(customerJO.getString("longitude")),
                            Integer.parseInt(customerJO.getString("customer_id")),
                            Integer.parseInt(customerJO.getString("created_by"))
                    );
                }
                // outflows
                JSONObject outflowsJO = new JSONObject(map.get("outflows"));
                JSONArray outflowsJArray = new JSONArray(outflowsJO.get("data").toString());
                for (int index = 0; index < outflowsJArray.length(); index++) {
                    Iterator<String> iterator = outflowsJArray.getJSONObject(index).keys();
                    //hashmap
                    Map<String, String> outflowsMap = new HashMap<>();while(iterator.hasNext()) { String key = iterator.next();outflowsMap.put(key, outflowsJArray.getJSONObject(index).getString(key)); }
                    //type
                    JSONObject typeMap = new JSONObject(outflowsMap.get("type"));
                    JSONObject typeJO = new JSONObject(typeMap.get("data").toString());


                    JSONObject bookMap = new JSONObject(typeJO.get("book").toString());
                    JSONObject bookJO = new JSONObject(bookMap.get("data").toString());
                    Book book = new Book(
                            bookJO.getString("title"),
                            Integer.parseInt(bookJO.getString("book_id"))
                    );
                    //type
                    BookType bookType = new BookType(
                            typeJO.getString("type"),
                            Integer.parseInt(typeJO.getString("book_type_id")),
                            typeJO.getString("isbn10"),
                            typeJO.getString("isbn13"),
                            typeJO.getString("serial_cd")
                    );
                    outflows.add(new Outflow(
                            Integer.parseInt(outflowsMap.get("quantity")),
                            Double.parseDouble(outflowsMap.get("selling_price")),
                            bookType,
                            book
                    ));
                }
                SaleType saleType = new SaleType(
                        map.get("sale_type"),
                        Integer.parseInt(map.get("sale_type_id"))
                );
                Sales.add(new Sale(
                        map.get("code"),
                        map.get("is_billed").equals("1"),
                        Integer.parseInt(map.get("months")),
                        Double.parseDouble(map.get("anual_interest")),
                        Integer.parseInt(map.get("sale_id")),
                        saleType,
                        customer,
                        accounts,
                        outflows
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("error ", e.getMessage());
        }
    }
    public class adapter extends RecyclerView.Adapter<bnav_salesFragment.adapter.ViewHolder> {
        private ArrayList<Sale> sales = new ArrayList<>();
        private Context context;
        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            TextView code, type, total, payment_number, payment_togo, payment_amount, next_payment, products, status;
            LinearLayout type_credit;
            Button open;
            ViewHolder(View view) {
                super(view);
                code = (TextView) view.findViewById(R.id.code);
                type = (TextView) view.findViewById(R.id.type);
                total = (TextView) view.findViewById(R.id.total);
                payment_number = (TextView) view.findViewById(R.id.payment_number);
                payment_togo = (TextView) view.findViewById(R.id.payment_togo);
                payment_amount = (TextView) view.findViewById(R.id.payment_amount);
                next_payment = (TextView) view.findViewById(R.id.next_payment);
                products = (TextView) view.findViewById(R.id.products);
                status = (TextView) view.findViewById(R.id.status);
                open = (Button) view.findViewById(R.id.open);
                type_credit = (LinearLayout) view.findViewById(R.id.type_credit);
            }
        }
        // Provide a suitable constructor (depends on the kind of dataset)
        adapter(ArrayList<Sale> sales, Context context) {
            this.sales = sales;
            this.context = context;
        }
        // Create new views (invoked by the layout manager)
        @NonNull
        @Override
        public bnav_salesFragment.adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sale, parent, false);
            // set the view's size, margins, paddings and layout parameters
            return new ViewHolder(v);
        }
        // Replace the contents of a view (invoked by the layout manager)
        @SuppressLint({"SetTextI18n", "ResourceAsColor"})
        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            holder.code.setText(sales.get(position).getCode());
            holder.type.setText(sales.get(position).getSaleType().getType());
            holder.total.setText(sales.get(position).getTotal().toString()+" Bs.");
            if(sales.get(position).getStatus()){
                holder.status.setText("ACTIVO");
                holder.open.setVisibility(View.VISIBLE);
            }else{
                holder.status.setText("CANCELADO");
                holder.open.setVisibility(View.GONE);
            }
            if(sales.get(position).hasInterest()){
                holder.total.setTextColor(getResources().getColor(R.color.wrong));
            }else{
                holder.total.setTextColor(getResources().getColor(R.color.okay));
            }
            //accounts
            if(sales.get(position).getSaleType().getSale_type_id() == 2){
                holder.type_credit.setVisibility(View.VISIBLE);
                holder.payment_number.setText("Cuentas: "+sales.get(position).getAccounts().size());
                holder.payment_togo.setText(sales.get(position).getRemainingAccounts());
                holder.payment_amount.setText(sales.get(position).getAmountPerAccount());
            }else{
                holder.type_credit.setVisibility(View.GONE);
            }

            holder.next_payment.setText(sales.get(position).getNextPaymentDate());
            holder.products.setText("Productos: "+sales.get(position).getProductQuantity());
            holder.open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), CreateSaleActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("position", position);
                    b.putString("code", sales.get(position).getCode());
                    i.putExtras(b);
                    startActivity(i);
                }
            });
        }
        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return sales.size();
        }
    }
}
