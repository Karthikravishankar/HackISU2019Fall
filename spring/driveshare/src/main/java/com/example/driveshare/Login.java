package com.example.driveshare;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/login")
public class Login {

	@Autowired
	Server server;
	
    @GetMapping
    public ResponseEntity<String> getHome() throws IOException {
        System.out.println("user logged");
        return new ResponseEntity<>(
                "1dayumay",
                HttpStatus.OK);
    }

    @RequestMapping(value = "/validateLogin", method = RequestMethod.POST)
    public String getJourneyCoordinates(HttpServletRequest request) throws IOException, InterruptedException {
        return "";
    }
}