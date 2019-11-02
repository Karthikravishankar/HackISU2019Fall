package com.example.driveshare;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

@RestController
@RequestMapping("/search")
public class Search {

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
	
	
	@RequestMapping("/test")
	public String Search()
	{
		

		return "test";
	}
	
	@RequestMapping("/save")
	public void SaveDriverRecord()
	{
		Map<String,Object> temp = new HashMap<>();
		temp.put("username", "test");
		temp.put("drivername", "test");
		temp.put("pickup", "test");
		temp.put("destination", "test");
		temp.put("distance", "test");
		temp.put("gasolineSaved", "test");
		BigQueryHelper bg = new BigQueryHelper();
		bg.InsertIntoTable(temp , "driveinfo");
	}


	@RequestMapping("/{username}")
	public String SearchPerson(@PathVariable String username)
	{
		Iterable<String> temp = null;
		
		for(String x : temp)
		{
			if(x.equals(username))
			{
				return x;
			}
		}
		
		return null;
	}
	

}
