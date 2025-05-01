package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
public class HomeController
{
	@GetMapping("/home")
	public String home(HttpServletRequest request)
	{
		return "Home\nSession Id: " + request.getSession().getId();
	}

	@GetMapping("/csrf-token")
	public CsrfToken token(HttpServletRequest request)
	{
		return (CsrfToken) request.getAttribute("_csrf");
	}
}
