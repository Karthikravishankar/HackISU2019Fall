package com.example.driveshare;

import java.awt.List;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import com.google.cloud.bigquery.InsertAllRequest;
import com.google.cloud.bigquery.InsertAllResponse;
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
	
	@RequestMapping("/languages")
	public String allLanguage() throws InterruptedException
	{
		BigQueryHelper bq = new BigQueryHelper();
		Iterable<FieldValueList> collection = bq.executeQuery("Select * from driveshare.Languageinfo ");
		String result = "";
		for(FieldValueList fields : collection)
		{
			result += fields.get(0).toString() + ",";
		}
		
		return result;
		
	}


	@RequestMapping(value = "/getDriver" , method=RequestMethod.POST )
	public String Search(HttpServletRequest request) throws JSONException, IOException, InterruptedException {
		String username = request.getParameter("username");
		String destination = request.getParameter("destination");
		String type = request.getParameter("type");
		String latitude = request.getParameter("latitude");
		String longitude = request.getParameter("longitude");
		String destlatitude = request.getParameter("dest_lat");
		String destlongitude = request.getParameter("dest_lng");
		AI ai =  new AI(getUserPreferences(username),username,latitude,longitude,destlatitude,destlongitude);
		System.out.println(ai.getHashMap()==null);
		System.out.println(ai.getHashMap().toString());
		if(ai.getHashMap()!=null){
			String result = ai.getHashMap().toString();
			System.out.println(result);
			return result;
		}
		else {
			return "NA";
		}
	}

	@RequestMapping(value = "/WorkFlowStatus", method = RequestMethod.POST)
	public String workFlow(HttpServletRequest request) throws IOException, JSONException, InterruptedException {
		String username = request.getParameter("username");
		String val = firebaseHelper.getFirebaseData("userinfo",username,"status");
		return val;
	}

	@RequestMapping(value = "/getFireBaseData", method = RequestMethod.POST)
	public String getJourneyCoordinates(HttpServletRequest request) throws IOException, InterruptedException {
		String search = request.getParameter("search");
		String object = request.getParameter("object");
		String tableName = request.getParameter("tableName");
		String toReturn = firebaseHelper.getFirebaseData(tableName,object,search);
		return toReturn;
	}

	@RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
	public String deleteUser(HttpServletRequest request) throws IOException, JSONException, InterruptedException {
		String username = request.getParameter("username");
		String table = request.getParameter("table");
		return firebaseHelper.deleteFirebaseData(table,username);
	}

	@RequestMapping(value = "/getCustomerName", method = RequestMethod.POST)
	public String getCustomerName(HttpServletRequest request) throws IOException, JSONException, InterruptedException {
		String username = request.getParameter("username");
		String val = firebaseHelper.getFirebaseData("userinfo",username,"customername");
		return val;
	}


	@RequestMapping(value = "/updateFireBase", method = RequestMethod.POST)
	public String updateFireBaseRequestHandler(HttpServletRequest request) throws IOException, JSONException {
		String tableName = request.getParameter("tableName");
		String object = request.getParameter("object");
		String jsonString = request.getParameter("jsonString");
		JSONObject jsonObject = new JSONObject(jsonString);
		Iterator<String> keys = jsonObject.keys();
		while(keys.hasNext()) {
			String key = keys.next();
			firebaseHelper.updateFirebaseData(tableName,object,key, String.valueOf(jsonObject.get(key)));
		}
		return "Successfully updated Settings";
	}

	public JSONObject getUserPreferences(String User) throws IOException, InterruptedException, JSONException {
		JSONObject jsonObject = new JSONObject();

		String Movies = firebaseHelper.getFirebaseData("preferences",User,"Movies");
		String Vacation = firebaseHelper.getFirebaseData("preferences",User,"Vacation");
		String Music = firebaseHelper.getFirebaseData("preferences",User,"Music");
		String Sports = firebaseHelper.getFirebaseData("preferences",User,"Music");
		String Pets = firebaseHelper.getFirebaseData("preferences",User,"Music");
		String Drink = firebaseHelper.getFirebaseData("preferences",User,"Music");
		String Food = firebaseHelper.getFirebaseData("preferences",User,"Music");
		String Age = firebaseHelper.getFirebaseData("preferences",User,"Music");
		String TalkorListener = firebaseHelper.getFirebaseData("preferences",User,"TalkorListener");

		jsonObject.put("Movies", Movies);
		jsonObject.put("Vacation", Vacation);
		jsonObject.put("Music", Music);
		jsonObject.put("Sports", Sports);
		jsonObject.put("Pets", Pets);
		jsonObject.put("Drink", Drink);
		jsonObject.put("Food", Food);
		jsonObject.put("Age", Age);
		jsonObject.put("TalkorListener", TalkorListener);
		return jsonObject;
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

	@RequestMapping(value = "/saveDriveInfo" , method=RequestMethod.POST )
	public String saveDriveInfo(HttpServletRequest request) {
		Map<String,String> driveinfo = new HashMap<>();
		String date = request.getParameter("date");
		String username = request.getParameter("username");
		String drivername = request.getParameter("drivername");
		String pickup = request.getParameter("pickup");
		String destination = request.getParameter("destination");
		String distance = request.getParameter("distance");
		String cost = request.getParameter("cost");
		String gasoline = request.getParameter("gasolineSaved");

		driveinfo.put("date", date);
		driveinfo.put("username", username);
		driveinfo.put("drivername", drivername);
		driveinfo.put("pickup", pickup);
		driveinfo.put("destination", destination);
		driveinfo.put("distance", distance);
		driveinfo.put("cost", cost);
		driveinfo.put("gasolineSaved", gasoline);
		BigQueryHelper bg = new BigQueryHelper();
		bg.InsertIntoTableP2(driveinfo , "driveinfo");


//		InsertAllRequest insertRequest =
//				InsertAllRequest.newBuilder(bg.getTableByName("driveinfo").getTableId()).addRow(driveinfo).build();
		// Insert rows
//		InsertAllResponse insertResponse = bg.
		// Check if errors occurred
//		if (insertResponse.hasErrors()) {
//			System.out.println("Errors occurred while inserting rows");
//		}
		return "success";
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

	
	
	@RequestMapping("/transSound/{text}/{input}/{output}")
	public String TransSound(@PathVariable String text , @PathVariable String input , @PathVariable String output) throws IOException, Exception
	{
		TextToSound.getSound(Translator.translatelanguage(text, language.SearchLanguage(input),language.SearchLanguage(output)), language.SearchLanguage(output));
		return "good";
	}
	
	@RequestMapping(value = "/soundtest", method = RequestMethod.POST)
	public String soundtest(HttpServletRequest request) throws IOException, JSONException
	{
		String username = request.getParameter("username");
		String customername = request.getParameter("customer");
		byte[] temp = TextToSound.getSound("hello", "en-US");
		return Base64.encodeBase64String(TextToSound.getSound("You two have the same preferences in Movies, and Sports.", "en-US"));
		
		
		//return Base64.encodeBase64String(TextToSound.getSound("hello", "en-US"));
	}

	@RequestMapping("/translate/{request}")
	public String translate(@PathVariable String request) throws Exception {
		String input = request;
		String type = Translator.detect(input);
		if(!type.equals("en"))
			return Base64.encodeBase64String((TextToSound.getSound(Translator.translatelanguage(input,type,"en") , "en")));
		else
			return Base64.encodeBase64String((TextToSound.getSound(input,type)));
	}


	@RequestMapping("/soundtotext/{input}/{output}")
	public String soundtotext(@RequestBody String file , @PathVariable String input , @PathVariable String output) throws Exception
	{
		String temp = file.substring(6);
		byte[] data = Base64.decodeBase64(temp);

		try(OutputStream out = new FileOutputStream("test.mp3"))
		{
			out.write(data);
		}

	return "finish";
		//return SoundToText.trans(data , input);
	}
	
	@RequestMapping("/soundtotext")
	public String soundtotext() throws Exception
	{

		return SoundToText.testTran();


	}

	private String find_language(String input) throws InterruptedException {
		BigQueryHelper bq = new BigQueryHelper();
		Iterable<FieldValueList> collection = bq.executeQuery("Select * from driveshare.Languageinfo ");
		String result = "";
		for(FieldValueList fields : collection)
		{
			if(fields.get(0).getValue().toString().toLowerCase().equals(input.toLowerCase()))
			{
				return fields.get(1).getValue().toString().toLowerCase();
			}
		}

		return null;
	}

}
