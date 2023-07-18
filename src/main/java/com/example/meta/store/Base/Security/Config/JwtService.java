package com.example.meta.store.Base.Security.Config;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.meta.store.Base.ErrorHandler.JWTExpired;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;



@Service
public class JwtService {

	private static final String SECRET_KEY = "2A472D4B6150645367556B58703273357638792F423F4528482B4D6251655468";
	
	public String extractUserName(String token) {
		return extractClaim(token , Claims::getSubject); 
	}
	
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	
	public String generateToken(UserDetails user) {
		return generateToken(new HashMap<>(), user);
	}
	public String generateToken(Map<String,Object> extraClaims, UserDetails userDetails) {
		return Jwts
				.builder()
				.setClaims(extraClaims)
				.setSubject(userDetails.getUsername())
				.claim("Authorization", userDetails.getAuthorities())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 10000000 * 24 * 7))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256)
				.compact();
	}
	
	public Boolean isTokenValid(String token, UserDetails userDetails) {
		final String userName = extractUserName(token);
		try {
		return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
		 } catch (JWTExpired e) {
		        return false;
		    }
	}
	
	private boolean isTokenExpired(String token)  {
		return extractExpration(token).before(new Date());
	}

	

	private Date extractExpration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	private Claims extractAllClaims(String token) {
		try {
		return Jwts
				.parserBuilder()
				.setSigningKey(getSignInKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
		} catch (ExpiredJwtException e) {
	        throw new JWTExpired("Token expired");
	    }
	}

	private Key getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
		
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
