package com.euromoby.api.auth_api.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class EndpointsConfiguration {
    @Bean
    RouterFunction<ServerResponse> userRoutes(UserHandler userHandler) {
        return route(GET("/v1/auth/me"), userHandler::currentUser);
    }

    @Bean
    RouterFunction<ServerResponse> authRoutes(AuthHandler authHandler) {
        return route(POST("/v1/auth/token").and(accept(MediaType.APPLICATION_FORM_URLENCODED)),
                authHandler::auth);
    }
}
