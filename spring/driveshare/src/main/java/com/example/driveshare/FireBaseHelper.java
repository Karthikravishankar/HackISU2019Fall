package com.example.driveshare;

import com.google.firebase.database.*;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class FireBaseHelper {
    public String getFirebaseData(String tableName, String object, String search) throws IOException, InterruptedException {
        CountDownLatch done = new CountDownLatch(1);
        final String[] value = new String[1];
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mFirebaseDatabase.getReference().child(tableName);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.child(object).child(search) != null){
                    value[0] = String.valueOf(snapshot.child(object).child(search).getValue());
                }
                // it exists!
                done.countDown();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        try {
            done.await(); //it will wait till the response is received from firebase.
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        if(value[0]==null){
            value[0] = "NA";
        }
        return value[0];
    }

    public String deleteFirebaseData(String tableName, String object) throws IOException, InterruptedException {
        CountDownLatch done = new CountDownLatch(1);
        final String[] value = new String[1];
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mFirebaseDatabase.getReference().child(tableName);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.child(object).exists()){
                    value[0] = "true";
                    snapshot.child(object).getRef().removeValue(null);
                }
                // it exists!
                done.countDown();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        try {
            done.await(); //it will wait till the response is received from firebase.
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        if(value[0]==null){
            value[0] = "false";
        }
        return value[0];
    }

    public void updateFirebaseData(String tableName, String object, String key, String value){
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference;
        mDatabaseReference = mDatabase.getReference().child(tableName);
        mDatabaseReference = mDatabaseReference.child(object);
        mDatabaseReference.child(key).setValue(value,null);
    }

    public JSONObject getAvailableDriver(String userName){
        JSONArray array = new JSONArray();
        JSONObject jsonobject = new JSONObject();
        final CountDownLatch[] done = {new CountDownLatch(1)};
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mFirebaseDatabase.getReference().child("preferences");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot Snapshot: dataSnapshot.getChildren()) {
                    System.out.println(String.valueOf(Snapshot.child("User").getValue()));
                    if(Snapshot.child("Driving").exists() && String.valueOf(Snapshot.child("User").getValue()).equals(userName)==false) {
                        if (Snapshot.child("Driving").getValue().toString().equals("true")) {
                            Object object = Snapshot.getValue(Object.class);
                            array.put(new Gson().toJson(object));
                        }
                    }
                }
                if(array!=null) {
                    try {
                        jsonobject.put("result", array);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                done[0].countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        try {
            done[0].await(); //it will wait till the response is received from firebase.
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(array.length());
        return jsonobject;
    }
}
