package com.spdcl.model;

import java.util.UUID;

public class SessionTariffModel {

	private UUID id;
	private String session;
	private double tariffValue;
	private double appAmnt;
	private double meterRemovingAmnt;
	private double disconnectionAmnt;
	private String status;
	private String tenantCode;
	private String tariffType;
	private String phaseType;
	
	public String getPhaseType() {
		return phaseType;
	}
	public void setPhaseType(String phaseType) {
		this.phaseType = phaseType;
	}
	public double getAppAmnt() {
		return appAmnt;
	}
	public void setAppAmnt(double appAmnt) {
		this.appAmnt = appAmnt;
	}
	public double getMeterRemovingAmnt() {
		return meterRemovingAmnt;
	}
	public void setMeterRemovingAmnt(double meterRemovingAmnt) {
		this.meterRemovingAmnt = meterRemovingAmnt;
	}
	public double getDisconnectionAmnt() {
		return disconnectionAmnt;
	}
	public void setDisconnectionAmnt(double disconnectionAmnt) {
		this.disconnectionAmnt = disconnectionAmnt;
	}
	public String getTariffType() {
		return tariffType;
	}
	public void setTariffType(String tariffType) {
		this.tariffType = tariffType;
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public double getTariffValue() {
		return tariffValue;
	}
	public void setTariffValue(double tariffValue) {
		this.tariffValue = tariffValue;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTenantCode() {
		return tenantCode;
	}
	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}
	
	
}
