package com.example.demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.Customer;

public interface CustomersRepo extends JpaRepository<Customer, Long> {

	List<Customer> findByUsername(String username);
	List<Customer> findByEmail(String email);

}
