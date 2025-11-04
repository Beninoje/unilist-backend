package com.unilist.unilist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class UnilistBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(UnilistBackendApplication.class, args);
	}

}
