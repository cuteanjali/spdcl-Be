package com.spdcl.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.itextpdf.html2pdf.HtmlConverter;
@Service
public class DisconnectionService {

    @Autowired
	private TemplateEngine templateEngine;
    
    public InputStreamResource routeDownload(String tenantCode, UUID id)
			throws FileNotFoundException, java.io.IOException {
		Context context = new Context();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "Test");
		context.setVariables(map);
		final String html = templateEngine.process("2020-instruction-en", context);
		final String fileName = "application.pdf";
		HtmlConverter.convertToPdf(html, new FileOutputStream(fileName));
		return new InputStreamResource(new FileInputStream(fileName));
		
    }
}

