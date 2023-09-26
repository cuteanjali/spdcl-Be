package com.spdcl.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "session_tariff")
public class SessionTariffEntity extends BaseEntity{
	
	private String session;
	private double tariffValue;
	private double appAmnt;
	private double meterRemovingAmnt;
	private double disconnectionAmnt;
	private String status;
	private String tariffType;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "tenant_id")
	private TenantEntity tenantEntity;

	
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

	public TenantEntity getTenantEntity() {
		return tenantEntity;
	}

	public void setTenantEntity(TenantEntity tenantEntity) {
		this.tenantEntity = tenantEntity;
	}
	
	
}
