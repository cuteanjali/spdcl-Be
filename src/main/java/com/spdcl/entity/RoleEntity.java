package com.spdcl.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "role")
public class RoleEntity extends BaseEntity{

	private String name;
	private String descName;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "tenant_id")
	private TenantEntity tenantEntity;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

	public String getDescName() {
		return descName;
	}

	public void setDescName(String descName) {
		this.descName = descName;
	}

	public TenantEntity getTenantEntity() {
		return tenantEntity;
	}

	public void setTenantEntity(TenantEntity tenantEntity) {
		this.tenantEntity = tenantEntity;
	}
	
	
}
