package com.example.driveshare;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
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

	public static void main(String[] args) throws InterruptedException, IOException {
		SpringApplication.run(DriveshareApplication.class, args);


		//Big query initialize
//		bigQueryDR = BigQueryOptions.getDefaultInstance().getService();

		GoogleCredentials credentials = null;
		File credentialsPath = new File("C:\\Users\\Karth\\Desktop\\hackisu2019fall\\HackISU2019Fall\\spring\\driveshare\\src\\cert.json");  // TODO: update to your key path.
//		File credentialsPath = new File("/home/taro/Workspace/hanker/HackISU2019Fall/spring/driveshare/src/cert.json");  // TODO: update to your key path.
//		File credentialsPath = new File("/Users/skinnyg/Documents/HackISU/HackISU2019Fall/spring/driveshare/src/cert.json");
		try (FileInputStream serviceAccountStream = new FileInputStream(credentialsPath)) {
			credentials = ServiceAccountCredentials.fromStream(serviceAccountStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Instantiate a client.
		bigQueryDR = BigQueryOptions.newBuilder().setCredentials(credentials).build().getService();
		System.out.println("Datasets:");
		for (Dataset dataset : bigQueryDR.listDatasets().iterateAll()) {
			System.out.printf("%s%n", dataset.getDatasetId().getDataset());
			if(dataset.getDatasetId().getDataset().equals("driveshare"));{
				datasetDR = dataset;
			}
		}

		// Firebase initialize
		FileInputStream serviceAccount =
				new FileInputStream("C:\\Users\\Karth\\Desktop\\hackisu2019fall\\HackISU2019Fall\\spring\\driveshare\\src\\FBcert.json");
//				new FileInputStream("/home/taro/Workspace/hanker/HackISU2019Fall/spring/driveshare/src/FBcert.json");
//				new FileInputStream("/Users/skinnyg/Documents/HackISU/HackISU2019Fall/spring/driveshare/src/FBcert.json");
		FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://woven-fountain-257807.firebaseio.com")
				.build();

		FirebaseApp.initializeApp(options);
		
		
	}

}
