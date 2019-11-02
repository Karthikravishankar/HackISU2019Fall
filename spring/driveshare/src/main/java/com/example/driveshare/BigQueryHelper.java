package com.example.driveshare;

import com.google.api.gax.paging.Page;
import com.google.cloud.bigquery.*;

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

    // [TARGET create(JobInfo, JobOption...)]
// [VARIABLE "SELECT field FROM my_dataset_name.my_table_name"]
    public Job createJob(String query) {
        // [START createJob]
        Job job = null;
        JobConfiguration jobConfiguration = QueryJobConfiguration.of(query);
        JobInfo jobInfo = JobInfo.of(jobConfiguration);
        try {
            job = DriveshareApplication.bigQueryDR.create(jobInfo);
        } catch (BigQueryException e) {
            // the job was not created
        }
        // [END createJob]
        return job;
    }
}
