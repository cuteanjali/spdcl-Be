package com.spdcl.email.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DisconnectionRequest {

	private String name;
	private String meter;
	private Date dateConnection;
	private Date dateDisconnection;
	private Date dateLastBill;
	private float loadBal;
	private double payAmnt;
	private double duesAmnt;
	private int noOfDays;
	private UUID id;
	private String tenantCode;
	private String tariffType;
	private String phaseType;
	private List<String> session;
	private double securityAmnt;
	
	private boolean disconnectionApplicable;
	private boolean meterRemovingApplicable;
	private boolean appApplicable;
	
	
	public boolean isDisconnectionApplicable() {
		return disconnectionApplicable;
	}
	public void setDisconnectionApplicable(boolean disconnectionApplicable) {
		this.disconnectionApplicable = disconnectionApplicable;
	}
	public boolean isMeterRemovingApplicable() {
		return meterRemovingApplicable;
	}
	public void setMeterRemovingApplicable(boolean meterRemovingApplicable) {
		this.meterRemovingApplicable = meterRemovingApplicable;
	}
	public boolean isAppApplicable() {
		return appApplicable;
	}
	public void setAppApplicable(boolean appApplicable) {
		this.appApplicable = appApplicable;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMeter() {
		return meter;
	}
	public void setMeter(String meter) {
		this.meter = meter;
	}
	public Date getDateConnection() {
		return dateConnection;
	}
	public void setDateConnection(Date dateConnection) {
		this.dateConnection = dateConnection;
	}
	public Date getDateDisconnection() {
		return dateDisconnection;
	}
	public void setDateDisconnection(Date dateDisconnection) {
		this.dateDisconnection = dateDisconnection;
	}
	public Date getDateLastBill() {
		return dateLastBill;
	}
	public void setDateLastBill(Date dateLastBill) {
		this.dateLastBill = dateLastBill;
	}
	public float getLoadBal() {
		return loadBal;
	}
	public void setLoadBal(float loadBal) {
		this.loadBal = loadBal;
	}
	public double getPayAmnt() {
		return payAmnt;
	}
	public void setPayAmnt(double payAmnt) {
		this.payAmnt = payAmnt;
	}
	public double getDuesAmnt() {
		return duesAmnt;
	}
	public void setDuesAmnt(double duesAmnt) {
		this.duesAmnt = duesAmnt;
	}
	public int getNoOfDays() {
		return noOfDays;
	}
	public void setNoOfDays(int noOfDays) {
		this.noOfDays = noOfDays;
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
	public String getTariffType() {
		return tariffType;
	}
	public void setTariffType(String tariffType) {
		this.tariffType = tariffType;
	}
	public String getPhaseType() {
		return phaseType;
	}
	public void setPhaseType(String phaseType) {
		this.phaseType = phaseType;
	}
	public List<String> getSession() {
		return session;
	}
	public void setSession(List<String> session) {
		this.session = session;
	}
	public double getSecurityAmnt() {
		return securityAmnt;
	}
	public void setSecurityAmnt(double securityAmnt) {
		this.securityAmnt = securityAmnt;
	}
	
	
}
