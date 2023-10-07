package com.spdcl.email.dto;

public class ApplicationSubmissionDTO extends BaseMailDTO
{
	private String applicantName;
	private String bodyMessage;
	private String subjectMessage;
	private String appUrl;
	private String UserId;
	private String newPassword;

	/**
	 * @return the applicantName
	 */
	public String getApplicantName()
	{
		return applicantName;
	}

	/**
	 * @param applicantName
	 *            the applicantName to set
	 */
	public void setApplicantName(String applicantName)
	{
		this.applicantName = applicantName;
	}

	/**
	 * @return the bodyMessage
	 */
	public String getBodyMessage()
	{
		return bodyMessage;
	}

	/**
	 * @param bodyMessage
	 *            the bodyMessage to set
	 */
	public void setBodyMessage(String bodyMessage)
	{
		this.bodyMessage = bodyMessage;
	}

	/**
	 * @return the subjectMessage
	 */
	public String getSubjectMessage()
	{
		return subjectMessage;
	}

	/**
	 * @param subjectMessage
	 *            the subjectMessage to set
	 */
	public void setSubjectMessage(String subjectMessage)
	{
		this.subjectMessage = subjectMessage;
	}

	/**
	 * @return the appUrl
	 */
	public String getAppUrl()
	{
		return appUrl;
	}

	/**
	 * @param appUrl
	 *            the appUrl to set
	 */
	public void setAppUrl(String appUrl)
	{
		this.appUrl = appUrl;
	}

	/**
	 * @return the userId
	 */
	public String getUserId()
	{
		return UserId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId)
	{
		UserId = userId;
	}

	/**
	 * @return the newPassword
	 */
	public String getNewPassword()
	{
		return newPassword;
	}

	/**
	 * @param newPassword
	 *            the newPassword to set
	 */
	public void setNewPassword(String newPassword)
	{
		this.newPassword = newPassword;
	}

}
