package com.spdcl.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spdcl.entity.TenantEntity;
import com.spdcl.entity.UserEntity;

public interface AdminUserRepository extends JpaRepository<UserEntity,UUID> {
  
	UserEntity findByUserNameAndStatusAndTenantEntity(String userName, String status, TenantEntity tenantCode);
  
	//UserEntity findByUserNameAndTenantEntity(String userName, TenantEntity tenantCode);
    
    UserEntity findByUserNameAndTenantEntity(String userName, TenantEntity tenantCode);
    
    List<UserEntity> findByTenantEntity(TenantEntity tenantCode);
}
