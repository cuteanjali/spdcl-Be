package com.spdcl.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Table(name = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity extends BaseEntity{
	@Column(name = "userName", nullable = false, length = 100)
	private String userName;
	@Column(name = "password", nullable = false, length = 250)
	private String password;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "tenant_id")
	private TenantEntity tenantEntity;
	
	@Column(name = "status", nullable = true, length = 10)
	private String status;

	
	
}
