package com.example.driveshare;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Search {
	
	@RequestMapping("/search")
	public String Search()
	{
		return "sdf";
	}
	

}
