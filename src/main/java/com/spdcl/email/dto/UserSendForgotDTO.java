package com.spdcl.email.dto;




public class UserSendForgotDTO extends BaseMailDTO{
	private String applicantName;
	private String password;
	private String otp;
	private String validTime;
	/**
	 * @return the applicantName
	 */
	public String getApplicantName() {
		return applicantName;
	}
	/**
	 * @param applicantName the applicantName to set
	 */
	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the otp
	 */
	public String getOtp() {
		return otp;
	}
	/**
	 * @param otp the otp to set
	 */
	public void setOtp(String otp) {
		this.otp = otp;
	}
	/**
	 * @return the validTime
	 */
	public String getValidTime() {
		return validTime;
	}
	/**
	 * @param validTime the validTime to set
	 */
	public void setValidTime(String validTime) {
		this.validTime = validTime;
	}
	
}
