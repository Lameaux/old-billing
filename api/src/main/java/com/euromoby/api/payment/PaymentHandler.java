package com.euromoby.api.payment;

import com.euromoby.api.common.ErrorCode;
import com.euromoby.api.common.ErrorResponse;
import com.euromoby.api.common.UUIDValidator;
import com.euromoby.api.security.AuthFilter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

@Component
public class PaymentHandler {
    private static final String URI_PREFIX = "/payments/";

    private final PaymentService paymentService;

    public PaymentHandler(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    Mono<ServerResponse> listPayments(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentService.getAllPayments(getMerchantId(serverRequest)), PaymentResponse.class);
    }

    Mono<ServerResponse> getPayment(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        if (!UUIDValidator.isValid(id)) {
            return ErrorResponse.badRequest(ErrorCode.INVALID_UUID, "id");
        }

        Mono<PaymentResponse> paymentResponseMono = paymentService.getPayment(UUID.fromString(id), getMerchantId(serverRequest));

        return paymentResponseMono.flatMap(p -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentResponseMono, PaymentResponse.class)
        ).switchIfEmpty(ErrorResponse.notFound(ErrorCode.NOT_FOUND, "payment"));

    }

    Mono<ServerResponse> createPayment(ServerRequest serverRequest) {
        Mono<PaymentRequest> paymentRequestMono = serverRequest.bodyToMono(PaymentRequest.class);

        Mono<PaymentResponse> paymentResponseMono = paymentService.createPayment(getMerchantId(serverRequest), paymentRequestMono);

        return paymentResponseMono.flatMap(p -> ServerResponse.created(URI.create(URI_PREFIX + p.getId())).contentType(MediaType.APPLICATION_JSON).body(paymentResponseMono, PaymentResponse.class));
    }

    private UUID getMerchantId(ServerRequest serverRequest) {
        return AuthFilter.getMerchantId(serverRequest);
    }
}
