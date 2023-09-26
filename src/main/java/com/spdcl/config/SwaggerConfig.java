package com.spdcl.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;


@Configuration
public class SwaggerConfig {

	 public static final String AUTHORIZATION_HEADER = "Authorization";

	    private ApiInfo apiInfo() {
	        return new ApiInfo("SPDCL API",
	                "This API specification SPDCL API'S",
	                "API TOS",
	                "",
	                new Contact("Nitish Kumar", "www.insomnia.com", ""),
	                "",
	                "",
	                Collections.emptyList());
	    }

	    @Bean
	    public Docket api() {
	    	
	        return new Docket(DocumentationType.OAS_30)
	        		.groupName("SPDCL v1")
	                .apiInfo(apiInfo())
	                .securityContexts(Arrays.asList(securityContext()))
	                .securitySchemes(Arrays.asList(apiKey()))
	                .select()
	                .apis(RequestHandlerSelectors.basePackage("com.spdcl.controller"))
	                .paths(PathSelectors.any())
	                .build()
	                .tags(
							new Tag("User","End Point for user")

					);
	    }

	    private ApiKey apiKey() {
	        return new ApiKey(AUTHORIZATION_HEADER, "JWT", "header");
	    }

	    private SecurityContext securityContext() {
	        return SecurityContext.builder()
	                .securityReferences(defaultAuth())
	                .build();
	    }

	    List<SecurityReference> defaultAuth() {
	        AuthorizationScope authorizationScope
	                = new AuthorizationScope("global", "accessEverything");
	        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
	        authorizationScopes[0] = authorizationScope;
	        return Arrays.asList(new SecurityReference(AUTHORIZATION_HEADER, authorizationScopes));
	    }
	
}