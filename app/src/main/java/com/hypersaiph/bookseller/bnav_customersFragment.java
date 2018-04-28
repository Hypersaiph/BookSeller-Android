package com.hypersaiph.bookseller;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.hypersaiph.bookseller.HttpClient.HttpTask;
import com.hypersaiph.bookseller.HttpClient.ResponseInterface;
import com.hypersaiph.bookseller.Models.Customer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

public class bnav_customersFragment extends Fragment implements GoogleMap.OnMyLocationButtonClickListener, OnMapReadyCallback, ResponseInterface {

    private GoogleMap mMap;
    private LatLng currentPosition;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private Boolean mRequestingLocationUpdates;
    private ArrayList<Customer> Customers = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private FloatingActionButton add_customer;
    private EditText form_name,form_surname,form_nit,form_email,form_address,form_phone,form_note;
    private TextInputLayout input_layout_form_surname, input_layout_form_nit;
    private String access_token;

    public bnav_customersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bnav_customers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        add_customer = (FloatingActionButton) view.findViewById(R.id.add_customer);
        SharedPreferences sharedPref = Objects.requireNonNull(this.getActivity()).getSharedPreferences(Globals.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        access_token = sharedPref.getString("access_token",null);
        mRequestingLocationUpdates = true;
        createLocationRequest();
        layoutInflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment map = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);
        getCustomers();

        LocationManager lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage(getActivity().getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(getActivity().getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getActivity().startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(getActivity().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                }
            });
            dialog.show();
        }
        add_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomerDialog();
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-16.505722, -68.119761);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Kupini"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14.0f));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setInfoWindowAdapter(new infoAdapter());
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                int index = Integer.parseInt(marker.getSnippet());
                //edit
                openEditCustomerDialog(index);
            }
        });
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }
    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                //Log.e("location",location.toString());
                                if(mMap != null){
                                    currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                                }
                            }
                        }
                    });
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        // Update UI with location data
                        // ...
                        //Log.e("location data",location.toString());
                        if(mMap != null){
                            currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                        }
                    }
                };
            };
        }else{
            Log.e("error","faltan permisos");
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }
    @Override
    public void requestFinished(String result, String method, String flag) {
        if (method.equals("GET")) {
            Customers.clear();
            mMap.clear();
            parseResult(result);
            setCustomerlocation();
        }
        if(method.equals("POST") && flag.equals("create_customer")){
            parseCustomerResult(result);
        }
        if(method.equals("PATCH") && flag.equals("patch_customer")){
            parseCustomerResult(result);
        }
    }
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
        }
    }
    public void getCustomers(){
        HttpTask task = new HttpTask(null, "GET", access_token, this);
        task.execute(Globals.API_URL + "/customers");
    }
    private void setCustomerlocation() {
        for(int i=0; i<Customers.size();i++){
            LatLng position = new LatLng(Customers.get(i).getLatitude(), Customers.get(i).getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .snippet(i+"")
                    .title(Customers.get(i).getName()+" "+Customers.get(i).getSurname()));
        }
    }
    public void postCustomer(String name, String surname, String nit, String email, String address, String phone, String note){
        //currentPosition
        Map<String, String> postData = new HashMap<>();
        postData.put("name", name);
        postData.put("surname", surname);
        postData.put("nit", nit);
        postData.put("email", email);
        postData.put("address", address);
        postData.put("phone", phone);
        postData.put("latitude", ""+currentPosition.latitude);
        postData.put("longitude", ""+currentPosition.longitude);
        postData.put("note", note);
        HttpTask task = new HttpTask(postData, "POST","create_customer", access_token, this);
        task.execute(Globals.API_URL + "/customers");
    }
    public void patchCustomer(String name, String surname, String nit, String email, String address, String phone, String note, int id){
        Map<String, String> postData = new HashMap<>();
        postData.put("_method", "PATCH");
        postData.put("name", name);
        postData.put("surname", surname);
        postData.put("nit", nit);
        postData.put("email", email);
        postData.put("address", address);
        postData.put("phone", phone);
        postData.put("latitude", ""+currentPosition.latitude);
        postData.put("longitude", ""+currentPosition.longitude);
        postData.put("note", note);
        HttpTask task = new HttpTask(postData, "PATCH","patch_customer", access_token, this);
        task.execute(Globals.API_URL + "/customers/"+id);
    }
    private void openCustomerDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("REGISTRAR CLIENTE");
        View view= layoutInflater.inflate(R.layout.form_customer, null);
        builder.setView(view);
        form_name = (EditText) view.findViewById(R.id.form_name);
        form_surname = (EditText) view.findViewById(R.id.form_surname);
        form_nit = (EditText) view.findViewById(R.id.form_nit);
        form_email = (EditText) view.findViewById(R.id.form_email);
        form_address = (EditText) view.findViewById(R.id.form_address);
        form_phone = (EditText) view.findViewById(R.id.form_phone);
        form_note = (EditText) view.findViewById(R.id.form_note);
        input_layout_form_surname = (TextInputLayout) view.findViewById(R.id.input_layout_form_surname);
        input_layout_form_nit = (TextInputLayout) view.findViewById(R.id.input_layout_form_nit);

        builder.setPositiveButton(R.string.store, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(!form_surname.getText().toString().equals("") && !form_nit.getText().toString().equals("")){
                    postCustomer(
                            form_name.getText().toString(),
                            form_surname.getText().toString(),
                            form_nit.getText().toString(),
                            form_email.getText().toString(),
                            form_address.getText().toString(),
                            form_phone.getText().toString(),
                            form_note.getText().toString()
                    );
                }else{
                    if(form_surname.getText().toString().equals("")){
                        input_layout_form_surname.setError("Este campo es requerido.");
                    }
                    if(form_nit.getText().toString().equals("")){
                        input_layout_form_nit.setError("Este campo es requerido.");
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
    private void openEditCustomerDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("ACTUALIZAR CLIENTE");
        View view= layoutInflater.inflate(R.layout.form_customer, null);
        builder.setView(view);
        form_name = (EditText) view.findViewById(R.id.form_name);
        form_surname = (EditText) view.findViewById(R.id.form_surname);
        form_nit = (EditText) view.findViewById(R.id.form_nit);
        form_email = (EditText) view.findViewById(R.id.form_email);
        form_address = (EditText) view.findViewById(R.id.form_address);
        form_phone = (EditText) view.findViewById(R.id.form_phone);
        form_note = (EditText) view.findViewById(R.id.form_note);
        input_layout_form_surname = (TextInputLayout) view.findViewById(R.id.input_layout_form_surname);
        input_layout_form_nit = (TextInputLayout) view.findViewById(R.id.input_layout_form_nit);
        form_name.setText(Customers.get(position).getName().equals("null")?"":Customers.get(position).getName());
        form_surname.setText(Customers.get(position).getSurname());
        form_nit.setText(Customers.get(position).getNit());
        form_email.setText(Customers.get(position).getEmail().equals("null")?"":Customers.get(position).getEmail());
        form_address.setText(Customers.get(position).getAddress().equals("null")?"":Customers.get(position).getAddress());
        form_phone.setText(Customers.get(position).getPhone().equals("null")?"":Customers.get(position).getPhone());
        form_note.setText(Customers.get(position).getNote().equals("null")?"":Customers.get(position).getNote());
        builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(!form_surname.getText().toString().equals("") && !form_nit.getText().toString().equals("")){
                    patchCustomer(
                            form_name.getText().toString(),
                            form_surname.getText().toString(),
                            form_nit.getText().toString(),
                            form_email.getText().toString(),
                            form_address.getText().toString(),
                            form_phone.getText().toString(),
                            form_note.getText().toString(),
                            Customers.get(position).getCustomer_id()
                    );
                }else{
                    if(form_surname.getText().toString().equals("")){
                        input_layout_form_surname.setError("Este campo es requerido.");
                    }
                    if(form_nit.getText().toString().equals("")){
                        input_layout_form_nit.setError("Este campo es requerido.");
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
    private void parseCustomerResult(String result) {
        try {
            JSONArray jsonArray = new JSONArray(result);
            JSONObject jsonObject = new JSONObject(jsonArray.get(0).toString());
            Toast.makeText(getActivity(),jsonObject.get("message").toString(),Toast.LENGTH_SHORT).show();
            getCustomers();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void parseResult(String result){
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = new JSONArray(jsonObject.get("data").toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                Iterator<String> keyItr = jsonArray.getJSONObject(i).keys();
                Map<String, String> map = new HashMap<>();
                while(keyItr.hasNext()) {
                    String key = keyItr.next();
                    map.put(key, jsonArray.getJSONObject(i).getString(key));
                }

                Customer customer = new Customer(
                        map.get("name"),
                        map.get("surname"),
                        map.get("nit"),
                        map.get("email"),
                        map.get("address"),
                        map.get("phone"),
                        map.get("note"),
                        Double.parseDouble(map.get("latitude")),
                        Double.parseDouble(map.get("longitude")),
                        Integer.parseInt(map.get("customer_id")),
                        Integer.parseInt(map.get("created_by"))
                );
                Customers.add(customer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("error ", e.getMessage());
        }
    }
    public class infoAdapter implements GoogleMap.InfoWindowAdapter{

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View v = layoutInflater.inflate(R.layout.item_info_window_customer, null);
            TextView name = (TextView) v.findViewById(R.id.name);
            TextView nit = (TextView) v.findViewById(R.id.nit);
            TextView address = (TextView) v.findViewById(R.id.address);
            TextView email = (TextView) v.findViewById(R.id.email);
            TextView phone = (TextView) v.findViewById(R.id.phone);
            TextView note = (TextView) v.findViewById(R.id.note);
            //set data
            int index= Integer.parseInt(marker.getSnippet());
            if(Customers.get(index).getName().equals("null")) {
                name.setText(Customers.get(index).getSurname());
            }else{
                name.setText(Customers.get(index).getName()+" "+Customers.get(index).getSurname());
            }
            nit.setText(Customers.get(index).getNit());
            address.setText(Customers.get(index).getAddress().equals("null")?"":Customers.get(index).getAddress());
            email.setText(Customers.get(index).getEmail().equals("null")?"":Customers.get(index).getEmail());
            phone.setText(Customers.get(index).getPhone().equals("null")?"":Customers.get(index).getPhone());
            note.setText(Customers.get(index).getNote().equals("null")?"":Customers.get(index).getNote());
            return v;
        }
    }
}
