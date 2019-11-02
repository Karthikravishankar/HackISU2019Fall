package com.example.driveshare;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class Search {
	
	@GetMapping
	public ResponseEntity<String> MainSearch()
	{
		//simple comment test git
		System.out.println("Search page is actived");
		return new ResponseEntity<>("Welcome to search page" , HttpStatus.OK);
	}
	
	
	@RequestMapping("/test")
	public String Search()
	{
		return "test";
	}
	

}
