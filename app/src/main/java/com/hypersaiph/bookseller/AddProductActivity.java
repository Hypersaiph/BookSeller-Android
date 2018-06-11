package com.hypersaiph.bookseller;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.hypersaiph.bookseller.HttpClient.HttpTask;
import com.hypersaiph.bookseller.HttpClient.ResponseInterface;
import com.hypersaiph.bookseller.Models.Book;
import com.hypersaiph.bookseller.Models.BookType;
import com.hypersaiph.bookseller.Models.Outflow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity implements ResponseInterface {
    private Toolbar toolbar;
    private ArrayList<BookType> BookTypes = new ArrayList<BookType>();
    private RadioGroup radioGroup;
    private TextInputLayout input_layout_quantity, input_layout_selling_price;
    private EditText product_name, quantity, selling_price;
    private TextView form_book_type;
    private Button search_scanner;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private LayoutInflater layoutInflater;
    private int book_type;
    private String book_title, book_serial, access_token;
    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                //Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                searchBooks(book_type, book_title, result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        //var inits
        SharedPreferences sharedPref = this.getSharedPreferences(Globals.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        access_token = sharedPref.getString("access_token",null);
        book_type = 1;
        book_title = "";
        book_serial = "";
        layoutInflater = getLayoutInflater();
        //parse UI
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        product_name = (EditText) findViewById(R.id.product_name);
        search_scanner = (Button) findViewById(R.id.search_scanner);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        mAdapter = new adapter(BookTypes);
        recyclerView.setAdapter(mAdapter);
        setSupportActionBar(toolbar);
        //Your toolbar is now an action bar and you can use it like you always do, for example:
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Agregar Producto a la Venta");
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.book_type_1:
                        book_type = 1;
                        searchBooks(book_type, book_title, "");
                        break;
                    case R.id.book_type_2:
                        book_type = 2;
                        searchBooks(book_type, book_title, "");
                        break;
                    case R.id.book_type_3:
                        book_type = 3;
                        searchBooks(book_type, book_title, "");
                        break;
                }
            }
        });
        search_scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activate zxing

                new IntentIntegrator(AddProductActivity.this).initiateScan();
                //searchBooks(book_type, book_title, "1925483592");
            }
        });
        product_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    book_title = s.toString();
                    searchBooks(book_type, book_title, "");
                }
            }
        });
        searchBooks(book_type, book_title, "");
    }

    private void searchBooks(int book_type, String book_title, String book_serial) {
        HttpTask task = new HttpTask(null, "GET","book", access_token, this);
        String companion = "";
        if(book_serial.equals("")){
            companion = "?include=book"+"&book_type="+book_type+"&title="+book_title;
        }else{
            companion = "?include=book"+"&serial="+book_serial;
        }
        task.execute(Globals.API_URL + "/type"+companion);
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
        if(method.equals("GET") && flag.equals("book")){
            BookTypes.clear();
            parseResult(result);
            mAdapter.notifyDataSetChanged();
        }
    }
    public void parseResult(String result){
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = new JSONArray(jsonObject.get("data").toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                Book book;
                Iterator<String> keyItr = jsonArray.getJSONObject(i).keys();
                //hashmap
                Map<String, String> map = new HashMap<>();while(keyItr.hasNext()) { String key = keyItr.next();map.put(key, jsonArray.getJSONObject(i).getString(key)); }
                //get book
                JSONObject bookMap = new JSONObject(map.get("book"));
                JSONObject bookJO = new JSONObject(bookMap.get("data").toString());

                BookTypes.add(new BookType(
                        map.get("type"),
                        Integer.parseInt(map.get("book_type_id")),
                        Double.parseDouble(map.get("price")),
                        map.get("isbn10"),
                        map.get("isbn13"),
                        map.get("serial_cd"),
                        //book object
                        bookJO.get("title").toString(),
                        Integer.parseInt(bookJO.get("book_id").toString()),
                        bookJO.get("language").toString()
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("error ", e.getMessage());
        }
    }
    private void statusDialog(final BookType bookType) {
        //Toast.makeText(this, bookType.getType_id()+"", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this);
        builder.setTitle(bookType.getTitle());
        View view= layoutInflater.inflate(R.layout.form_sale_product, null);
        builder.setView(view);
        form_book_type= (TextView) view.findViewById(R.id.form_book_type);
        quantity = (EditText) view.findViewById(R.id.quantity);
        selling_price = (EditText) view.findViewById(R.id.selling_price);
        input_layout_quantity = (TextInputLayout) view.findViewById(R.id.input_layout_quantity);
        input_layout_selling_price = (TextInputLayout) view.findViewById(R.id.input_layout_selling_price);
        selling_price.setText(bookType.getPrice().toString());
        form_book_type.setText(bookType.getType());
        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(quantity.getText().toString().length() > 0 && selling_price.getText().toString().length() >0){
                    int q = Integer.parseInt(quantity.getText().toString());
                    Double ai = Double.parseDouble(selling_price.getText().toString());
                    if( q > 0 && q <= 1000 && ai > 0 && ai <= 100000){
                        input_layout_quantity.setErrorEnabled(false);
                        input_layout_selling_price.setErrorEnabled(false);
                        Outflow outflow = new Outflow(
                                Integer.parseInt(quantity.getText().toString()),
                                Double.parseDouble(selling_price.getText().toString()),
                                bookType
                        );
                        CreateSaleActivity.cart.add(outflow);
                        finish();
                    }else{
                        if(q==0 || q > 1000){
                            input_layout_quantity.setError("Se requiere un valor entre 1 y 1000 unidades.");
                        }else{ input_layout_quantity.setErrorEnabled(false); }
                        if(q==0 || q > 1000000){
                            input_layout_selling_price.setError("Se requiere un valor entre 1 y 1000000.");
                        }else{ input_layout_selling_price.setErrorEnabled(false); }
                    }
                }else{
                    if(quantity.getText().toString().equals("")){
                        input_layout_quantity.setError("Este campo es requerido.");
                    }
                    if(selling_price.getText().toString().equals("")){
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
    public class adapter extends RecyclerView.Adapter<AddProductActivity.adapter.ViewHolder> {
        private ArrayList<BookType> bookTypes;
        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            TextView title, type, language, price;
            Button select;
            ViewHolder(View view) {
                super(view);
                title = view.findViewById(R.id.title);
                type = view.findViewById(R.id.type);
                language = view.findViewById(R.id.language);
                price = view.findViewById(R.id.price);
                select = view.findViewById(R.id.select);
            }
        }
        // Provide a suitable constructor (depends on the kind of dataset)
        adapter(ArrayList<BookType> bookTypes) {
            this.bookTypes = bookTypes;
        }
        // Create new views (invoked by the layout manager)
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_product, parent, false);
            // set the view's size, margins, paddings and layout parameters
            return new ViewHolder(v);
        }
        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            holder.title.setText(bookTypes.get(position).getTitle());
            holder.type.setText(bookTypes.get(position).getType());
            //quantity
            holder.language.setText(bookTypes.get(position).getLanguage());
            //quantity
            holder.price.setText(bookTypes.get(position).getPrice().toString()+" Bs.");
            holder.price.setTextColor(getResources().getColor(R.color.okay));
            holder.select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //open quantity form
                    statusDialog(bookTypes.get(position));
                }
            });
        }
        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return bookTypes.size();
        }
    }
}
