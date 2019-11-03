package com.example.driveshare;

import java.awt.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
import com.google.protobuf.ByteString;

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
		System.out.print("sdfa");
		return "hh";
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
		String from_lat = request.getParameter("from_lat");
		String from_lng = request.getParameter("from_lng");
		String dest_lat = request.getParameter("dest_lat");
		String dest_lng= request.getParameter("dest_lng");

		firebaseHelper.updateFirebaseData("preferences",User,"From", From);
		firebaseHelper.updateFirebaseData("preferences",User,"Destination", Destination);
		firebaseHelper.updateFirebaseData("preferences",User,"Driving", Driving);
		firebaseHelper.updateFirebaseData("preferences",User,"from_lat", from_lat);
		firebaseHelper.updateFirebaseData("preferences",User,"from_lng", from_lng);
		firebaseHelper.updateFirebaseData("preferences",User,"dest_lat", dest_lat);
		firebaseHelper.updateFirebaseData("preferences",User,"dest_lng", dest_lng);
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
	
	
	@RequestMapping("/transSound_{text}")
	public String TransSound(@PathVariable String text) throws IOException, Exception
	{
		TextToSound.getSound(Translator.translatelanguage(text, "zh"), "en-US");
		return "good";
	}
	
	@RequestMapping("/soundtest")
	public String soundtest() throws IOException, JSONException
	{
		byte[] temp = TextToSound.getSound("hello", "en-US");
		return Base64.encodeBase64String(TextToSound.getSound("hello", "en-US"));
		
		
		//return Base64.encodeBase64String(TextToSound.getSound("hello", "en-US"));
	}
	
	@RequestMapping("/soundtotext")
	public String soundtotext() throws Exception
	{
		return SoundToText.trans();
	}

}
