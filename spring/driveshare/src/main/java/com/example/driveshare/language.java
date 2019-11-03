package com.example.driveshare;

import com.google.cloud.bigquery.FieldValueList;

public class language {
	
	
	public static String SearchLanguage(String language) throws InterruptedException
	{
		BigQueryHelper bq = new BigQueryHelper();
		Iterable<FieldValueList> collection  = bq.executeQuery("Select * from driveshare.Languageinfo ");
		
		for(FieldValueList fields : collection)
		{
			if(fields.get(0).getValue().toString().equals(language.toLowerCase()))
				return fields.get(1).getValue().toString();
		}
		
		return "en";
		
	}
	
	
	

}
