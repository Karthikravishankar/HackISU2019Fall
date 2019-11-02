package com.example.driveshare;

import com.google.api.gax.paging.Page;
import com.google.cloud.bigquery.*;

import java.util.ArrayList;
import java.util.Map;

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
        String query = "Select * from driveshare.userinfo where username = '" + username + "' and password  ='" + password + "'";
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
//        for (FieldValueList row : DriveshareApplication.bigQueryDR.query(queryConfig).iterateAll()) {
//            for (FieldValue val : row) {
//                System.out.printf("%s,", val.toString());
//            }
//            System.out.printf("\n");
//        }
        return DriveshareApplication.bigQueryDR.query(queryConfig).iterateAll();
    }
    
    public void InsertIntoTable(Map<String,Object> input , String tablename)
    {
    	InsertAllRequest insertrequest = InsertAllRequest.newBuilder(getTableByName(tablename).getTableId()).addRow(input).build();
    	InsertAllResponse insertresponse = DriveshareApplication.bigQueryDR.insertAll(insertrequest);
    	
    	if(insertresponse.hasErrors())
    		System.out.println("error ocurr when inserting the row");
    	
    }
    
}
