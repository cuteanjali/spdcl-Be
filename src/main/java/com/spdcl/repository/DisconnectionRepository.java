package com.spdcl.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.spdcl.entity.DisconnectionEntity;

public interface DisconnectionRepository extends JpaRepository<DisconnectionEntity,UUID>{

	
	
	@Query(value = "select * FROM disconnection_table where name LIKE lower(concat('%', concat(:name, '%'))) or meter LIKE lower(concat('%', concat(:name, '%'))) or consumer LIKE lower(concat('%', concat(:name, '%')))", nativeQuery = true)
	List<DisconnectionEntity> getAllRecords(@Param("name") String name);
	
	@Query(value = "select * from disconnection_table where name like %?1% or meter like %?1% or consumer like %?1% or reading like %?1%",nativeQuery = true)
	Page<DisconnectionEntity> fullTextSearch(String text, Pageable pageable);

	List<DisconnectionEntity> findByConsumer(String name);	
}
