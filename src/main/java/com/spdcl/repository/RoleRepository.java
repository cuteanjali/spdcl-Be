package com.spdcl.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spdcl.entity.RoleEntity;
import com.spdcl.entity.TenantEntity;

public interface RoleRepository extends JpaRepository<RoleEntity,UUID> {

	List<RoleEntity> findByTenantEntity(TenantEntity tenantEntity);
	
	List<RoleEntity> findByTenantEntityAndName(TenantEntity tenantEntity,String name);
}
