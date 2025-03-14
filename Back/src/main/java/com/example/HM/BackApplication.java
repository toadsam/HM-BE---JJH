package com.example.HM;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackApplication {

	public static void main(String[] args) {
		System.out.println("OPENAI_API_KEY from System.getenv(): " + System.getenv("OPENAI_API_KEY"));
		SpringApplication.run(BackApplication.class, args);
	}


}
