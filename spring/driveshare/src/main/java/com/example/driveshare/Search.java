package com.example.driveshare;

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
		System.out.println("Search page is actived");
		return new ResponseEntity<>("Welcome to search page" , HttpStatus.OK);
	}
	
	
	@RequestMapping("/sdf")
	public String Search()
	{
		return "sdf";
	}
	

}
