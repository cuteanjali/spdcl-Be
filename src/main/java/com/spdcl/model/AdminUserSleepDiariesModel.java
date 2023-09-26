package com.spdcl.model;

import java.time.LocalDate;
import java.util.UUID;
public class AdminUserSleepDiariesModel {
	
	private UUID userId;
	private UUID Id;
	private String firstName;
	private String lastName;
	private float efficiency;
	private float fatigueScale;
	private LocalDate weekEndSleepDate;
	private LocalDate weekStartSleepDate;
	private String week;
	private String totalSleepTime;
	
	/**
	 * @return the userId
	 */
	public UUID getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(UUID userId) {
		this.userId = userId;
	}
	/**
	 * @return the id
	 */
	public UUID getId() {
		return Id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(UUID id) {
		Id = id;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/**
	 * @return the efficiency
	 */
	public float getEfficiency() {
		return efficiency;
	}
	/**
	 * @param efficiency the efficiency to set
	 */
	public void setEfficiency(float efficiency) {
		this.efficiency = efficiency;
	}
	/**
	 * @return the fatigueScale
	 */
	public float getFatigueScale() {
		return fatigueScale;
	}
	/**
	 * @param fatigueScale the fatigueScale to set
	 */
	public void setFatigueScale(float fatigueScale) {
		this.fatigueScale = fatigueScale;
	}
	/**
	 * @return the weekEndSleepDate
	 */
	public LocalDate getWeekEndSleepDate() {
		return weekEndSleepDate;
	}
	/**
	 * @param weekEndSleepDate the weekEndSleepDate to set
	 */
	public void setWeekEndSleepDate(LocalDate weekEndSleepDate) {
		this.weekEndSleepDate = weekEndSleepDate;
	}
	/**
	 * @return the weekStartSleepDate
	 */
	public LocalDate getWeekStartSleepDate() {
		return weekStartSleepDate;
	}
	/**
	 * @param weekStartSleepDate the weekStartSleepDate to set
	 */
	public void setWeekStartSleepDate(LocalDate weekStartSleepDate) {
		this.weekStartSleepDate = weekStartSleepDate;
	}
	/**
	 * @return the week
	 */
	public String getWeek() {
		return week;
	}
	/**
	 * @param week the week to set
	 */
	public void setWeek(String week) {
		this.week = week;
	}
	/**
	 * @return the totalSleepTime
	 */
	public String getTotalSleepTime() {
		return totalSleepTime;
	}
	/**
	 * @param totalSleepTime the totalSleepTime to set
	 */
	public void setTotalSleepTime(String totalSleepTime) {
		this.totalSleepTime = totalSleepTime;
	}
	
	
	
	
}
