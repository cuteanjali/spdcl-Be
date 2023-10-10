package com.spdcl.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name = "disconnection_table")
public class DisconnectionEntity  extends BaseEntity{

	private String name;
	private String meter;
	private Date dateConnection;
	private Date dateDisconnection;
	private Date dateLastBill;
	private float loadBal;
	private double payAmnt;
	private double duesAmnt;
	private int noOfDays;
	private double securityAmnt;
	@OneToMany(mappedBy="disconnectionEntity",cascade=CascadeType.ALL, orphanRemoval=true)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<DisconnectionSessionTariffEntity> disconnectionSessionTariffEntities = new ArrayList<DisconnectionSessionTariffEntity>();
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "tenant_id")
	private TenantEntity tenantEntity;

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

	public double getSecurityAmnt() {
		return securityAmnt;
	}

	public void setSecurityAmnt(double securityAmnt) {
		this.securityAmnt = securityAmnt;
	}

	public List<DisconnectionSessionTariffEntity> getDisconnectionSessionTariffEntities() {
		return disconnectionSessionTariffEntities;
	}

	public void setDisconnectionSessionTariffEntities(
			List<DisconnectionSessionTariffEntity> disconnectionSessionTariffEntities) {
		this.disconnectionSessionTariffEntities = disconnectionSessionTariffEntities;
	}

	public TenantEntity getTenantEntity() {
		return tenantEntity;
	}

	public void setTenantEntity(TenantEntity tenantEntity) {
		this.tenantEntity = tenantEntity;
	}

	
	
}
