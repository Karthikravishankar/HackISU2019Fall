package com.example.driveshare;

import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.bigquery.FieldValue;
import com.google.cloud.bigquery.FieldValueList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/search")
public class Search {

	private FireBaseHelper firebaseHelper = new FireBaseHelper();

	@GetMapping
	public ResponseEntity<String> MainSearch() {
		System.out.println("Search page is actived");
		return new ResponseEntity<>("Welcome to search page" , HttpStatus.OK);
	}

	@RequestMapping("/All")
	public String ListAlluser()
	{
		return "";
	}


	@RequestMapping(value = "/getDriver" , method=RequestMethod.POST )
	public String Search(HttpServletRequest request) {
		String User = request.getParameter("User");
		return "";
	}

	@RequestMapping(value = "/Settings" , method=RequestMethod.POST )
	public String Settings(HttpServletRequest request) {
		String User = request.getParameter("User");
		String From = request.getParameter("From");
		String Destination = request.getParameter("Destination");
		String Driving = request.getParameter("Driving");
		firebaseHelper.updateFirebaseData("preferences",User,"From", From);
		firebaseHelper.updateFirebaseData("preferences",User,"Destination", Destination);
		firebaseHelper.updateFirebaseData("preferences",User,"Driving", Driving);
		return "Success";
	}
	
	@RequestMapping(value = "/save" , method=RequestMethod.POST )
	public void SaveDriverRecord(@RequestBody  DriveInfo di)
	{
		Map<String,Object> driveinfo = new HashMap<>();
		driveinfo.put("username", di.username);
		driveinfo.put("drivername", di.drivername);
		driveinfo.put("pickup", di.pickup);
		driveinfo.put("destination", di.destination);
		driveinfo.put("distance", di.distance);
		driveinfo.put("cost", "100");
		driveinfo.put("gasolineSaved", di.gasolineSaved);
		BigQueryHelper bg = new BigQueryHelper();
		bg.InsertIntoTable(driveinfo , "driveinfo");
	}


	@RequestMapping("/{username}")
	public ArrayList<String> SearchPerson(@PathVariable String username) throws InterruptedException
	{
		String query = "SELECT * FROM driveshare.userinfo WHERE username = '" + username + "'";
		
		BigQueryHelper bq = new BigQueryHelper();
		
		Iterable<FieldValueList> temp = bq.executeQuery(query);

		ArrayList<String> result = new ArrayList<>();
		
		for(FieldValueList x : temp)
		{
			for(FieldValue k : x)
			{
				result.add(k.getValue().toString());
			}		
		}
			
		return result;
	}
	

}
