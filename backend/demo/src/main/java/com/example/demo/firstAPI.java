package com.example.demo;

import org.springframework.web.bind.annotation.*;

	@RestController
	public class firstAPI {

	    @GetMapping("/")
	    public String hello() {
	        return "Hi..Jay Bhardwaj lets start Spring Boot!";
	    }
	}



