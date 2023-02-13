package com.example.demo.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTFilter extends OncePerRequestFilter {
	
	@Autowired
	private UserDetailsService service;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	private static final Logger LOG= LoggerFactory.getLogger(JWTFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		final String authHeader = request.getHeader("Authorization");
	    final String jwtToken;
	    final String userEmail;
	    if (authHeader == null || !authHeader.startsWith("Bearer")) {
	      System.out.println("No token found");
	      filterChain.doFilter(request, response); /*next filter i.e uesrnamepassswordAuhentication filter will throw error if path is protected and username details are not set in the context of securitycontextholder*/
	      return;
	    }
	    jwtToken = authHeader.substring(7);
	    userEmail = jwtUtil.getUsernameFromToken(jwtToken);
	    System.out.println("userEmail: "+userEmail);
	    
	    if(userEmail!=null && SecurityContextHolder.getContext().getAuthentication() == null ) {
	    	UserDetails userDetails = service.loadUserByUsername(userEmail);
	    	if (jwtUtil.validateToken(jwtToken, userDetails)) {
	            LOG.info("valid token receiveds");
	            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
	                userDetails, null, userDetails.getAuthorities());

	            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

	            SecurityContextHolder.getContext().setAuthentication(authToken);
	          }
	    }
		
		
		filterChain.doFilter(request, response);
		
	}

}
