package com.example.driveshare;

import com.google.cloud.bigquery.FieldValue;
import com.google.cloud.bigquery.FieldValueList;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

@RestController
@RequestMapping("/statistics")
public class Statistics {

	
	@GetMapping
	public ResponseEntity<String> MainStatistics()
	{
		System.out.println("Statistics page on");
		
		return new ResponseEntity<>("Server Connected",HttpStatus.OK);
	}

	@RequestMapping(value = "/Stat" , method= RequestMethod.POST )
	public String stat(HttpServletRequest request) throws InterruptedException, JSONException {
		String username = request.getParameter("username");
		String query = "SELECT * FROM driveshare.driveinfo WHERE username = '" + username + "'";

		BigQueryHelper bq = new BigQueryHelper();

		Iterable<FieldValueList> temp = bq.executeQuery(query);

		JSONObject toSend = new JSONObject();

		for(FieldValueList x : temp)
		{
			JSONObject tempy = new JSONObject();
			tempy.put(String.valueOf(tempy.length()), x.get("date").getValue().toString());
			tempy.put(String.valueOf(tempy.length()), x.get("drivername").getValue().toString());
			tempy.put(String.valueOf(tempy.length()), x.get("cost").getValue().toString());
			tempy.put(String.valueOf(tempy.length()), x.get("gasolineSaved").getValue().toString());
			toSend.put(String.valueOf(toSend.length()),tempy.toString());
		}
		String val = toSend.toString();
		return val;
	}

	@RequestMapping("/test")
	public String StatisticsTest()
	{
		return "Test statistics";
		
	}

}
