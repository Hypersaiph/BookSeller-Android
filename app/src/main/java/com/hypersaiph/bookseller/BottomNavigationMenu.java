package com.hypersaiph.bookseller;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import com.google.android.gms.maps.MapView;

public class BottomNavigationMenu extends AppCompatActivity {

    FragmentManager fragmentManager;
    int selected_item;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_sales:
                    if(selected_item != item.getItemId()){
                        selected_item = item.getItemId();
                        switchToSales();
                    }
                    return true;
                case R.id.navigation_products:
                    if(selected_item != item.getItemId()) {
                        selected_item = item.getItemId();
                        switchToProducts();
                    }
                    return true;
                case R.id.navigation_customers:
                    if(selected_item != item.getItemId()) {
                        selected_item = item.getItemId();
                        switchToCustomers();
                    }
                    return true;
                case R.id.navigation_notifications:
                    if(selected_item != item.getItemId()) {
                        selected_item = item.getItemId();
                        switchToNotifications();
                    }
                    return true;
                case R.id.navigation_profile:
                    if(selected_item != item.getItemId()) {
                        selected_item = item.getItemId();
                        switchToProfile();
                    }
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation_menu);
        try{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        MapView mv = new MapView(getApplicationContext());
                        mv.onCreate(null);
                        mv.onPause();
                        mv.onDestroy();
                    }catch (Exception ignored){

                    }
                }
            }).start();
        }catch (Exception e){
            Log.e("Google Maps","thread not supported");
        }
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_sales);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayout, new bnav_salesFragment()).commit();
        selected_item = navigation.getSelectedItemId();
    }
    public void switchToSales(){
        fragmentManager.beginTransaction().replace(R.id.frameLayout, new bnav_salesFragment()).commit();
    }
    public void switchToProducts(){
        fragmentManager.beginTransaction().replace(R.id.frameLayout, new bnav_productsFragment()).commit();
    }
    public void switchToCustomers(){
        fragmentManager.beginTransaction().replace(R.id.frameLayout, new bnav_customersFragment()).commit();
    }
    public void switchToNotifications(){
        fragmentManager.beginTransaction().replace(R.id.frameLayout, new bnav_notificationsFragment()).commit();
    }
    public void switchToProfile(){
        fragmentManager.beginTransaction().replace(R.id.frameLayout, new bnav_profileFragment()).commit();
    }
}
