package com.spdcl.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "disconnection_session_tariff")
public class DisconnectionSessionTariffEntity extends BaseEntity{

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "sessionTariff_id")
	private SessionTariffEntity sessionTariffEntity;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "disconnection_id")
	private DisconnectionEntity disconnectionEntity;

	public SessionTariffEntity getSessionTariffEntity() {
		return sessionTariffEntity;
	}

	public void setSessionTariffEntity(SessionTariffEntity sessionTariffEntity) {
		this.sessionTariffEntity = sessionTariffEntity;
	}

	public DisconnectionEntity getDisconnectionEntity() {
		return disconnectionEntity;
	}

	public void setDisconnectionEntity(DisconnectionEntity disconnectionEntity) {
		this.disconnectionEntity = disconnectionEntity;
	}
	
	
}
