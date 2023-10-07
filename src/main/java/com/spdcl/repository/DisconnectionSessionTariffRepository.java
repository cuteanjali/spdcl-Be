package com.spdcl.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spdcl.entity.DisconnectionEntity;
import com.spdcl.entity.DisconnectionSessionTariffEntity;
import com.spdcl.entity.SessionTariffEntity;
public interface DisconnectionSessionTariffRepository extends JpaRepository<DisconnectionSessionTariffEntity,UUID>{
 
	List<DisconnectionSessionTariffEntity> findByDisconnectionEntity(DisconnectionEntity disconnectionEntity);
	
	List<DisconnectionSessionTariffEntity> findBySessionTariffEntityIn(List<SessionTariffEntity> disconnectionEntity);
	
}
