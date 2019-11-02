package com.example.driveshare;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@SpringBootApplication
public class DriveshareApplication {


	public static BigQuery bigQueryDR;
	public static Dataset datasetDR;

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(DriveshareApplication.class, args);
		bigQueryDR = BigQueryOptions.getDefaultInstance().getService();

		GoogleCredentials credentials = null;
		File credentialsPath = new File("C:\\Users\\Karth\\Desktop\\hackisu2019fall\\HackISU2019Fall\\spring\\driveshare\\src\\cert.json");  // TODO: update to your key path.
		try (FileInputStream serviceAccountStream = new FileInputStream(credentialsPath)) {
			credentials = ServiceAccountCredentials.fromStream(serviceAccountStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Instantiate a client.
		bigQueryDR =
				BigQueryOptions.newBuilder().setCredentials(credentials).build().getService();

		// Use the client.
		System.out.println("Datasets:");
		for (Dataset dataset : bigQueryDR.listDatasets().iterateAll()) {
			System.out.printf("%s%n", dataset.getDatasetId().getDataset());
			if(dataset.getDatasetId().getDataset().equals("driveshare"));{
				datasetDR = dataset;
			}
		}

	}

}
