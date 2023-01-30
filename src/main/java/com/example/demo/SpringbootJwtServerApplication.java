package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.demo.domain.Customer;
import com.example.demo.repo.CustomersRepo;

@SpringBootApplication
public class SpringbootJwtServerApplication implements CommandLineRunner {
	
	@Autowired
	private CustomersRepo repo;

	public static void main(String[] args) {
		SpringApplication.run(SpringbootJwtServerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	
		Customer cust = new Customer("admin", "admin@gmail.com", "password", true, true, true, true, null);
		repo.save(cust);
		
	}

}
