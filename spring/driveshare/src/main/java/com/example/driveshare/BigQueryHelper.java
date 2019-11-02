package com.example.driveshare;

import com.google.api.gax.paging.Page;
import com.google.cloud.bigquery.*;
import com.google.firebase.database.*;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class BigQueryHelper {

    public Table getTableByName(String name){
        Page<Table> tables = DriveshareApplication.bigQueryDR.listTables(String.valueOf(DriveshareApplication.datasetDR.getDatasetId().getDataset()), BigQuery.TableListOption.pageSize(100));
        for(Table table: tables.iterateAll()){
            if(table.getTableId().getTable().equals(name)){
                return table;
            }
        }
        return null;
    }


    public boolean userExists(String username, String password) throws InterruptedException {
        String query = "Select * from driveshare.userinfo where Username = '" + username + "' and Password  ='" + password + "'";
        QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();
        for (FieldValueList row : DriveshareApplication.bigQueryDR.query(queryConfig).iterateAll()) {
            for (FieldValue val : row) {
                System.out.printf("%s,", String.valueOf(val.getValue()));
                return true;
            }
        }
        return false;
    }

    public Iterable<FieldValueList> executeQuery(String query) throws InterruptedException {
        QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();

        
        return DriveshareApplication.bigQueryDR.query(queryConfig).iterateAll();
    }
    
    public void InsertIntoTable(Map<String,Object> input , String tablename)
    {
    	InsertAllRequest insertrequest = InsertAllRequest.newBuilder(getTableByName(tablename).getTableId()).addRow(input).build();
    	InsertAllResponse insertresponse = DriveshareApplication.bigQueryDR.insertAll(insertrequest);
    	
    	if(insertresponse.hasErrors())
    		System.out.println("error ocurr when inserting the row");
    	
    }


    public JSONObject getAvailableDriver(){
        JSONObject jsonobject = new JSONObject();
        final CountDownLatch[] done = {new CountDownLatch(1)};
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mFirebaseDatabase.getReference().child("preferences");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot Snapshot: dataSnapshot.getChildren()) {
                    if (Snapshot.child("Driving").getValue().toString().equals("true")) {
                        try {
                            jsonobject.put(Snapshot.getKey(),Snapshot.getValue());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

        return jsonobject;
    }
}
