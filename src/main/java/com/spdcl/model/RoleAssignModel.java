package com.spdcl.model;

import java.util.UUID;

public class RoleAssignModel {

	private UUID id;
	private String roleName;
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
	
}
