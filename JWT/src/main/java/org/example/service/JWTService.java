package org.example.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import java.security.Key;
import java.util.*;

@Service
public class JWTService {
	private final String secret;

	public JWTService() {
		// Dynamic Secret (Each Server Restart, you can't login with old token [SECRET CHANGED])
		// You can make it static string to save the logged in user status
		this.secret = generateSecretKey();
	}

	private String generateSecretKey() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
			SecretKey secretKey = keyGenerator.generateKey();
			System.out.println("SecretKey: " + secretKey);
			return Base64.getEncoder().encodeToString(secretKey.getEncoded());
		}
		catch (Exception e) {
			throw new RuntimeException("Error while generating secret key: " + e);
		}
	}

	private Key getKey() {
		byte[] decoded = Decoders.BASE64.decode(secret);
		return Keys.hmacShaKeyFor(decoded);
	}

	public String generateToken(String username) {
		return Jwts.builder()
				   .setClaims(new HashMap<>())
				   .setSubject(username)
				   .setIssuedAt(new Date())
				   .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24)))
				   .signWith(getKey(), SignatureAlgorithm.HS256)
				   .compact();
	}

	public String extractUsername(String token) {
		return extractClaims(token).getSubject();
	}

	private Claims extractClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();
	}

	public boolean isValidToken(String token, UserDetails userDetails) {
		String username = extractUsername(token);
		return username != null && username.equals(userDetails.getUsername()) && extractClaims(token).getExpiration()
																									 .after(new Date());
	}
}
