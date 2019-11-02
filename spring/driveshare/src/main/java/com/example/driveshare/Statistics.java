package com.example.driveshare;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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
	public String stat(HttpServletRequest request) {
		System.out.println("s");
		return "stat";
	}

	@RequestMapping("/test")
	public String StatisticsTest()
	{
		return "Test statistics";
		
	}

}
