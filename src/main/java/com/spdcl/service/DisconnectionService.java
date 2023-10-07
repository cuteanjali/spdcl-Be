package com.spdcl.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.itextpdf.html2pdf.HtmlConverter;
import com.spdcl.entity.DisconnectionEntity;
import com.spdcl.entity.DisconnectionSessionTariffEntity;
import com.spdcl.entity.SessionTariffEntity;
import com.spdcl.model.SessionTariffPDF;
import com.spdcl.repository.DisconnectionRepository;
import com.spdcl.repository.SessionTariffRepository;

@Service
public class DisconnectionService {

	@Autowired
	private TemplateEngine templateEngine;
	@Autowired
	private DisconnectionRepository disconnectionRepository;
	@Autowired
	private SessionTariffRepository sessionTariffRepository;

	public InputStreamResource routeDownload(String tenantCode, UUID id)
			throws FileNotFoundException, java.io.IOException {
		Context context = new Context();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, Object> map = new HashMap<String, Object>();
		DisconnectionEntity disconnectionEntity = disconnectionRepository.findById(id).get();
		List<SessionTariffPDF> listSession = new ArrayList<SessionTariffPDF>();
		Boolean breakBol = true;
		double totalFixedAmnt = 0;
		int noticePeriod = 30;
		String ruleDisc = null;
		int noDays = 0;
		int diffTimeDay = 0;
		double totalAmt = 0;
		double totalFinalPay = 0;
		String msg = null;
		if (disconnectionEntity != null && disconnectionEntity.getTenantEntity().getTenantCode().equals(tenantCode)) {
			map.put("name", disconnectionEntity.getName());
			map.put("meter", disconnectionEntity.getMeter());
			map.put("disconnection", formatter.format(disconnectionEntity.getDateDisconnection()));
			map.put("connection", formatter.format(disconnectionEntity.getDateConnection()));
			map.put("lastBillDate", formatter.format(disconnectionEntity.getDateLastBill()));
			map.put("connectionType", disconnectionEntity.getDisconnectionSessionTariffEntities().get(0)
					.getSessionTariffEntity().getTariffType());
			map.put("load", "(" + disconnectionEntity.getLoadBal() + "KB" + ")");
			map.put("load1", disconnectionEntity.getLoadBal());
			if (disconnectionEntity.getDisconnectionSessionTariffEntities() != null) {
				for (DisconnectionSessionTariffEntity sessionTariffPDF : disconnectionEntity
						.getDisconnectionSessionTariffEntities()) {
					totalFixedAmnt = totalFixedAmnt + sessionTariffPDF.getSessionTariffEntity().getTariffValue();
					if (breakBol) {
						breakBol = false;
						SessionTariffPDF sessionTariffPDF2 = new SessionTariffPDF();
						sessionTariffPDF2.setSession(sessionTariffPDF.getSessionTariffEntity().getSession());
						sessionTariffPDF2
								.setDiscAmnt(sessionTariffPDF.getSessionTariffEntity().getDisconnectionAmnt() + "");
						sessionTariffPDF2
								.setMeterAmnt(sessionTariffPDF.getSessionTariffEntity().getMeterRemovingAmnt() + "");
						sessionTariffPDF2.setAppAmnt(sessionTariffPDF.getSessionTariffEntity().getAppAmnt() + "");
						sessionTariffPDF2.setFixedAmnt(sessionTariffPDF.getSessionTariffEntity().getTariffValue() + "");
						listSession.add(sessionTariffPDF2);
					} else if (listSession.size() > 0) {
						if (!breakBol) {
							SessionTariffPDF sessionTariffPDF2 = new SessionTariffPDF();
							sessionTariffPDF2.setSession(sessionTariffPDF.getSessionTariffEntity().getSession());
							sessionTariffPDF2.setDiscAmnt("nil");
							sessionTariffPDF2.setMeterAmnt("nil");
							sessionTariffPDF2.setAppAmnt("nil");
							sessionTariffPDF2
									.setFixedAmnt(sessionTariffPDF.getSessionTariffEntity().getTariffValue() + "");
							listSession.add(sessionTariffPDF2);
						}
					}

				}
			}
			Date dateOfCon = disconnectionEntity.getDateConnection();
			Date dateOfDisCon = disconnectionEntity.getDateDisconnection();
			Date dateOfLastBill = disconnectionEntity.getDateLastBill();
			long diff = dateOfDisCon.getTime() - dateOfCon.getTime();
			int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
			if (diffDays < 365) {
				System.out.println("=======diffDays>365===========");
//				 if (request.getDateLastBill().getTime() < request.getDateDisconnection().getTime()) {
//				 }
			} else {
				if (dateOfDisCon.getTime() > dateOfLastBill.getTime()) {
					ruleDisc = "Last Bill Date To Disconnection Date + one month notice period";
					long diffTime = dateOfDisCon.getTime() - dateOfLastBill.getTime();
					diffTimeDay = (int) (diffTime / (24 * 60 * 60 * 1000));
					noDays = (diffTimeDay + noticePeriod);
					totalAmt = noDays * (totalFixedAmnt / 30) * disconnectionEntity.getLoadBal();
					totalFinalPay = (totalAmt + disconnectionEntity.getDuesAmnt()) - disconnectionEntity.getSecurityAmnt();
					if(disconnectionEntity.getSecurityAmnt()>0) {
						msg = "Note : after security adjustment";
					}else {
						msg = "Note : final payable amount against PLD";
					}
				}else if(dateOfLastBill.getTime() > dateOfDisCon.getTime()) {
					ruleDisc = "Last Bill Date  to 30 - (last bill date - disconnection date) ";
					long diffTime =  dateOfLastBill.getTime() - dateOfDisCon.getTime();
					diffTimeDay = (int) (diffTime / (24 * 60 * 60 * 1000));
					noDays = (noticePeriod - diffTimeDay);
					totalAmt = noDays * (totalFixedAmnt / 30) * disconnectionEntity.getLoadBal();
					totalFinalPay = (totalAmt + disconnectionEntity.getDuesAmnt()) - disconnectionEntity.getSecurityAmnt();
					if(disconnectionEntity.getSecurityAmnt()>0) {
						msg = "Note : after security adjustment";
					}else {
						msg = "Note : final payable amount against PLD";
					}
				}
			}
			map.put("msg", msg);
			map.put("totalAmt", Math.round(totalAmt));
			map.put("noticePeriod", noticePeriod);
			map.put("diffTimeDay", diffTimeDay);
			map.put("dues", disconnectionEntity.getDuesAmnt());
			map.put("totalFinalPay", totalFinalPay);
			map.put("noDays", noDays);
			map.put("ruleDisc", ruleDisc);
			map.put("listSession", listSession);
			map.put("totalFixedAmnt", totalFixedAmnt + "");
			map.put("totalMeterRemAmnt", listSession.get(0).getMeterAmnt());
			map.put("totalAppAmnt", listSession.get(0).getAppAmnt());
			map.put("totalDiscAmnt", listSession.get(0).getDiscAmnt());
		}

		context.setVariables(map);
		final String html = templateEngine.process("disconnection-en", context);
		final String fileName = "application.pdf";
		HtmlConverter.convertToPdf(html, new FileOutputStream(fileName));
		return new InputStreamResource(new FileInputStream(fileName));

	}
}
