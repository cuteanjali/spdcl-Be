package com.spdcl.email.utils;

import java.io.StringWriter;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.spdcl.email.dto.BaseMailDTO;
import com.spdcl.utils.ServiceException;


public class SendMail {
	private static final String UTF8 = "utf-8";
	private static final String HTML = "html";
	VelocityContext context = null;
	BaseMailDTO mailDTO = null;

	/**
	 * @param pMailDO
	 * @throws ServiceException
	 */
	public SendMail(BaseMailDTO pMailDO) throws ServiceException {
		StringUtils utils = null;
		try {

			EmailUtils.initVelocity();
			context = new VelocityContext();
			mailDTO = pMailDO;
			utils = new StringUtils();
			context.put("utils", utils);
		} catch (Throwable th) {

			throw new ServiceException(th.getMessage());
		}
	}

	/**
	 * @param key
	 * @param value
	 */
	public void putDynamicValue(String key, Object value) {
		context.put(key, value);
	}

	/**
	 * @throws ServiceException
	 */
	public void send() throws ServiceException {
		checkSeparate(mailDTO, mailDTO.getLanguage());
	}

	/**
	 * @param pMailDO
	 * @param lang
	 * @throws ServiceException
	 */
	private void checkSeparate(BaseMailDTO pMailDO, String lang) throws ServiceException {
		try {

			StringBuffer templateBody = new StringBuffer(pMailDO.getBody());
			StringBuffer templateSubject = new StringBuffer(pMailDO.getSubject());

			StringWriter body = new StringWriter();
			StringWriter subject = new StringWriter();

			// Merge Dynamic Contents
			Velocity.evaluate(context, body, pMailDO.getId() + ':' + pMailDO.getLanguage(), templateBody.toString()); // $NON-NLS-1$
			Velocity.evaluate(context, subject, pMailDO.getId() + ':' + pMailDO.getLanguage(),
					templateSubject.toString()); // $NON-NLS-1$

			// Final contents ready to be sent
			String finalBody = body.toString();
			String finalSubject = subject.toString();

			pMailDO.setBody(finalBody);
			pMailDO.setSubject(finalSubject);
			
			// send
			triggerEmail(pMailDO);

		} catch (Throwable th) {

			throw new ServiceException(th.getMessage());
		}
	}

	/**
	 * @param baseMailDTO
	 * @throws ServiceException 
	 */
	private void triggerEmail(BaseMailDTO baseMailDTO) throws ServiceException {

		Properties props = null;
		Authenticator auth = null;
		Session session = null;
		MimeMessage message = null;

		try {
			props = new Properties();
			props.put("mail.smtp.host", baseMailDTO.getHost());
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.socketFactory.port", baseMailDTO.getPort());
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.port", baseMailDTO.getPort()); // SMTP Port
			// create Authenticator object to pass in Session.getInstance argument
			auth = new Authenticator() {
				// override the getPasswordAuthentication method
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(baseMailDTO.getFromAddress(), baseMailDTO.getPassword());
				}
			};
			session = Session.getInstance(props, auth);

			message = new MimeMessage(session);
			message.setFrom(new InternetAddress(baseMailDTO.getFromAddress()));

			message.addRecipients(Message.RecipientType.TO, getRecepients(baseMailDTO.getReciepients()));
			// check and add CC if any
			if (baseMailDTO.getCc() != null && !baseMailDTO.getCc().isEmpty()) {
				message.addRecipients(Message.RecipientType.CC, getRecepients(baseMailDTO.getCc()));
			}

			// check and add Bcc if any
			if (baseMailDTO.getBcc() != null && !baseMailDTO.getBcc().isEmpty()) {
				message.addRecipients(Message.RecipientType.BCC, getRecepients(baseMailDTO.getBcc()));
			}
			message.setSubject(baseMailDTO.getSubject());
			message.setText(baseMailDTO.getBody(), UTF8, HTML);
			
			System.out.println("Start Send...");
			// Send message
			Transport.send(message);
			System.out.println("message sent successfully....");

		} catch (MessagingException mex) {
			mex.printStackTrace();
			throw new ServiceException(mex);
		}

	}

	/**
	 * @param baseMailDTO
	 * @return
	 * @throws AddressException
	 */
	private Address[] getRecepients(List<String> Ids) throws AddressException {
		Address[] recipients = null;
		recipients = new Address[Ids.size()];
		int i = 0;
		for (String recipient : Ids) {
			recipients[i] = new InternetAddress(recipient);
			i++;
		}
		return recipients;
	}

}
