package com.spdcl.utils;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;

public interface IJwtTokenProviderService {
	String generateToken(String username);
    Authentication validateUserAndGetAuthentication(String token);
    String getUsername(String token);
    String parseToken(HttpServletRequest req);
    boolean validateToken(String token);

}
