package com.spdcl.model;

import java.util.UUID;



public class AdminUserResponseModel {

	private UUID id;
	private String userName;
	private String accessToken;
	private String token;
	private String tenantCode;
	private String firstName;
	private String lastName;
	private String status;
	private UUID roleId;
	private String role;
	private String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public UUID getRoleId() {
		return roleId;
	}
	public void setRoleId(UUID roleId) {
		this.roleId = roleId;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
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
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	/**
	 * @return the id
	 */
	public UUID getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(UUID id) {
		this.id = id;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}
	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}
	/**
	 * @return the tenantCode
	 */
	public String getTenantCode() {
		return tenantCode;
	}
	/**
	 * @param tenantCode the tenantCode to set
	 */
	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}
	
}
