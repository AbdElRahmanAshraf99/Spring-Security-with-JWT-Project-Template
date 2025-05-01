package org.example.controller;

import org.example.model.User;
import org.example.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
	@Autowired
	private UserDetailsServiceImpl userService;
	@Autowired
	private JWTService jwtService;
	@Autowired
	private AuthenticationManager authenticationManager;

	@PostMapping(value = "/user")
	public ResponseEntity<String> register(@RequestBody User user)
	{
		User finalUser;
		try {
			finalUser = userService.register(user);
		}
		catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok("User created with id: " + finalUser.getId());
	}

	@PostMapping(value = "/login")
	public ResponseEntity<String> login(@RequestBody User user)
	{
		try {
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					user.getUsername(),
					user.getPassword()));
			if (authentication.isAuthenticated()) {
				return ResponseEntity.ok(jwtService.generateToken(user.getUsername()));
			}
			else {
				return ResponseEntity.badRequest().body("Incorrect username or password");
			}
		}
		catch (BadCredentialsException e) {
			return ResponseEntity.badRequest().body("Incorrect username or password");
		}
	}

}
