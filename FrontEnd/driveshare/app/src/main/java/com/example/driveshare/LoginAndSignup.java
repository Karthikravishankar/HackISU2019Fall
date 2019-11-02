package com.example.driveshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStructure;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class LoginAndSignup extends AppCompatActivity {
    private Button login;
    private EditText username;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
//                Intent i = new Intent(LoginAndSignup.this, DriveShareMain.class);
//                startActivity(i);
                try {
                    sendPostValidateLogin();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

//    private void Login() {
//        RequestQueue queue = Volley.newRequestQueue(this);
//        String url =  "http://" + getString(R.string.ip_address) + ":8080/login/validateLogin";
//
//        // Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        System.out.println(response);
//                        if (response.equals("true")){
//                            Intent i = new Intent(LoginAndSignup.this, DriveShareMain.class);
//                            startActivity(i);
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                System.out.println(username.getText().toString());
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Username", username.getText().toString());
//                params.put("Password", password.getText().toString());
//
//                System.out.println("Put successfully");
//                return params;
//            }
//        };
//
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest);
//    }


    private void sendPostValidateLogin() throws Exception {
        RequestQueue queue = Volley.newRequestQueue(this);
        //The password's TextInputLayout
        String url =  "http://" + getString(R.string.ip_address) + ":8080/login/validateLogin";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        if (response.equals("true")){
                            Intent i = new Intent(LoginAndSignup.this, DriveShareMain.class);
                            i.putExtra("Username", username.getText().toString());
                            startActivity(i);
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
                System.out.println(username.getText().toString());
                Map<String, String> params = new HashMap<String, String>();
                params.put("Username", username.getText().toString());
                params.put("Password", password.getText().toString());

                System.out.println("Put successfully");
                return params;
            }
        };
        queue.add(postRequest);
        System.out.println("done?");
    }
}





