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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.navigation.NavigationView;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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

public class DriveShareMain extends AppCompatActivity implements OnMapReadyCallback {
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private NavigationView navigation;
    private Button submit,workflow,reached,updateButton;
    private Spinner type;
    private EditText dest;
    private String lat, lng, progress = "";
    private String username;
    private String customername,drivername,FromLocation="",TOdestination="";
    private JSONObject coordinates = new JSONObject();
    private double distance=0.0;
    private int sendCount;
    private boolean allowed=false;
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
        workflow = findViewById(R.id.WorkFlow);
        reached = findViewById(R.id.reached);
        workflow.setVisibility(View.INVISIBLE);
        reached.setVisibility(View.INVISIBLE);
        updateButton = findViewById(R.id.Update);
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
                if (isLocationServiceRunning() == false) {
                    if (!dest.getText().toString().equals("")) {
                        if (startJobLocationService()) {
                            sendPostRequest();
                        }
                    } else {
                        Toast.makeText(DriveShareMain.this, "Please add destination!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    if (!dest.getText().toString().equals("")) {
                        sendPostRequest();
                    } else {
                        Toast.makeText(DriveShareMain.this, "Please add destination!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.Google_Map);
        mapFragment.getMapAsync(this);
        getCustomerDriverName();
        linkActivity();
        getWorkFlowStatus();
        workflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(progress.equals("Wait For Driver")){
                    // nothing
                }
                else if(progress.equals("Start Driving")){
                    progress = "Pickup";
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("status","Pickup");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // change status of customer to "Normal"
                    sendPostUpdateDataBase("userinfo",username, jsonObject.toString());
                }
                else if(progress.equals("Pickup")){
                    if(isLocationServiceRunning()==false) {
                        if (startJobLocationService()) {
                            getCoordinates("userinfo",username,"JourneyCoordinates");
                            allowed=true;
                            progress = "Verification";
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("status", "Enter the car");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            // change status of customer to "Enter car"
                            sendPostUpdateDataBase("userinfo", customername, jsonObject.toString());

                            jsonObject = new JSONObject();
                            try {
                                jsonObject.put("status", "Verification");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            // change status of customer to "Normal"
                            sendPostUpdateDataBase("userinfo", username, jsonObject.toString());
                        }
                    }
                    else {
                        stopService(serviceIntent);
                    }
                }
                else if(progress.equals("Verification")){
                    if(isLocationServiceRunning()==false) {
                        if(startJobLocationService()) {
                            getCoordinates("userinfo",username,"JourneyCoordinates");
                            allowed=true;
                            //TODO
                            // send verification code / remainder
                            // sendTwilioVerify();
                            // if success
                            // change progress to interests
                            progress = "Interests";
                            // change progress of customer to rise with driver
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("status", "Ride with Driver");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            // change status of customer to "Ride with Driver"
                            sendPostUpdateDataBase("userinfo", customername, jsonObject.toString());
//                            sendTwilioVerify();
                            jsonObject = new JSONObject();
                            try {
                                jsonObject.put("status", progress);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            // change status of customer to "Normal"
                            sendPostUpdateDataBase("userinfo", username, jsonObject.toString());
                            // set reached to visible
                            reached.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        stopService(serviceIntent);
                    }
                }
                else if(progress.equals("Interests")){
                    if(isLocationServiceRunning()==false){
                        if(startJobLocationService()){
                            allowed=true;
                            getCoordinates("userinfo",username,"JourneyCoordinates");
                            //TODO
                            // play sounds
                            sendPlaySoundRequest();
                        }
                    }
                    else{
                        stopService(serviceIntent);
                    }
                }
                else if(progress.equals("Finish")){
                    //TODO
                    // set rating for each other using pop up
                    // change status to normal
                    allowed= false;
                    try {
                        if(isLocationServiceRunning()){
                            stopService(serviceIntent);
                        }
                        ratingAlertPopUp();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // update firebase with progress
                workflow.setText(progress);
            }
        });
        reached.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress = "Finish";
                allowed=false;
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("status","Finish");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // change status of customer to "Normal"
                sendPostUpdateDataBase("userinfo",customername, jsonObject.toString());


                jsonObject = new JSONObject();
                try {
                    jsonObject.put("status","Finish");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // change status of customer to "Normal"
                sendPostUpdateDataBase("userinfo",username, jsonObject.toString());
                reached.setVisibility(View.INVISIBLE);
                workflow.setText(progress);
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCoordinates("userinfo",username,"JourneyCoordinates");
            }
        });
    }

    private void sendPlaySoundRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/search/soundtest";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("null") || response.equals("NA") ){
                            // do nothing
                        }
                        else{
                            byte[] temp = Base64.decode(response,Base64.DEFAULT);



                            playMp3(temp);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("customer", customername);
                return params;
            }
        };
        queue.add(postRequest);
    }

    private void playMp3(byte[] mp3SoundByteArray) {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();

            // create temp file that will hold byte array
            File tempMp3 = File.createTempFile("kurchina", "mp3", getCacheDir());
            tempMp3.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(mp3SoundByteArray);
            fos.close();

            // resetting mediaplayer instance to evade problems
            mediaPlayer.reset();

            // In case you run into issues with threading consider new instance like:
            // MediaPlayer mediaPlayer = new MediaPlayer();

            // Tried passing path directly, but kept getting
            // "Prepare failed.: status=0x1"
            // so using file descriptor instead
            FileInputStream fis = new FileInputStream(tempMp3);
            mediaPlayer.setDataSource(fis.getFD());

            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException ex) {
            String s = ex.toString();
            ex.printStackTrace();
        }
    }

    private void saveInBigQuery (final String customername, final String username, final String distance, final String cost, final String gasoline, final String date)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/search/saveDriveInfo";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("null") || response.equals("NA") ){
                            // do nothing
                        }
                        System.out.println(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.networkResponse);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", customername);
                params.put("drivername", username);
                params.put("pickup", FromLocation);
                params.put("destination ", TOdestination);
                params.put("cost", cost);
                params.put("gasolineSaved", gasoline);
                params.put("distance", distance);
                params.put("date", date);

                return params;
            }
        };
        System.out.println("In Save BQ");
        queue.add(postRequest);
        System.out.println("In Save BQ2");

    }

    private void getCustomerDriverName() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/search/getCustomerName";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("null") || response.equals("NA") ){
                            // do nothing
                        }
                        else{
                            customername = response;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                return params;
            }
        };
        queue.add(postRequest);
    }
    private void sendTwilioVerify() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/sms/verify";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Verification Send");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("customername", customername);
                return params;
            }
        };
        queue.add(postRequest);
    }
    private void getWorkFlowStatus() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/search/WorkFlowStatus";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("|" + response + "|");
                        if(response.equals("null") || response.equals("Normal") || response.equals("[]")){
                            // do nothing
                        }
                        else if(response.equals("Normal")){

                        }
                        else{
                            type.setVisibility(View.INVISIBLE);
                            dest.setVisibility(View.INVISIBLE);
                            submit.setVisibility(View.INVISIBLE);
                            workflow.setVisibility(View.VISIBLE);
                            progress = response;
                            workflow.setText(progress);
                            if (progress.equals("Verification") || progress.equals("Interests")) {
                                reached.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                return params;
            }
        };
        queue.add(postRequest);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            lat = intent.getStringExtra("Latitude");
            lng = intent.getStringExtra("Longitude");
            JSONObject newJSON = new JSONObject();
            try {
                newJSON.put("Latitude", lat);
                newJSON.put("Longitude", lng);
//                if(distance==0.0) {
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf((String)lat), Double.valueOf((String)lng)), 20));
//                }
                if(allowed) {
                    coordinates.put(String.valueOf(coordinates.length()), newJSON.toString());
                    sendCount++;
                    if (sendCount % 5 == 0) {
                        updateCoordinates("userinfo", username);
                        updateCoordinates("userinfo", customername);
                    }
                    updateMap();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void getDriverJobFrom(final String tableName, final String object, final String search) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/search/getFireBaseData";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("|"+response+"|");
                        // get the coordinates from firebase and then update the json here
                        if(response==null || response.equals("null")){
                        }
                        else if(response.equals("NA")==false){
                            FromLocation = response;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DriveShareMain.this, "Failed to get coordinate data", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("search", search);
                params.put("object", object);
                params.put("tableName", tableName);
                return params;
            }
        };
        queue.add(postRequest);
    }

    private void getDriverJobDest(final String tableName, final String object, final String search) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/search/getFireBaseData";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("|"+response+"|");
                        // get the coordinates from firebase and then update the json here
                        if(response==null || response.equals("null")){
                        }
                        else if(response.equals("NA")==false){
                            TOdestination = response;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DriveShareMain.this, "Failed to get coordinate data", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("search", search);
                params.put("object", object);
                params.put("tableName", tableName);
                return params;
            }
        };
        queue.add(postRequest);
    }

    private void getCoordinates(final String tableName, final String object, final String search) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/search/getFireBaseData";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // get the coordinates from firebase and then update the json here
                        if(response==null || response.equals("null")){
                            coordinates = new JSONObject();
                        }
                        else if(response.equals("NA")==false){
                            try {
                                coordinates = new JSONObject(response);
                                updateMap();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            coordinates = new JSONObject();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DriveShareMain.this, "Failed to get coordinate data", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("search", search);
                params.put("object", object);
                params.put("tableName", tableName);
                return params;
            }
        };
        queue.add(postRequest);
    }

    private void updateCoordinates(final String tableName, final String object) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/search/updateFireBase";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // ??
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DriveShareMain.this, "Failed to get coordinate data", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    System.out.println(object);
                    JSONObject toSend = new JSONObject();
                    toSend.put("JourneyCoordinates",coordinates.toString());
                    params.put("jsonString", toSend.toString());
                    params.put("object", object);
                    params.put("tableName", tableName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return params;
            }
        };
        queue.add(postRequest);
    }

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.driveshare.JobLocationService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void sendPostRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/search/getDriver";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        stopService(serviceIntent);
                        try {
                            alertPopUpConfirm(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                params.put("username", username);
                params.put("destination", dest.getText().toString());
                params.put("type", type.getSelectedItem().toString());
                params.put("latitude", lat);
                params.put("longitude", lng);
                Geocoder geo = new Geocoder(DriveShareMain.this);
                List<Address> address;
                try {
                    address = geo.getFromLocationName(dest.getText().toString(), 1);
                    params.put("dest_lat", String.valueOf(address.get(0).getLatitude()));
                    params.put("dest_lng", String.valueOf(address.get(0).getLongitude()));
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

    private void alertPopUpConfirm(String response) throws JSONException {
        final TextView input1 = new TextView(this);
        String toRender = ">.<*\n";
        final JSONObject toRenderJSON = new JSONObject(response);
        Iterator<String> keys = toRenderJSON.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            if(key.equals("Driving")==false && key.equals("dest_lng")==false && key.equals("dest_lat")==false
                    && key.equals("from_lat")==false && key.equals("from_lng")==false){
                toRender=toRender+key+" : " + toRenderJSON.get(key)+"\n";
            }
        }
        input1.setText(toRender);
        input1.setGravity(Gravity.CENTER);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(input1);

        sendPostUpdateDataBase("userinfo",String.valueOf(toRenderJSON.get("User")),response);

        final AlertDialog builder = new AlertDialog.Builder(this)
                .setTitle("Confirmation of Starting Job")
                .setCancelable(false)
                .setMessage("Please look at the information and then confirm your ride.").setView(linearLayout).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
        builder.show();
        ((AlertDialog) builder).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // customer
                progress = "Wait For Driver";
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("status", progress);
                    jsonObject.put("drivername", toRenderJSON.get("User"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sendPostUpdateDataBase("userinfo", username, jsonObject.toString());

                // driver
                jsonObject = new JSONObject();
                try {
                    jsonObject.put("status", "Start Driving");
                    jsonObject.put("customername", username);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    sendPostUpdateDataBase("userinfo", (String) toRenderJSON.get("User"), jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                type.setVisibility(View.INVISIBLE);
                dest.setVisibility(View.INVISIBLE);
                submit.setVisibility(View.INVISIBLE);
                workflow.setVisibility(View.VISIBLE);
                workflow.setText(progress);
                builder.dismiss();
            }
        });
        ((AlertDialog) builder).getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
    }

    private void ratingAlertPopUp() throws JSONException {
        final TextView input1 = new TextView(this);
        String toRender = ">.<*\n";
        input1.setText(toRender);
        input1.setGravity(Gravity.CENTER);

        final EditText input2 = new EditText(this);
        input2.setHint("Rating");
        input2.setGravity(Gravity.CENTER);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(input1);
        linearLayout.addView(input2);

        final AlertDialog builder = new AlertDialog.Builder(this)
                .setTitle("Rate your Friendship")
                .setCancelable(false)
                .setMessage("Please rate your friendship based on the journey.").setView(linearLayout).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
        builder.show();
        ((AlertDialog) builder).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("status","Normal");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // change status of customer to "Normal"
                sendPostUpdateDataBase("userinfo",customername, jsonObject.toString());


                jsonObject = new JSONObject();
                try {
                    jsonObject.put("status","Normal");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                JSONObject toSave = new JSONObject();
                try {
                    getDriverJobDest("preferences", username,"Destination");
                    getDriverJobFrom("preferences", username,"From");
                    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    Date date = new Date();
                    toSave.put("username",customername);
                    toSave.put("drivername",username);
                    toSave.put("pickup",FromLocation);
                    toSave.put("destination",TOdestination);
                    toSave.put("distance",String.valueOf(distance));
                    toSave.put("cost",String.valueOf((distance/24.7)*2.81));
                    toSave.put("gasolineSaved",String.valueOf(distance/24.7));
                    toSave.put("date",dateFormat.format(date));
                    saveInBigQuery(customername, username, String.valueOf(distance), String.valueOf((distance/24.7)*2.81),String.valueOf(distance/24.7),dateFormat.format(date));

                } catch (JSONException e) {
                    e.printStackTrace();
                }



                // change status of customer to "Normal"
                sendPostUpdateDataBase("userinfo",username, jsonObject.toString());
                type.setVisibility(View.VISIBLE);
                dest.setVisibility(View.VISIBLE);
                submit.setVisibility(View.VISIBLE);
                workflow.setVisibility(View.INVISIBLE);
                reached.setVisibility(View.INVISIBLE);
//                sendPostDelete("preferences",username);
//                sendPostDelete("userinfo",customername);
                builder.dismiss();
            }
        });
        ((AlertDialog) builder).getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
    }

    private void translatePopUp() throws JSONException {
        final TextView input1 = new TextView(this);
        String toRender = ">.<*\n";
        input1.setText(toRender);
        input1.setGravity(Gravity.CENTER);

        final EditText input2 = new EditText(this);
        input2.setHint("Text");
        input2.setGravity(Gravity.CENTER);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(input1);
        linearLayout.addView(input2);

        final AlertDialog builder = new AlertDialog.Builder(this)
                .setTitle("Enter text to translate")
                .setCancelable(false)
                .setMessage("Please rate your friendship based on the journey.").setView(linearLayout).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
        builder.show();
        ((AlertDialog) builder).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("status","Normal");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // change status of customer to "Normal"
                sendPostUpdateDataBase("userinfo",customername, jsonObject.toString());


                jsonObject = new JSONObject();
                try {
                    jsonObject.put("status","Normal");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                JSONObject toSave = new JSONObject();
                try {
                    getDriverJobDest("preferences", username,"Destination");
                    getDriverJobFrom("preferences", username,"From");
                    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    Date date = new Date();
                    toSave.put("username",customername);
                    toSave.put("drivername",username);
                    toSave.put("pickup",FromLocation);
                    toSave.put("destination",TOdestination);
                    toSave.put("distance",String.valueOf(distance));
                    toSave.put("cost",String.valueOf((distance/24.7)*2.81));
                    toSave.put("gasolineSaved",String.valueOf(distance/24.7));
                    toSave.put("date",dateFormat.format(date));
                    saveInBigQuery(customername, username, String.valueOf(distance), String.valueOf((distance/24.7)*2.81),String.valueOf(distance/24.7),dateFormat.format(date));

                } catch (JSONException e) {
                    e.printStackTrace();
                }



                // change status of customer to "Normal"
                sendPostUpdateDataBase("userinfo",username, jsonObject.toString());
                type.setVisibility(View.VISIBLE);
                dest.setVisibility(View.VISIBLE);
                submit.setVisibility(View.VISIBLE);
                workflow.setVisibility(View.INVISIBLE);
                reached.setVisibility(View.INVISIBLE);
//                sendPostDelete("preferences",username);
//                sendPostDelete("userinfo",customername);
                builder.dismiss();
            }
        });
        ((AlertDialog) builder).getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
    }

    private void sendPostUpdateDataBase(final String tableName, final String object, final String jsonString) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/search/updateFireBase";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(DriveShareMain.this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DriveShareMain.this, "Failed to update settings", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("jsonString", jsonString);
                params.put("object", object);
                params.put("tableName", tableName);
                System.out.println(object);
                System.out.println(jsonString);
                return params;
            }
        };
        queue.add(postRequest);
    }

    private void sendPostDelete(final String tableName, final String object) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/search/deleteUser";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(DriveShareMain.this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("object", object);
                params.put("tableName", tableName);
                return params;
            }
        };
        queue.add(postRequest);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
        if (isLocationServiceRunning()) {
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
                        analytics.putExtra("username", username);
                        startActivity(analytics);
                        break;
                    case R.id.preferences:
                        stopService(serviceIntent);
                        Intent preferences = new Intent(DriveShareMain.this, Preferences.class);
                        preferences.putExtra("username", username);
                        startActivity(preferences);
                        break;
                    case R.id.settings:
                        stopService(serviceIntent);
                        Intent settings = new Intent(DriveShareMain.this, Settings.class);
                        settings.putExtra("username", username);
                        startActivity(settings);
                        break;
                    case R.id.sound:
                        stopService(serviceIntent);
                        Intent sound = new Intent(DriveShareMain.this, Sound.class);
                        sound.putExtra("username", username);
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
    }

    private void updateMap(){
        // TODO get map to show cur location
        // Add a marker in Sydney, Australia, and move the camera.
//        LatLng sydney = new LatLng(42.030781, -93.631912);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Ames"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(sydney.latitude, sydney.longitude), 20));

        if(lat != null || coordinates.length()>0) {

            try {
                mMap.clear();
                JSONObject startLocation = null;
                startLocation = new JSONObject((String) coordinates.get("0"));

                mMap.addMarker(new MarkerOptions()
                        .title("Starting Marker")
                        .position(new LatLng(Double.valueOf((String) startLocation.get("Latitude")), Double.valueOf((String) startLocation.get("Longitude"))))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                Iterator<String> keys = coordinates.keys();
                List<LatLng> journey = new ArrayList<>();
                distance = 0.0;
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject tempLocation = new JSONObject((String) coordinates.get(key));
                    LatLng newCoord = new LatLng(Double.valueOf((String) tempLocation.get("Latitude")), Double.valueOf((String) tempLocation.get("Longitude")));
                    if (journey.size() > 0) {
                        Location locationA = new Location("Prev");
                        locationA.setLatitude(journey.get(journey.size() - 1).latitude);
                        locationA.setLongitude(journey.get(journey.size() - 1).longitude);

                        Location locationB = new Location("Cur");
                        locationB.setLatitude(newCoord.latitude);
                        locationB.setLongitude(newCoord.longitude);

                        distance = (distance + (double) locationA.distanceTo(locationB));
                    }
                    journey.add(newCoord);
                }
                PolylineOptions opts = new PolylineOptions().addAll(journey).color(Color.CYAN).width(6);
                mMap.addPolyline(opts);

                mMap.addMarker(new MarkerOptions()
                        .title("Current Marker")
                        .position(new LatLng(Double.valueOf(journey.get(journey.size() - 1).latitude), Double.valueOf(journey.get(journey.size() - 1).longitude)))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                DecimalFormat df = new DecimalFormat("#.###");
                distance = Double.parseDouble(df.format(distance * 0.00062137));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
    }

    private boolean startJobLocationService() {
        checkPermissions();
        //if permission given
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // if location enabled
            if (isLocationEnabled()) {
                // start service
                serviceIntent = new Intent(this, JobLocationService.class);
                serviceIntent.putExtra("userName", username);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        DriveShareMain.this.startForegroundService(serviceIntent);
                    } else {
                        startService(serviceIntent);
                    }
                    return true;
                }
            } else {
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
