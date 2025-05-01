package org.example.service;

import org.example.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class UserDetailsImpl implements UserDetails
{
	private final User user;

	public UserDetailsImpl(User user)
	{
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities()
	{
		return List.of(() -> "ROLE_USER");
	}

	@Override
	public String getPassword()
	{
		return this.user.getPassword();
	}

	@Override
	public String getUsername()
	{
		return this.user.getUsername();
	}
}
