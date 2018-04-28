package com.hypersaiph.bookseller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.hypersaiph.bookseller.HttpClient.HttpTask;
import com.hypersaiph.bookseller.HttpClient.ResponseInterface;
import com.hypersaiph.bookseller.Models.Book;
import com.hypersaiph.bookseller.Models.BookType;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import mehdi.sakout.fancybuttons.FancyButton;

public class bnav_productsFragment extends Fragment implements ResponseInterface {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Book> Books = new ArrayList<>();

    public bnav_productsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bnav_products, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        mAdapter = new adapter(Books, getActivity());
        recyclerView.setAdapter(mAdapter);
        getProducts();
    }
    public void getProducts(){
        SharedPreferences sharedPref = Objects.requireNonNull(this.getActivity()).getSharedPreferences(Globals.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String access_token = sharedPref.getString("access_token",null);
        HttpTask task = new HttpTask(null, "GET", access_token, this);
        task.execute(Globals.API_URL + "/books"+"?include=authors,genres,types.publishers");
    }

    @Override
    public void requestFinished(String result, String method, String flag) {
        if(method.equals("GET")){
            Books.clear();
            parseResult(result);
            mAdapter.notifyDataSetChanged();
        }
    }
    public void parseResult(String result){
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = new JSONArray(jsonObject.get("data").toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                ArrayList<String> authors = new ArrayList<String>();
                ArrayList<String> genres = new ArrayList<String>();
                ArrayList<BookType> types = new ArrayList<BookType>();

                Iterator<String> keyItr = jsonArray.getJSONObject(i).keys();
                Map<String, String> map = new HashMap<>();
                while(keyItr.hasNext()) {
                    String key = keyItr.next();
                    map.put(key, jsonArray.getJSONObject(i).getString(key));
                }
                //Log.e("book "+i, map.get("title"));
                //get authors
                JSONObject authorsObject = new JSONObject(map.get("authors"));
                JSONArray jsonArrayAuthors = new JSONArray(authorsObject.get("data").toString());
                for (int index = 0; index < jsonArrayAuthors.length(); index++) {
                    authors.add(jsonArrayAuthors.getJSONObject(index).get("name").toString());
                }
                //get genres
                JSONObject genresObject = new JSONObject(map.get("genres"));
                JSONArray jsonArrayGenres = new JSONArray(genresObject.get("data").toString());
                for (int index = 0; index < jsonArrayGenres.length(); index++) {
                        genres.add(jsonArrayGenres.getJSONObject(index).get("genre").toString());
                }
                //get types
                JSONObject bookTypesJson = new JSONObject(map.get("types"));
                JSONArray jsonArrayBookTypes = new JSONArray(bookTypesJson.get("data").toString());
                for (int index = 0; index < jsonArrayBookTypes.length(); index++) {
                    ArrayList<String> publishers = new ArrayList<String>();


                    Iterator<String> iterator = jsonArrayBookTypes.getJSONObject(index).keys();
                    Map<String, String> bookTypeMap = new HashMap<>();
                    while(iterator.hasNext()) {
                        String key = iterator.next();
                        bookTypeMap.put(key, jsonArrayBookTypes.getJSONObject(index).getString(key));
                    }
                    //get publishers
                    JSONObject publishersObject = new JSONObject(bookTypeMap.get("publishers"));
                    JSONArray jsonArrayPublishers = new JSONArray(publishersObject.get("data").toString());
                    for (int key = 0; key < jsonArrayPublishers.length(); key++) {
                        String publisher = jsonArrayPublishers.getJSONObject(key).get("publisher").toString();
                        publishers.add(publisher);
                    }
                    //Log.e("price",  bookTypeMap.get("price"));
                    BookType bookType = new BookType(
                            bookTypeMap.get("type"),
                            Integer.parseInt(bookTypeMap.get("book_type_id")),
                            bookTypeMap.get("price").equals("null")?0.0 : Double.parseDouble(bookTypeMap.get("price")),
                            bookTypeMap.get("pages").equals("null")?0 : Integer.parseInt(bookTypeMap.get("pages")),
                            bookTypeMap.get("isbn10"),
                            bookTypeMap.get("isbn13"),
                            bookTypeMap.get("serial_cd"),
                            bookTypeMap.get("duration"),
                            bookTypeMap.get("weight").equals("null")?0.0 : Double.parseDouble(bookTypeMap.get("weight")),
                            bookTypeMap.get("width").equals("null")?0.0 : Double.parseDouble(bookTypeMap.get("width")),
                            bookTypeMap.get("height").equals("null")?0.0 : Double.parseDouble(bookTypeMap.get("height")),
                            bookTypeMap.get("depth").equals("null")?0.0 : Double.parseDouble(bookTypeMap.get("depth")),
                            publishers
                            );
                    types.add(bookType);
                }
                Book book = new Book(
                        map.get("title"),
                        map.get("description"),
                        Integer.parseInt(map.get("book_id")),
                        Integer.parseInt(map.get("edition")),
                        map.get("publication_date"),
                        map.get("cover_image"),
                        authors,
                        genres,
                        types
                );
                Books.add(book);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("error ", e.getMessage());
        }
    }
    public class adapter extends RecyclerView.Adapter<adapter.ViewHolder> {
        private ArrayList<Book> books = new ArrayList<>();
        private Context context;
        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            ImageView cover;
            TextView title, authors, genres, publishers, types;
            FancyButton share_btn;
            ViewHolder(View view) {
                super(view);
                title = (TextView) view.findViewById(R.id.title);
                authors = (TextView) view.findViewById(R.id.authors);
                genres = (TextView) view.findViewById(R.id.genres);
                publishers = (TextView) view.findViewById(R.id.publishers);
                types = (TextView) view.findViewById(R.id.types);
                cover = (ImageView) view.findViewById(R.id.cover);
                share_btn = (FancyButton) view.findViewById(R.id.share_btn);
            }
        }
        // Provide a suitable constructor (depends on the kind of dataset)
        public adapter(ArrayList<Book> books, Context context) {
            this.books = books;
            this.context = context;
        }
        // Create new views (invoked by the layout manager)
        @Override
        public adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }
        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            Picasso.get().load(Globals.API_BOOK_RESOURCE+books.get(position).getCover_image()).into(holder.cover);
            holder.share_btn.setIconResource(R.mipmap.ic_share_black_36dp);
            holder.title.setText(limit(books.get(position).getTitle(), 40));
            holder.authors.setText(limit(books.get(position).getAuthorsString(),29));
            holder.genres.setText(limit(books.get(position).getGenresString(),29));
            holder.publishers.setText(limit(books.get(position).getPublishersString(),27));
            holder.types.setText(books.get(position).getTypesString());
            holder.share_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, ""+books.get(position).getBook_id(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return books.size();
        }
    }
    //limit string
    public String limit(String string, int length){
        int limit = Math.abs(length);
        if(string.length() > limit){
            string = string.substring(0, length)+"...";
        }
        return string;
    }
}
