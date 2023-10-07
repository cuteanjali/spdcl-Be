package com.spdcl.model;

import java.util.UUID;

public class UpdateRequestModel {

	private UUID id;
	private String newPass;
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public String getNewPass() {
		return newPass;
	}
	public void setNewPass(String newPass) {
		this.newPass = newPass;
	}
	
	
}
