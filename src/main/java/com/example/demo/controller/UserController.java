package com.example.demo.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Customer;
import com.example.demo.domain.Role;
import com.example.demo.repo.CustomersRepo;
import com.example.demo.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.servlet.http.HttpServletRequest;

@RestController
//@CrossOrigin(origins = {"http://localhost:4200","http://localhost:59732"})
public class UserController {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserDetailsService userService;

	@Autowired
	private CustomersRepo repo;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@GetMapping("/test")
	public String test(final HttpServletRequest request) throws Exception {
		System.out.println("In ep");
		return "testtttt";

	}

	@PostMapping("/register")
	public String register(@RequestBody ObjectNode jwtRequest, final HttpServletRequest request) {
		Customer user = new Customer(jwtRequest.get("firstName").asText() + jwtRequest.get("lastName").asText(),
				jwtRequest.get("email").asText(), encoder.encode(jwtRequest.get("password").asText()), true, true, true,
				true, Role.USER);

		repo.save(user);
		return jwtUtil.generateToken(user);

	}

	@PostMapping("/authenticate")
	// end point to generate a new token after validating the creds passed in the
	// request
	public ResponseEntity<String> authenticate(@RequestBody ObjectNode jwtRequest, final HttpServletRequest request) {
		try {
			// validating if creds passed are valid so token will be generated only then
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtRequest.get("email").asText(),
					jwtRequest.get("password").asText()));
		} catch (BadCredentialsException e) {
			System.out.println("Bad creds");
			e.printStackTrace();
			throw new RuntimeException("INVALID_CREDENTIALS", e);
		}
		UserDetails ud = userService.loadUserByUsername(jwtRequest.get("email").asText());
		String token = jwtUtil.generateToken(ud);

		return new ResponseEntity<>(token, HttpStatus.OK);
	}

	// Ref: https://www.baeldung.com/java-jwt-token-decode
	@PostMapping("/decode-jwt")
	public ResponseEntity<ObjectNode> decode(
			@RequestParam(required = false, defaultValue = "false", name = "verify-sign") boolean verifySign,
			@RequestBody ObjectNode jwtRequest, final HttpServletRequest request) throws JsonProcessingException {
		ObjectNode response = new ObjectMapper().createObjectNode();
		System.out.println(verifySign);
		
		Optional<String> secret = Optional.empty();
		if (jwtRequest.has("secret"))
			secret = Optional.of(jwtRequest.get("secret").asText());
		try {
			response = jwtUtil.decode(jwtRequest.get("token").asText(), verifySign, secret);
			System.out.println(response.toString());
		}catch(Exception e) {
			e.printStackTrace();
//			response.put("error", "Internal server error occurred");
			return ResponseEntity.internalServerError().body(response);
		}
		return ResponseEntity.ok(response);
	}
}
