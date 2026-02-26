package com.unilist.campora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CamporaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CamporaBackendApplication.class, args);
	}

}
