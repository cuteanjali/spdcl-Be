package com.spdcl.model;

import java.util.List;
import java.util.UUID;

public class AdminUserModel {
	private UUID id;
	private String password;
	private String email;
	private String firstName;
	private String tenantCode;
	private String lastName;
	private String status;
	private List<RoleAssignModel> roles;
	
	
	public List<RoleAssignModel> getRoles() {
		return roles;
	}

	public void setRoles(List<RoleAssignModel> roles) {
		this.roles = roles;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
