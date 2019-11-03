package com.example.driveshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class Sound extends AppCompatActivity {
    private EditText textToTranslate;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);
        textToTranslate = findViewById(R.id.translate);
        button = findViewById(R.id.sound);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translate();
            }
        });
    }
    private void translate() {
        RequestQueue queue = Volley.newRequestQueue(this);
        //The password's TextInputLayout
        String url =  "http://" + getString(R.string.ip_address) + ":8080/search/translate/"+textToTranslate.getText().toString();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        byte[] temp = Base64.decode(response,Base64.DEFAULT);



                        playMp3(temp);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) {

        };
        queue.add(postRequest);
        System.out.println("done?");
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
//    private void getSound() {
//        RequestQueue queue = Volley.newRequestQueue(this);
//        //The password's TextInputLayout
//        String url =  "http://" + getString(R.string.ip_address) + ":8080/login/validateLogin";
//
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        System.out.println(response);
////                        byte[] data = Base64.decode(response, Base64.DEFAULT);
////                        playMp3(data);
//                        byte[] data = Base64.decode(response, Base64.URL_SAFE);
//
//
//                        playMp3(data);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("","");
//                return params;
//            }
//        };
//        queue.add(postRequest);
//        System.out.println("done?");
//    }
//
//    private void playMp3(byte[] mp3SoundByteArray) {
//        MediaPlayer mediaPlayer = new MediaPlayer();
//        try {
//            // create temp file that will hold byte array
//            File tempMp3 = File.createTempFile("kurchina", "mp3", getCacheDir());
//            tempMp3.deleteOnExit();
//            FileOutputStream fos = new FileOutputStream(tempMp3);
//            fos.write(mp3SoundByteArray);
//            fos.close();
//
//            // resetting mediaplayer instance to evade problems
//            mediaPlayer.reset();
//
//            // In case you run into issues with threading consider new instance like:
//            // MediaPlayer mediaPlayer = new MediaPlayer();
//
//            // Tried passing path directly, but kept getting
//            // "Prepare failed.: status=0x1"
//            // so using file descriptor instead
//            FileInputStream fis = new FileInputStream(tempMp3);
//            mediaPlayer.setDataSource(fis.getFD());
//
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//        } catch (IOException ex) {
//            String s = ex.toString();
//            ex.printStackTrace();
//        }
//    }
}
