package com.euromoby.api.config;

import com.euromoby.api.security.AuthFilter;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// https://github.com/springdoc/springdoc-openapi-demos/blob/master/springdoc-openapi-spring-boot-2-webflux-functional/src/main/java/org/springdoc/demo/app4/user/RoutingConfiguration.java

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        Contact contact = new Contact();
        contact.setUrl("http://euromoby.com");
        contact.setName("EUROMOBY");
        return new OpenAPI().components(
                new Components()
                        .addSecuritySchemes(AuthFilter.HEADER_MERCHANT, new SecurityScheme().type(SecurityScheme.Type.APIKEY))
                        .addSecuritySchemes(AuthFilter.HEADER_SECRET, new SecurityScheme().type(SecurityScheme.Type.APIKEY))
        ).info(new Info()
                .title("EUROMOBY Subscription API")
                .contact(contact)
                .description("Subscription as a Service. Together with Billing and Payment Gateway.")
                .version("v1")
        );
    }

    @Bean
    public GroupedOpenApi paymentsOpenApi() {
        String[] paths = {"/api/v1/payments/**"};
        return GroupedOpenApi.builder().group("payments").pathsToMatch(paths).build();
    }

    @Bean
    public GroupedOpenApi customersOpenApi() {
        String[] paths = {"/api/v1/customers/**"};
        return GroupedOpenApi.builder().group("customers").pathsToMatch(paths).build();
    }
}