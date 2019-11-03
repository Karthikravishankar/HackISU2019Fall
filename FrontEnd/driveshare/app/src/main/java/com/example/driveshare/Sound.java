package com.example.driveshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sound extends AppCompatActivity {

    private Button button;
    private Button test;
    private MediaRecorder recorder;
    private MediaPlayer player;
    private MediaPlayer plswork;
    private String FILE;
    private int YOUR_REQUEST_CODE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);
        FILE = Environment.getExternalStorageDirectory() + "/tempRecorder.3gpp";
        //FILE = "C:/Users/Jichuan Zhang/HACKISU2019/HackISU2019Fall/FrontEnd/driveshare/app/src/main/res/raw/temp.3gpp";
        YOUR_REQUEST_CODE = 200; // could be something else..
        button = findViewById(R.id.sound);
        test = findViewById((R.id.button2));
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player = MediaPlayer.create(Sound.this, R.raw.test);
                player.start();
                player.setOnCompletionListener(new OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        player.release();
                    }
                });
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getSound();
                if (button.getText().toString().equals("Speak"))
                {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) //check if permission request is necessary
                    {
                        ActivityCompat.requestPermissions(Sound.this, new String[] {android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, YOUR_REQUEST_CODE);
                    }


                }
                else if (button.getText().toString().equals("Finish Speak")){
                    stopRecord();
                    button.setText("Play");
                }
                else if (button.getText().toString().equals("Play")) {
                    try {
                        playRecord();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    button.setText("Stop");
                }
                else {
                    stopPlay();
                    button.setText("Speak");
                }
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(requestCode == YOUR_REQUEST_CODE)
            {
                try {
                    speak();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                button.setText("Finish Speak");
            }
        } else {
            //Log.d(TAG, "Permission failed");
        }
    }
    private void stopPlay() {

    }
    private void speak() throws IOException {
        if (recorder != null) {
            recorder.release();
        }
        File file = new File(FILE);
        if (file != null) {
            file.delete();
        }

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(FILE);

        recorder.prepare();
        recorder.start();   // Recording is now started

    }

    private void stopRecord() {
        recorder.stop();
        recorder.reset();   // You can reuse the object by going back to setAudioSource() step
        recorder.release(); // Now the object cannot be reused
    }

    private void playRecord() throws IOException {
        File file = new File(FILE);
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];

        for (int readNum; (readNum = fis.read(b)) != -1;) {
            bos.write(b, 0, readNum);
        }

        byte[] bytes = bos.toByteArray();

        final String result = Base64.encodeToString(bytes,0);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/search/soundtotext/chinese/english";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
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

                params.put("Sound",result);

                return params;
            }
        };

        queue.add(postRequest);
    }

//        try
//        {
//            OutputStream out = new FileOutputStream("output.mp3");
//            out.write(bytes);
//        }
//        catch (Exception e)
//        {
//            System.out.println(e.getMessage());
//        }
//
//
//
//                System.out.println("sdsfdsfdfsdfsdfsdf");
//        System.out.println(bytes.toString());
//        if (plswork != null) {
//            plswork.stop();
//            plswork.release();
//        }
//        plswork = new MediaPlayer();
//        plswork.setDataSource(FILE);
//        plswork.prepare();
//        plswork.start();
//        plswork.setOnCompletionListener(new OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mediaPlayer) {
//                plswork.release();
//            }
//        });

//    private void sendSoundToBackend() {
//        RequestQueue queue = Volley.newRequestQueue(this);
//        //The password's TextInputLayout
//        String url =  "";
//
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
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
//                params.put("Sound", sound);
//                return params;
//            }
//        };
//        queue.add(postRequest);
//
//    }

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
