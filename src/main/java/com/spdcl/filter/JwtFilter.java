package com.spdcl.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.spdcl.utils.JwtTokenProviderService;
import com.spdcl.utils.MyCustomException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProviderService jwtTokenProviderService;
  
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
    	String token = jwtTokenProviderService.parseToken(httpServletRequest);
        try {
            if (token != null && jwtTokenProviderService.validateToken(token)) {
                Authentication auth = jwtTokenProviderService.validateUserAndGetAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (MyCustomException ex) {
            SecurityContextHolder.clearContext();
            httpServletResponse.sendError(ex.getHttpStatus().value(), ex.getMessage());
            return;
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
