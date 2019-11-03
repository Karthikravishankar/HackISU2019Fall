package com.example.driveshare;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.material.navigation.NavigationView;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.view.MenuItem;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class  DriveShareMain extends AppCompatActivity implements OnMapReadyCallback {
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private NavigationView navigation;
    private Button submit;
    private Spinner type;
    private EditText dest;
    private String lat, lng;
    private String username;
    private GoogleMap mMap;
    private Intent serviceIntent;


    /*
        ToDo:
        1. Get current location and assign latitude and longitude variables.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivesharemain);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("JobLocationUpdates"));
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        serviceIntent = new Intent(DriveShareMain.this, JobLocationService.class);

        drawer = findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        username = getIntent().getStringExtra("Username");
        type = findViewById(R.id.spinner);
        dest = findViewById(R.id.editText_dest);
        submit = findViewById(R.id.button_search);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(        startJobLocationService()) {
                    if (!dest.getText().toString().equals("")) {
                        sendPostRequest();
                    } else
                        Toast.makeText(DriveShareMain.this, "Please add destination!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.Google_Map);
        mapFragment.getMapAsync(this);
        linkActivity();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            lat = intent.getStringExtra("Latitude");
            lng = intent.getStringExtra("Longitude");
        }
    };

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.example.driveshare.JobLocationService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void sendPostRequest(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/search/getDriver";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        stopService(serviceIntent);
                        Toast.makeText(DriveShareMain.this, "Successfully added request", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        System.out.println(error.networkResponse);
                        System.out.println(error.fillInStackTrace());
                        Toast.makeText(DriveShareMain.this, "Failed to create request.", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username",username);
                params.put("destination",dest.getText().toString());
                params.put("type", type.getSelectedItem().toString());
                params.put("latitude", lat);
                params.put("longitude", lng);
                Geocoder geo  = new Geocoder(DriveShareMain.this);
                List<Address> address;
                try {
                    address = geo.getFromLocationName(dest.getText().toString(),1);
                     params.put("dest_lat",String.valueOf(address.get(0).getLatitude()));
                     params.put("dest_lng",String.valueOf(address.get(0).getLongitude()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };
//        postRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
        if(isLocationServiceRunning()) {
            stopService(serviceIntent);
        }
    }
    private void linkActivity() {
        navigation = findViewById(R.id.nav_view);
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.analytics:
                        stopService(serviceIntent);
                        Intent analytics = new Intent(DriveShareMain.this, analytics.class);
                        analytics.putExtra("username",username);
                        startActivity(analytics);
                        break;
                    case R.id.preferences:
                        stopService(serviceIntent);
                        Intent preferences = new Intent(DriveShareMain.this, Preferences.class);
                        preferences.putExtra("username",username);
                        startActivity(preferences);
                        break;
                    case R.id.settings:
                        stopService(serviceIntent);
                        Intent settings = new Intent(DriveShareMain.this, Settings.class);
                        settings.putExtra("username",username);
                        startActivity(settings);
                        break;
                    case R.id.sound:
                        stopService(serviceIntent);
                        Intent sound = new Intent(DriveShareMain.this, Sound.class);
                        sound.putExtra("username",username);
                        startActivity(sound);
                        break;
                }
                return false;
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(42.030781, -93.631912);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Ames"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(sydney.latitude, sydney.longitude), 20));


    }

    private void checkPermissions(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
    }

    private boolean startJobLocationService(){
        checkPermissions();
        //if permission given
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // if location enabled
            if(isLocationEnabled()){
                // start service
                serviceIntent = new Intent(this, JobLocationService.class);
                serviceIntent.putExtra("userName", username);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
                else {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        DriveShareMain.this.startForegroundService(serviceIntent);
                    }
                    else {
                        startService(serviceIntent);
                    }
                    return true;
                }
            }
            else{
                // navigate to turn on location
                locationAlertDialog();
            }
        }
        return false;
    }

    private boolean isLocationEnabled() {
        Context context = this;
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = android.provider.Settings.Secure.getInt(context.getContentResolver(), android.provider.Settings.Secure.LOCATION_MODE);
            } catch (android.provider.Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != android.provider.Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }
    private void locationAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Location not turned on");
        alertDialog.setMessage("Please turn on your Location. Do you want to go to your Location Settings Page?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }
}
