package com.example.driveshare;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Settings extends AppCompatActivity {

    private Switch drive;
    private EditText start, end, lang;

    /*
        TODO:
        1. After making someone a driver, find a way to notify them.
            They do not need to check driving switch again. Set to true.
            Also add text confirmation.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        drive = findViewById(R.id.switch_drive);
        start = findViewById(R.id.editText_start);
        end = findViewById(R.id.editText_end);
        lang = findViewById(R.id.editText_lang);

        drive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!start.getText().toString().equals("") && !end.getText().toString().equals("")){
                    //geocoder to get lat long and sendPostReq
                    Geocoder geo  = new Geocoder(Settings.this);
                    Location from, destination;
                    Double f_lat, f_lng, d_lat, d_lng;
                    List<Address> address;
                    try {
                        address = geo.getFromLocationName(start.getText().toString(),1);
                        f_lat = address.get(0).getLatitude();
                        f_lng = address.get(0).getLongitude();
                        address = geo.getFromLocationName(end.getText().toString(),1);
                        d_lat = address.get(0).getLatitude();
                        d_lng = address.get(0).getLongitude();
                        sendPostRequest(f_lat, d_lat, f_lng, d_lng);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else {
                    buttonView.setChecked(false);
                    Toast.makeText(Settings.this, "Fill start and end address please!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendPostRequest(final Double lat1, final Double lat2, final Double lng1, final Double lng2) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/search/Settings";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(Settings.this, "Successfully added request", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        System.out.println(error.fillInStackTrace());
                        Toast.makeText(Settings.this, "Failed to create request.", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("User",getIntent().getStringExtra("username"));
                params.put("From", start.getText().toString());
                params.put("Destination", end.getText().toString());
                params.put("Driving", Boolean.toString(drive.isChecked()));
                params.put("from_lat", Double.toString(lat1));
                params.put("dest_lat", Double.toString(lat2));
                params.put("from_lng", Double.toString(lng1));
                params.put("dest_lng", Double.toString(lng2));
                params.put("lang", lang.getText().toString());
                return params;
            }
        };
        queue.add(postRequest);
    }
}
