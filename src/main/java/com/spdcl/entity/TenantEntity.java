package com.spdcl.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tenant")
public class TenantEntity extends BaseEntity{
	private String tenantName;
	private String tenantCode;
	private String tenantLogo;
	private String status;
	public String getTenantName() {
		return tenantName;
	}
	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}
	public String getTenantCode() {
		return tenantCode;
	}
	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}
	public String getTenantLogo() {
		return tenantLogo;
	}
	public void setTenantLogo(String tenantLogo) {
		this.tenantLogo = tenantLogo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
