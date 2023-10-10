package com.spdcl.model;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DisconnectionRequestData {

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
	private List<String> session;
	private String phaseType;
	private String tariffValue;
	private String disconnectionAmnt;
	private String meterRemovingAmnt;
	private String appAmnt;
	private double securityAmt;
	
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
	public String getPhaseType() {
		return phaseType;
	}
	public void setPhaseType(String phaseType) {
		this.phaseType = phaseType;
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
	public List<String> getSession() {
		return session;
	}
	public void setSession(List<String> session) {
		this.session = session;
	}
	public String getTariffValue() {
		return tariffValue;
	}
	public void setTariffValue(String tariffValue) {
		this.tariffValue = tariffValue;
	}
	public String getDisconnectionAmnt() {
		return disconnectionAmnt;
	}
	public void setDisconnectionAmnt(String disconnectionAmnt) {
		this.disconnectionAmnt = disconnectionAmnt;
	}
	public String getMeterRemovingAmnt() {
		return meterRemovingAmnt;
	}
	public void setMeterRemovingAmnt(String meterRemovingAmnt) {
		this.meterRemovingAmnt = meterRemovingAmnt;
	}
	public String getAppAmnt() {
		return appAmnt;
	}
	public void setAppAmnt(String appAmnt) {
		this.appAmnt = appAmnt;
	}
	public double getSecurityAmt() {
		return securityAmt;
	}
	public void setSecurityAmt(double securityAmt) {
		this.securityAmt = securityAmt;
	}
	
	
	
}
