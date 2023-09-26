package com.spdcl.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spdcl.entity.TenantEntity;

public interface TenantRepository extends JpaRepository<TenantEntity,UUID> {
  

	TenantEntity findByTenantCode(String str);
}
