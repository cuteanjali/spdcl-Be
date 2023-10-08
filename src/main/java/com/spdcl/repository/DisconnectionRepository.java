package com.spdcl.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.spdcl.entity.DisconnectionEntity;

public interface DisconnectionRepository extends JpaRepository<DisconnectionEntity,UUID>{

	
	
	@Query(value = "select * FROM disconnection_table where name LIKE lower(concat('%', concat(:name, '%'))) or meter LIKE lower(concat('%', concat(:name, '%')))", nativeQuery = true)
	List<DisconnectionEntity> getAllRecords(@Param("name") String name);
}
