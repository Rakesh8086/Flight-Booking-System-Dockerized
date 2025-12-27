package com.spring.login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringLoginApplication {

	public static void main(String[] args) {
		System.out.println("=======================================");
		System.out.println("Testing automatic re-deployment by jenkins");
		System.out.println("=======================================");
		SpringApplication.run(SpringLoginApplication.class, args);
	}

}
