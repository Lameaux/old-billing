package com.euromoby.api.user.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class EndpointsConfiguration {

    @Bean
    RouterFunction<ServerResponse> userRoutes(UserHandler userHandler) {
        return route(GET("/users"), userHandler::all)
                .andRoute(GET("/users/{id}"), userHandler::getById)
                .andRoute(DELETE("/users/{id}"), userHandler::deleteById)
                .andRoute(POST("/users"), userHandler::create)
                .andRoute(PUT("/users/{id}"), userHandler::updateById);
    }

    @Bean
    RouterFunction<ServerResponse> authRoutes(AuthHandler authHandler) {
        return route(POST("/auth"), authHandler::auth);
    }
}
