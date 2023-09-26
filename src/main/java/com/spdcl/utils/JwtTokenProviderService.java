package com.spdcl.utils;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.spdcl.service.CustomUserDetailsService;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtTokenProviderService implements IJwtTokenProviderService {


    private CustomUserDetailsService myUserDetailsService;
	
    @Value("${jwt.secret}")
	private String secret;
	@PostConstruct
	protected void init() {
		secret = Base64.getEncoder().encodeToString(secret.getBytes());
	}

    public JwtTokenProviderService(CustomUserDetailsService myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
    }
    @Override
	public String generateToken(String username) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, username);
	}
 
    public String createToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject)
				.setIssuer(ServiceUtils.ISSSUER)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				  .setExpiration(new Date(System.currentTimeMillis()+1000*60*6))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    @Override
    public Authentication validateUserAndGetAuthentication(String token) {
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(getUsername(token));
        Authentication authentication = null;
        if (userDetails != null) {
        	authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        }
        return authentication;
    }

    @Override
    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    @Override
    public String parseToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
        	 System.out.println("Expired or invalid JWT token");
            throw new MyCustomException("Expired or invalid JWT token", HttpStatus.INTERNAL_SERVER_ERROR);
           
        }
    }
}
