package com.spdcl;

import javax.annotation.PreDestroy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;


@SpringBootApplication
public class InsomniaBackendApplication {

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(InsomniaBackendApplication.class);
		springApplication.run(args);
		
	}
	
	@EventListener(ApplicationReadyEvent.class)
    public void init() throws Exception {
        System.out.println("- - - initializing  db using @PostConstruct");
    }
	
    @PreDestroy
    public void destroy() throws Exception {
        System.out.println("- - - destroying monitor bean using @PreDestroy");
    }
}
