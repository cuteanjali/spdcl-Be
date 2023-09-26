package com.spdcl.model;

import java.util.List;

public class SessionTariffRequest {

	private List<String> sessions;
	private String tenantCode;
	private String type;
	public List<String> getSessions() {
		return sessions;
	}
	public void setSessions(List<String> sessions) {
		this.sessions = sessions;
	}
	public String getTenantCode() {
		return tenantCode;
	}
	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
