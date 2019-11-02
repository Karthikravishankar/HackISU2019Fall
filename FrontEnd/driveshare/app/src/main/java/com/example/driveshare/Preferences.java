package com.example.driveshare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Preferences extends AppCompatActivity {
    private Spinner age, sport, pet, music, drink, food;
    private String Age, Sport, Pet, Music, Drink, Food, TalkorListener;
    private Button send;
    private RadioGroup talkOrlisten;
    private RadioButton talkorlistener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        age = findViewById(R.id.age);
        sport = findViewById(R.id.sport);
        pet = findViewById(R.id.pet);
        music = findViewById(R.id.music);
        drink = findViewById(R.id.drink);
        food = findViewById(R.id.food);
        send = findViewById(R.id.send);

        talkOrlisten = findViewById(R.id.talkOrlisten);
        int select = talkOrlisten.getCheckedRadioButtonId();
        talkorlistener = findViewById(select);
        TalkorListener = talkorlistener.getText().toString();

        age.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Age = age.getItemAtPosition(age.getSelectedItemPosition()).toString();
                Age = Age.replace("\"", "");
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });
        sport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Sport = sport.getItemAtPosition(age.getSelectedItemPosition()).toString();
                Sport = Sport.replace("\"", "");
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });
        pet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Pet = pet.getItemAtPosition(age.getSelectedItemPosition()).toString();
                Pet = Pet.replace("\"", "");
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });
        music.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Music = music.getItemAtPosition(age.getSelectedItemPosition()).toString();
                Music = Music.replace("\"", "");
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });
        drink.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Drink = drink.getItemAtPosition(age.getSelectedItemPosition()).toString();
                Drink = Drink.replace("\"", "");
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });
        food.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Food = food.getItemAtPosition(age.getSelectedItemPosition()).toString();
                Food = Food.replace("\"", "");
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPreference();
            }
        });
    }
    private void sendPreference() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Intent i = new Intent(LoginAndSignup.this, DriveShareMain.class);
//                        startActivity(i);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Age", Age);
                params.put("Sport", Sport);
                params.put("Pet", Pet);
                params.put("Music", Music);
                params.put("Drink", Drink);
                params.put("Food", Food);
                params.put("TalkorListener", TalkorListener);
                System.out.println("Put successfully");
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}