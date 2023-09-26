package com.spdcl.email.dto;

import java.util.List;



public class BaseMailDTO {

	private List<String> reciepients;
	private List<String> cc;
	private List<String> bcc;
	private String body;
	private String language;
	private String subject;
	private String id;
	
	private String fromAddress;
	private String password;
	private String host;
	private String port;
	
	private String erpMailName;
	private String erpMailTeam;
	private String erpName;
	/**
	 * @return the reciepients
	 */
	public List<String> getReciepients() {
		return reciepients;
	}
	/**
	 * @param reciepients the reciepients to set
	 */
	public void setReciepients(List<String> reciepients) {
		this.reciepients = reciepients;
	}
	/**
	 * @return the cc
	 */
	public List<String> getCc() {
		return cc;
	}
	/**
	 * @param cc the cc to set
	 */
	public void setCc(List<String> cc) {
		this.cc = cc;
	}
	/**
	 * @return the bcc
	 */
	public List<String> getBcc() {
		return bcc;
	}
	/**
	 * @param bcc the bcc to set
	 */
	public void setBcc(List<String> bcc) {
		this.bcc = bcc;
	}
	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}
	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}
	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}
	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the fromAddress
	 */
	public String getFromAddress() {
		return fromAddress;
	}
	/**
	 * @param fromAddress the fromAddress to set
	 */
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
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
	 * @return the host
	 */
	public String getHost() {
		return host;
	}
	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}
	/**
	 * @param port the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}
	/**
	 * @return the erpMailName
	 */
	public String getErpMailName() {
		return erpMailName;
	}
	/**
	 * @param erpMailName the erpMailName to set
	 */
	public void setErpMailName(String erpMailName) {
		this.erpMailName = erpMailName;
	}
	/**
	 * @return the erpMailTeam
	 */
	public String getErpMailTeam() {
		return erpMailTeam;
	}
	/**
	 * @param erpMailTeam the erpMailTeam to set
	 */
	public void setErpMailTeam(String erpMailTeam) {
		this.erpMailTeam = erpMailTeam;
	}
	/**
	 * @return the erpName
	 */
	public String getErpName() {
		return erpName;
	}
	/**
	 * @param erpName the erpName to set
	 */
	public void setErpName(String erpName) {
		this.erpName = erpName;
	}
	
	
}
