package com.euromoby.api.payment;

import com.euromoby.api.common.ErrorCode;
import com.euromoby.api.common.ErrorResponse;
import com.euromoby.api.common.UUIDValidator;
import com.euromoby.api.security.IsOperator;
import com.euromoby.api.security.IsViewer;
import com.euromoby.api.security.MerchantFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class PaymentHandler {
    private static final String PARAM_ID = "id";
    private static final String PARAM_MERCHANT_REFERENCE = "merchant_reference";

    private final PaymentService paymentService;

    public PaymentHandler(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @IsViewer
    Mono<ServerResponse> listPayments(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentService.getAllPayments(getMerchantId(serverRequest)), PaymentResponse.class);
    }

    @IsViewer
    Mono<ServerResponse> getPayment(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable(PARAM_ID);
        if (!UUIDValidator.isValid(id)) {
            return ErrorResponse.badRequest(ErrorCode.INVALID_UUID, PARAM_ID);
        }

        Mono<PaymentResponse> paymentResponseMono = paymentService.getPayment(UUID.fromString(id), getMerchantId(serverRequest));

        return paymentResponseMono.flatMap(p -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(p)
        ).switchIfEmpty(ErrorResponse.notFound(ErrorCode.NOT_FOUND, "payment"));

    }

    @IsViewer
    Mono<ServerResponse> getPaymentByMerchantReference(ServerRequest serverRequest) {
        Optional<String> omr = serverRequest.queryParam(PARAM_MERCHANT_REFERENCE);
        if (omr.isEmpty()) {
            return ErrorResponse.badRequest(ErrorCode.MISSING_QUERY_PARAM, PARAM_MERCHANT_REFERENCE);
        }

        Mono<PaymentResponse> paymentResponseMono = paymentService.getPaymentByMerchantReference(getMerchantId(serverRequest), omr.get());

        return paymentResponseMono.flatMap(p -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(p)
        ).switchIfEmpty(ErrorResponse.notFound(ErrorCode.NOT_FOUND, "payment"));
    }

    @IsOperator
    Mono<ServerResponse> createPayment(ServerRequest serverRequest) {
        Mono<PaymentRequest> paymentRequestMono = serverRequest.bodyToMono(PaymentRequest.class);

        Mono<PaymentResponse> paymentResponseMono = paymentService.createPayment(getMerchantId(serverRequest), paymentRequestMono);

        // FIXME validation of input pramaeters

        return paymentResponseMono.flatMap(p -> ServerResponse.created(null)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(p)
        ).onErrorResume(
                DuplicateKeyException.class,
                throwable -> ErrorResponse.conflict(ErrorCode.DUPLICATE_VALUE, PARAM_MERCHANT_REFERENCE)
        );
    }

    private UUID getMerchantId(ServerRequest serverRequest) {
        return MerchantFilter.getMerchantId(serverRequest);
    }
}
