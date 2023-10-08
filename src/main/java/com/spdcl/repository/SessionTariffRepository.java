package com.spdcl.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spdcl.entity.SessionTariffEntity;
import com.spdcl.entity.TenantEntity;

public interface SessionTariffRepository extends JpaRepository<SessionTariffEntity,UUID> {
	  

	List<SessionTariffEntity> findByTenantEntity(TenantEntity tenantEntity);

	List<SessionTariffEntity> findByTenantEntityAndTariffTypeAndPhaseTypeAndSessionIn(TenantEntity tenantEntity, String tariffType, String phaseType, List<String> sessions);
	
	
	
}
