package com.lifespace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LifeSpaceSprApplication {

	public static void main(String[] args) {
		SpringApplication.run(LifeSpaceSprApplication.class, args);
	}

}
