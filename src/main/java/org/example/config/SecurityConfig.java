package org.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private JWTFilter jWTFilter;

	//	Used If you need to customize security filters
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
	{
		http
				// Disable CSRF if working with APIs
				.csrf(AbstractHttpConfigurer::disable)
				// Authenticate Every Request
				.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.POST, "/user", "/login")
												   .permitAll()
												   .anyRequest()
												   .authenticated())
				// Enable default configuration of http (API)
				.httpBasic(Customizer.withDefaults())
				// Enable default form login if working web
				//	.formLogin(Customizer.withDefaults())
				// Make the session creation stateless for APIs
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				// Add JWT Filter before UsernamePassword Filter
				.addFilterBefore(jWTFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public AuthenticationProvider authenticationProvider()
	{
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(new BCryptPasswordEncoder(12));
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception
	{
		return configuration.getAuthenticationManager();
	}

	//	Spring security checks for this method to get user data (In memory)
	/*
	@Bean
	public UserDetailsService userDetailsService()
	{
		//	Define a custom user for in memory user manager
		UserDetails user = User
				//	Define the password encoder
				.withDefaultPasswordEncoder()
				//	Define user data
				.username("user").password("password").roles("USER").build();
		//	Define the way of storing users data (in memory)
		return new InMemoryUserDetailsManager(user);
	}
	*/
}
