package com.spdcl.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.spdcl.entity.UserEntity;
import com.spdcl.repository.AdminUserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
   // @Autowired
   // private UserRepository repository;
    @Autowired
    private AdminUserRepository adminUserRepository;
    @Autowired
    private UserService userService;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
     //   UserEntity user = repository.findByEmailAndStatusAndTenantCode(username, "Active", userService.getTenantCode());
        UserEntity userAdmin = adminUserRepository.findByUserNameAndStatusAndTenantEntity(username, "Active", userService.getTenantCode());
        UserDetails details = null;
       /* if (user != null) {
        	details =  new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
        }*/
        if (userAdmin != null) {
        	 details = new org.springframework.security.core.userdetails.User(userAdmin.getUserName(), userAdmin.getPassword(), new ArrayList<>());
        }
		return details;
    }
    
}
