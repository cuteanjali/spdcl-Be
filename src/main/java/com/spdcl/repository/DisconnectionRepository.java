package com.spdcl.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spdcl.entity.DisconnectionEntity;

public interface DisconnectionRepository extends JpaRepository<DisconnectionEntity,UUID>{

	
}
