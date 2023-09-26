package com.spdcl.executor;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spdcl.email.dto.BaseMailDTO;
import com.spdcl.email.utils.ParseEmailTemplate;
import com.spdcl.email.utils.SendMail;

public class EmailExecutor extends TimerTask {

	Logger logger = LoggerFactory.getLogger(EmailExecutor.class);
	private BaseMailDTO mailDO = null;
	private String eType = null;

	public EmailExecutor(BaseMailDTO dto, String emailType) {
		mailDO = dto;
		eType = emailType;
	}

	@Override
	public void run() {
		SendMail sendMail = null;
		ParseEmailTemplate emailTemplate = null;
		logger.info("Starting email sending of type " + eType);
		try {
			// get the template for the requested email type
			emailTemplate = new ParseEmailTemplate(ParseEmailTemplate.ENGLISH);
			emailTemplate.parseMailConfig(eType, mailDO);

			// populate and send
			sendMail = new SendMail(mailDO);
			sendMail.putDynamicValue("do", mailDO);
			sendMail.send();
			logger.info("Finishing email sending of type " + eType);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex.getMessage());
		}

	}
}
