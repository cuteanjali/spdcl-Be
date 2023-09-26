package com.spdcl.email.dto;

import java.util.Date;

public class DisconnectionRequest {

	private String name;
	private String meter;
	private Date dateConnection;
	private Date dateDisconnection;
	private Date dateLastBill;
	private int noOfKb;
	private String tenantCode;
	private String id;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTenantCode() {
		return tenantCode;
	}
	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
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
	public int getNoOfKb() {
		return noOfKb;
	}
	public void setNoOfKb(int noOfKb) {
		this.noOfKb = noOfKb;
	}
	
	
}
