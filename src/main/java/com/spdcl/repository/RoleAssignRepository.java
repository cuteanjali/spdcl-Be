package com.spdcl.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spdcl.entity.RoleAssignEntity;
import com.spdcl.entity.RoleEntity;
import com.spdcl.entity.UserEntity;

public interface RoleAssignRepository extends JpaRepository<RoleAssignEntity,UUID> {

	List<RoleAssignEntity> findByRoleEntityAndUserEntity(RoleEntity RoleEntity,UserEntity userEntity);
}
