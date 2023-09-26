package com.spdcl.email.utils;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.spdcl.email.dto.BaseMailDTO;
import com.spdcl.utils.ServiceException;
import com.spdcl.utils.ServiceUtils;


public class ParseEmailTemplate {
	
	private static final String EMAILCONTENTS = "EMAILCONTENTS";
	private static final String MAILID_ATTRIBUTE = "MAILID";
	private static final String BODY = "BODY";
	private static final String SUBJECT = "SUBJECT";
	private static final String FROMID = "FROMID";
	private static final String PWD = "PWD";
	private static final String HOST = "HOST";
	private static final String PORT = "PORT";
	private static String MAIL_CONFIG_FILE = "mailsconfig_";
	public static String ENGLISH = "enUS";

	private String localeCode = null;

	public ParseEmailTemplate(String locale) {
		localeCode = locale;
	}

	/**
	 * @param emailType
	 * @param baseMailDTO
	 * @throws ServiceException
	 */
	public void parseMailConfig(String emailType, BaseMailDTO baseMailDTO) throws ServiceException {
		InputStream is = null;
		String emailId = null;
		DocumentBuilderFactory dbFactory = null;
		DocumentBuilder dBuilder = null;
		Document doc = null;
		NodeList contentChildNodes = null;
		NodeList nList = null;
		Node root = null;
		Element eElement = null;
		Element cElement = null;
		Node nNode = null;
		Node cNode = null;
		try {
			is = ServiceUtils.class.getClassLoader().getResourceAsStream(MAIL_CONFIG_FILE + localeCode + ".xml");
			dbFactory = DocumentBuilderFactory.newInstance();
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(is);

			root = doc.getDocumentElement();
			nList = root.getChildNodes();

			for (int temp = 0; temp < nList.getLength(); temp++) {

				nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					eElement = (Element) nNode;
					if (eElement.getNodeName().equalsIgnoreCase(EMAILCONTENTS)) {

						emailId = eElement.getAttribute(MAILID_ATTRIBUTE);
						if (emailId.equalsIgnoreCase(emailType)) {
							contentChildNodes = eElement.getChildNodes();
							for (int i = 0; i < contentChildNodes.getLength(); i++) {
								cNode = contentChildNodes.item(i);
								if (cNode.getNodeType() == Node.ELEMENT_NODE) {
									cElement = (Element) cNode;
									if (cElement.getNodeName().equalsIgnoreCase(BODY)) {
										baseMailDTO.setBody(cElement.getTextContent());
									}
									if (cElement.getNodeName().equalsIgnoreCase(SUBJECT)) {
										baseMailDTO.setSubject(cElement.getTextContent());
									}

								}

							}
						}
					}

					if (eElement.getNodeName().equalsIgnoreCase(FROMID)) {
						baseMailDTO.setFromAddress(eElement.getTextContent());

					}
					if (eElement.getNodeName().equalsIgnoreCase(PWD)) {
						baseMailDTO.setPassword(eElement.getTextContent());
					}
					if (eElement.getNodeName().equalsIgnoreCase(HOST)) {
						baseMailDTO.setHost(eElement.getTextContent());
					}
					if (eElement.getNodeName().equalsIgnoreCase(PORT)) {
						baseMailDTO.setPort(eElement.getTextContent());
					}
				}
			}
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
}
