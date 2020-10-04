package com.euromoby.payment.rest;

import com.euromoby.payment.repository.MerchantRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class PaymentRouter {

    private final MerchantRepository merchantRepository;

    public PaymentRouter(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    @Bean
    public RouterFunction<ServerResponse> route(PaymentHandler paymentsHandler) {
        return RouterFunctions
                .route()
                .filter(new AuthFilter(merchantRepository))
                .GET("/payments", RequestPredicates.accept(MediaType.APPLICATION_JSON), paymentsHandler::listPayments)
                .GET("/payments/{id}", RequestPredicates.accept(MediaType.APPLICATION_JSON), paymentsHandler::getPayment)
                .POST("/payments", RequestPredicates.accept(MediaType.APPLICATION_JSON), paymentsHandler::createPayment)
                .build();
    }
}
