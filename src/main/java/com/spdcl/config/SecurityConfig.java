package com.spdcl.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.spdcl.filter.JwtFilter;
import com.spdcl.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	
	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private JwtFilter jwtFilter;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}

	@Bean(name = BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests().antMatchers("/v1/forgotPass/**","/v1/login/**","/v1/refreshToken/**","/v1/saveUser/**","/v1/downloadDisconnection/**")

		.permitAll().anyRequest().authenticated()
		.and().exceptionHandling().and().sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().cors();
		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
	}
	@Override
	public void configure(WebSecurity web) throws Exception {
		// Allow swagger to be accessed without authentication
		web.ignoring().antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui/index.html/**")//
		.antMatchers("/swagger-resources/**")//
		.antMatchers("/swagger-ui.html")//
		.antMatchers("/configuration/**")//
		.antMatchers("/webjars/**")//
		.antMatchers("/public")


		// Un-secure H2 Database (for testing purposes, H2 console shouldn't be unprotected in production)
		.and()
		.ignoring()
		.antMatchers("/v1/saveTenant/**","/images/**", "/v1/login/**");
	}
}