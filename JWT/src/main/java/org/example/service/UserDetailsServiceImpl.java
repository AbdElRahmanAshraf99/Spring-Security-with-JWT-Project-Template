package org.example.service;

import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService
{

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		User user = userRepository.findByUsername(username);
		if (user == null)
			throw new UsernameNotFoundException(username);
		return new UserDetailsImpl(user);
	}

	public User register(User user) throws RuntimeException
	{
		validateUser(user);
		user.setPassword(new BCryptPasswordEncoder(12).encode(user.getPassword()));
		return userRepository.save(user);
	}

	private void validateUser(User user) throws RuntimeException
	{
		if (user == null)
			throw new RuntimeException("No User data!");
		if (user.getUsername() == null || user.getUsername().isEmpty())
			throw new RuntimeException("No Username provided!");
		if (user.getPassword() == null || user.getPassword().isEmpty())
			throw new RuntimeException("No Password provided!");
		if (user.getPassword().length() < 6)
			throw new RuntimeException("Password must be at least 6 characters!");
		if (user.getUsername().length() < 6)
			throw new RuntimeException("Username must be at least 6 characters!");
		if (user.getPassword().chars().noneMatch(Character::isUpperCase))
			throw new RuntimeException("Password must be an upper case letter!");
		if (user.getPassword().chars().allMatch(Character::isLetterOrDigit))
			throw new RuntimeException("Password must contain a special character!");
	}
}
