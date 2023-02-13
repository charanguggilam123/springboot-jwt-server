package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.demo.filter.JWTFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
	
	@Autowired
	private JWTFilter filter;
	
	@Bean
 	public WebSecurityCustomizer webSecurityCustomizer() {
 		return (web) -> web.ignoring()
 		// Spring Security should completely ignore URLs starting with /resources/
 				.requestMatchers("/resources/**")
 				.requestMatchers(new AntPathRequestMatcher("/h2-console/**"));
 	}

 	@Bean
 	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
 		
 		CorsConfigurationSource corsSource = request -> {
 			CorsConfiguration config = new CorsConfiguration();
 			config.addAllowedOrigin("http://localhost:4200");
// 			config.setAllowedHeaders(List.of("*"));
 			config.applyPermitDefaultValues();
 			return config;
 			
 		};
 		http.csrf().disable().cors(t ->	t.configurationSource(corsSource))
 		.authorizeHttpRequests()
 		.requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
 		.requestMatchers("/authenticate","/decode-jwt","/register").permitAll()
 		.anyRequest()
 		.authenticated();
 		// describing where this filter needs to be placed. In our case authentication filter
 		http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
 		return http.build();
 	}
 	
 	/*
 	 * No need to provide authentication provider because spring boot defaults to
 	 * DAOAuthenticationProvider with UserDetailsServiceBean and password encoder bean
 	 * which in this case our custom implementation of user details service and
 	 * bcrypt encoder declared below
 	 * */
 	@Bean
 	AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
 		return config.getAuthenticationManager();
 		
 	}
 	
 	@Bean
 	  PasswordEncoder encoder() {
 	    return new BCryptPasswordEncoder();
 	  }


}
