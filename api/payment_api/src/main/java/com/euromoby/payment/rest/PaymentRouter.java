package com.euromoby.payment.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class PaymentRouter {
    @Bean
    public RouterFunction<ServerResponse> route(PaymentHandler paymentsHandler) {
        return RouterFunctions
                .route()
                .GET("/payments", RequestPredicates.accept(MediaType.APPLICATION_JSON), paymentsHandler::listPayments)
                .GET("/payments/{id}", RequestPredicates.accept(MediaType.APPLICATION_JSON), paymentsHandler::getPayment)
                .POST("/payments", RequestPredicates.accept(MediaType.APPLICATION_JSON), paymentsHandler::createPayment)
                .build();
    }
}
