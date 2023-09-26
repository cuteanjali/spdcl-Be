package com.spdcl.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spdcl.entity.RefreshToken;
import com.spdcl.entity.TenantEntity;
import com.spdcl.repository.AdminUserRepository;
import com.spdcl.repository.RefreshTokenRepository;
import com.spdcl.repository.TenantRepository;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private AdminUserRepository userInfoRepository;

    @Autowired
	private TenantRepository tenantRepository; 
    
    public RefreshToken createRefreshToken(String username, String tenantCode) {
    	TenantEntity tenantEntity1  = tenantRepository.findByTenantCode(tenantCode);
        RefreshToken refreshToken = RefreshToken.builder().userEntity(userInfoRepository.findByUserNameAndTenantEntity(username, tenantEntity1).get())
        	.token(UUID.randomUUID().toString())
        	
        	.expiryDate(Instant.now().plusMillis(51600000)).build();
        refreshToken.setCreatedDate(new Date());  
        refreshToken.setModifiedDate(new Date());  
        return refreshTokenRepository.save(refreshToken);
    }


    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }


    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

}
