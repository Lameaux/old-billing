package com.euromoby.api.customer;

import com.euromoby.api.common.ErrorCode;
import com.euromoby.api.common.ErrorResponse;
import com.euromoby.api.common.UUIDValidator;
import com.euromoby.api.security.IsOperator;
import com.euromoby.api.security.MerchantFilter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Component
public class CustomerHandler {
    private static final String PARAM_ID = "id";
    private static final String PARAM_MERCHANT_REFERENCE = "merchant_reference";

    private final CustomerService customerService;

    public CustomerHandler(CustomerService customerService) {
        this.customerService = customerService;
    }

    @IsOperator
    Mono<ServerResponse> listCustomers(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(customerService.getAllCustomers(getMerchantId(serverRequest)), CustomerResponse.class);
    }

    Mono<ServerResponse> getCustomer(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable(PARAM_ID);
        if (!UUIDValidator.isValid(id)) {
            return ErrorResponse.badRequest(ErrorCode.INVALID_UUID, PARAM_ID);
        }

        Mono<CustomerResponse> customerResponseMono = customerService.getCustomer(UUID.fromString(id), getMerchantId(serverRequest));

        return customerResponseMono.flatMap(c -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(c)
        ).switchIfEmpty(ErrorResponse.notFound(ErrorCode.NOT_FOUND, "customer"));
    }

    Mono<ServerResponse> getCustomerByMerchantReference(ServerRequest serverRequest) {
        Optional<String> omr = serverRequest.queryParam(PARAM_MERCHANT_REFERENCE);
        if (omr.isEmpty()) {
            return ErrorResponse.badRequest(ErrorCode.MISSING_QUERY_PARAM, PARAM_MERCHANT_REFERENCE);
        }

        Mono<CustomerResponse> customerResponseMono = customerService.getCustomerByMerchantReference(getMerchantId(serverRequest), omr.get());

        return customerResponseMono.flatMap(c -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(c)
        ).switchIfEmpty(ErrorResponse.notFound(ErrorCode.NOT_FOUND, "customer"));
    }

    Mono<ServerResponse> createCustomer(ServerRequest serverRequest) {
        Mono<CustomerRequest> customerRequestMono = serverRequest.bodyToMono(CustomerRequest.class);

        Mono<CustomerResponse> customerResponseMono = customerService.createCustomer(getMerchantId(serverRequest), customerRequestMono);

        return customerResponseMono.flatMap(c -> ServerResponse.created(null)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(c)
        ).onErrorResume(
                DataIntegrityViolationException.class,
                throwable -> ErrorResponse.conflict(ErrorCode.DUPLICATE_VALUE, PARAM_MERCHANT_REFERENCE)
        );
    }

    private UUID getMerchantId(ServerRequest serverRequest) {
        return MerchantFilter.getMerchantId(serverRequest);
    }
}
