package com.spdcl.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "role_assign")
@Data
public class RoleAssignEntity extends BaseEntity{

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "role_id")
	private RoleEntity roleEntity;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private UserEntity userEntity;
	
}
