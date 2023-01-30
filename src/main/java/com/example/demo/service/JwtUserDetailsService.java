package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Customer;
import com.example.demo.repo.CustomersRepo;

@Service
public class JwtUserDetailsService implements UserDetailsService {
	
	@Autowired
	private CustomersRepo customerRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<Customer> resp = customerRepo.findByEmail(username);
		if(resp.isEmpty())
			throw new UsernameNotFoundException("No user found with the details");
		return resp.get(0);
	}

}
