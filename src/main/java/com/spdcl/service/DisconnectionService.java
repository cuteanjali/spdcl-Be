package com.spdcl.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import com.spdcl.model.ApplicableTariffPDF;
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
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		Map<String, Object> map = new HashMap<String, Object>();
		DisconnectionEntity disconnectionEntity = disconnectionRepository.findById(id).get();
		List<SessionTariffPDF> listSession = new ArrayList<SessionTariffPDF>();
		List<String> applicableTariffPDFStr = Arrays.asList("Meter Removing","Application","Disconnection"); 
		List<ApplicableTariffPDF> applicableTariffPDFList = new ArrayList<ApplicableTariffPDF>();
		List<ApplicableTariffPDF> applicableTariffPDFList1 = new ArrayList<ApplicableTariffPDF>();
		Boolean breakBol = true;
		double totalFixedAmnt = 0;
		int noticePeriod = 30;
		String ruleDisc = null;
		int noDays = 0;
		int diffTimeDay = 0;
		double totalAmt = 0;
		double totalFinalPay = 0;
		double applicationPay = 0;
		double disconnectionPay = 0;
		double meterRemovingPay = 0;
		String msg = null;
		if (disconnectionEntity != null && disconnectionEntity.getTenantEntity().getTenantCode().equals(tenantCode)) {
			map.put("name", disconnectionEntity.getName());
			map.put("meter", disconnectionEntity.getMeter());
			map.put("disconnection", formatter.format(disconnectionEntity.getDateDisconnection()));
			
			map.put("connection", formatter.format(disconnectionEntity.getDateConnection()));
			map.put("lastBillDate", formatter.format(disconnectionEntity.getDateLastBill()));
			map.put("connectionType", disconnectionEntity.getDisconnectionSessionTariffEntities().get(0)
					.getSessionTariffEntity().getTariffType());
			map.put("phaseType", disconnectionEntity.getDisconnectionSessionTariffEntities().get(0)
					.getSessionTariffEntity().getPhaseType());
			map.put("load", "(" + disconnectionEntity.getLoadBal() + "KB" + ")");
			map.put("load1", disconnectionEntity.getLoadBal());
			String checked = "<input type = 'checkbox' checked />";
			String unchecked = "<input type = 'checkbox'/>";
			for(String str : applicableTariffPDFStr) {
				ApplicableTariffPDF applicableTariffPDF = new ApplicableTariffPDF();
				ApplicableTariffPDF applicableTariffPDF1 = new ApplicableTariffPDF();
				if (disconnectionEntity.isAppApplicable() && str.equalsIgnoreCase("Application")) {
					applicableTariffPDF.setChecking(checked);
					if(disconnectionEntity.getDisconnectionSessionTariffEntities() != null && disconnectionEntity.getDisconnectionSessionTariffEntities().size()>0) {
						applicationPay = disconnectionEntity.getDisconnectionSessionTariffEntities().get(0).getSessionTariffEntity().getAppAmnt();
						applicableTariffPDF1.setApplicableTariff(str);
						applicableTariffPDF1.setChecking(applicationPay+"");
						applicableTariffPDFList1.add(applicableTariffPDF1);
					}
					
				} else if (!disconnectionEntity.isAppApplicable() && str.equalsIgnoreCase("Application")){
					applicableTariffPDF.setChecking(unchecked);
				}
				else if (disconnectionEntity.isDisconnectionApplicable() && str.equalsIgnoreCase("Disconnection")) {
					if(disconnectionEntity.getDisconnectionSessionTariffEntities() != null && disconnectionEntity.getDisconnectionSessionTariffEntities().size()>0) {
						disconnectionPay = disconnectionEntity.getDisconnectionSessionTariffEntities().get(0).getSessionTariffEntity().getDisconnectionAmnt();
						applicableTariffPDF1.setApplicableTariff(str);
						applicableTariffPDF1.setChecking(disconnectionPay+"");
						applicableTariffPDFList1.add(applicableTariffPDF1);
					}
					 applicableTariffPDF.setChecking(checked);
				}else if (!disconnectionEntity.isDisconnectionApplicable() && str.equalsIgnoreCase("Disconnection")){
					applicableTariffPDF.setChecking(unchecked);
				}
				 
				else if (disconnectionEntity.isMeterRemovingApplicable() && str.equalsIgnoreCase("Meter Removing")) {
					if(disconnectionEntity.getDisconnectionSessionTariffEntities() != null && disconnectionEntity.getDisconnectionSessionTariffEntities().size()>0) {
						meterRemovingPay = disconnectionEntity.getDisconnectionSessionTariffEntities().get(0).getSessionTariffEntity().getMeterRemovingAmnt();
						applicableTariffPDF1.setApplicableTariff(str);
						applicableTariffPDF1.setChecking(meterRemovingPay+"");
						applicableTariffPDFList1.add(applicableTariffPDF1);
					}
					 applicableTariffPDF.setChecking(checked);
				}else if (!disconnectionEntity.isMeterRemovingApplicable() && str.equalsIgnoreCase("Meter Removing")){
					applicableTariffPDF.setChecking(unchecked);
				}
				applicableTariffPDF.setApplicableTariff(str);
				applicableTariffPDFList.add(applicableTariffPDF);
			}
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
					if(applicationPay > 0 || disconnectionPay > 0 || meterRemovingPay>0) {
						totalFinalPay = (totalAmt + disconnectionEntity.getDuesAmnt()) - disconnectionEntity.getSecurityAmnt();	
						
					}else {
						totalFinalPay = (totalAmt + disconnectionEntity.getDuesAmnt()) - disconnectionEntity.getSecurityAmnt();	
					}
					
					if(disconnectionEntity.getSecurityAmnt()>0) {
						msg = "Note : after security adjustment";
					}else {
						msg = "Note : final payable amount against PLD";
					}
				}else if(dateOfLastBill.getTime() > dateOfDisCon.getTime()) {
					ruleDisc = "Last Bill Date  to 30 - (last bill date - disconnection date) ";
	
					Calendar call22 = Calendar.getInstance();
					call22.setTime(dateOfDisCon);
					call22.add(Calendar.DATE, +30);
					
					long diffTime =  call22.getTime().getTime() - dateOfLastBill.getTime();
					diffTimeDay = (int) (diffTime / (24 * 60 * 60 * 1000));
					noDays = (diffTimeDay);
					totalAmt = noDays * (totalFixedAmnt / 30) * disconnectionEntity.getLoadBal();
					if(applicationPay > 0 || disconnectionPay > 0 || meterRemovingPay > 0) {
						totalFinalPay = (totalAmt + disconnectionEntity.getDuesAmnt()) - disconnectionEntity.getSecurityAmnt();	
						
					}else {
						totalFinalPay = (totalAmt + disconnectionEntity.getDuesAmnt()) - disconnectionEntity.getSecurityAmnt();	
						
					}
					
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
			map.put("totalFinalPay", Math.round(totalFinalPay));
			map.put("noDays", noDays);
			map.put("ruleDisc", ruleDisc);
			map.put("listSession", listSession);
			map.put("listApplicable", applicableTariffPDFList);
			map.put("listApplicable1", applicableTariffPDFList1);
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
