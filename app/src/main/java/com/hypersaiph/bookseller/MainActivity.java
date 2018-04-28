package com.hypersaiph.bookseller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseMessaging.getInstance().subscribeToTopic("books");
        FirebaseMessaging.getInstance().subscribeToTopic("book_types");
        FirebaseMessaging.getInstance().subscribeToTopic("inflows");
        FirebaseMessaging.getInstance().subscribeToTopic("accounts");
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        //Picasso.get().load(R.drawable.bg).into(imageView);
        SharedPreferences sharedPref = this.getSharedPreferences(Globals.SHARED_PREFERENCES,Context.MODE_PRIVATE);
        String access_token = sharedPref.getString("access_token",null);
        //String access_token = null;
        if(access_token != null){
            //Toast.makeText(this, access_token, Toast.LENGTH_SHORT).show();
            Log.e("access_token", access_token);
            Intent i = new Intent(MainActivity.this, BottomNavigationMenu.class);
            startActivity(i);
            finish();
        }else{
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
            /*final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 150);*/
        }
    }
}
