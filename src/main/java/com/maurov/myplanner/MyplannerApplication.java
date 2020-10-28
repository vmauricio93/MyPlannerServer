package com.maurov.myplanner;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MyplannerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyplannerApplication.class, args);
	}

}
