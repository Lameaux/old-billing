package com.euromoby.payment.rest;

import com.euromoby.payment.dto.PaymentDto;
import com.euromoby.payment.service.PaymentService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Component
public class PaymentHandler {
    private static final Mono<ServerResponse> NOT_FOUND = ServerResponse.notFound().build();
    private final PaymentService paymentService;

    public PaymentHandler(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    Mono<ServerResponse> listPayments(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentService.getAllPayments(getMerchantId(serverRequest)), PaymentDto.class);
    }

    Mono<ServerResponse> getPayment(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");

        if (StringUtils.isEmpty(id)) {
            return NOT_FOUND;
        }

        Mono<PaymentDto> payment = paymentService.getPayment(UUID.fromString(id), getMerchantId(serverRequest));

        return payment.flatMap(p -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(payment, PaymentDto.class))
        ).switchIfEmpty(NOT_FOUND);

    }

    Mono<ServerResponse> createPayment(ServerRequest serverRequest) {
        Mono<PaymentDto> payment = serverRequest.bodyToMono(PaymentDto.class);

        return payment.flatMap(p -> ServerResponse.created(URI.create("/payment/" + p.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentService.createPayment(getMerchantId(serverRequest)), PaymentDto.class)
        );
    }

    private UUID getMerchantId(ServerRequest serverRequest) {
        return UUID.fromString(Optional.ofNullable(serverRequest.headers().firstHeader(AuthFilter.HEADER_MERCHANT)).orElseThrow());
    }

}
