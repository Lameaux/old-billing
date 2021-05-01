package com.euromoby.api.customer;

import com.euromoby.api.common.ErrorCode;
import com.euromoby.api.common.ErrorResponse;
import com.euromoby.api.common.UUIDValidator;
import com.euromoby.api.security.AuthenticationUtil;
import com.euromoby.api.security.IsOperator;
import com.euromoby.api.security.IsViewer;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
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
    private static final int DEFAULT_PAGE_NUM = 0;
    private static final int DEFAULT_PAGE_SIZE = 50;
    private static final String PARAM_ORDER_BY = "order_by";
    private static final String PARAM_ORDER_DIRECTION = "order_direction";
    private static final String PARAM_PAGE = "page";
    private static final String PARAM_SIZE = "size";
    private static final String PARAM_MSISDN = "msisdn";
    private static final String PARAM_NAME = "name";
    private static final String PARAM_EMAIL = "email";

    private final CustomerService customerService;

    public CustomerHandler(CustomerService customerService) {
        this.customerService = customerService;
    }

    @IsViewer
    Mono<ServerResponse> listAll(ServerRequest serverRequest) {
        Optional<String> orderBy = serverRequest.queryParam(PARAM_ORDER_BY);
        Optional<String> orderDirection = serverRequest.queryParam(PARAM_ORDER_DIRECTION);
        Optional<String> page = serverRequest.queryParam(PARAM_PAGE);
        Optional<String> size = serverRequest.queryParam(PARAM_SIZE);

        return AuthenticationUtil.getMerchantId(serverRequest).flatMap(merchantId ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(customerService.getAllCustomers(
                                merchantId,
                                orderBy.orElse(PARAM_NAME),
                                orderDirection.orElse(Sort.Direction.ASC.toString()),
                                page.map(Integer::valueOf).orElse(DEFAULT_PAGE_NUM),
                                size.map(Integer::valueOf).orElse(DEFAULT_PAGE_SIZE)
                        ), CustomerResponse.class));
    }

    @IsViewer
    Mono<ServerResponse> findByFilter(ServerRequest serverRequest) {
        Optional<String> email = serverRequest.queryParam(PARAM_EMAIL);
        Optional<String> msisdn = serverRequest.queryParam(PARAM_MSISDN);
        Optional<String> name = serverRequest.queryParam(PARAM_NAME);
        Optional<String> page = serverRequest.queryParam(PARAM_PAGE);
        Optional<String> size = serverRequest.queryParam(PARAM_SIZE);
        Optional<String> merchantReference = serverRequest.queryParam(PARAM_MERCHANT_REFERENCE);

        return AuthenticationUtil.getMerchantId(serverRequest).flatMap(merchantId ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(customerService.findCustomersByFilter(
                                merchantId,
                                merchantReference.orElse(""),
                                email.orElse(""),
                                msisdn.orElse(""),
                                name.orElse(""),
                                page.map(Integer::valueOf).orElse(DEFAULT_PAGE_NUM),
                                size.map(Integer::valueOf).orElse(DEFAULT_PAGE_SIZE)
                        ), CustomerResponse.class));
    }

    @IsViewer
    Mono<ServerResponse> getCustomer(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable(PARAM_ID);
        if (!UUIDValidator.isValid(id)) {
            return ErrorResponse.badRequest(ErrorCode.INVALID_UUID, PARAM_ID);
        }

        Mono<CustomerResponse> customerResponseMono = AuthenticationUtil.getMerchantId(serverRequest).flatMap(
                merchantId -> customerService.getCustomer(UUID.fromString(id), merchantId)
        );

        return customerResponseMono.flatMap(c -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(c)
        ).switchIfEmpty(ErrorResponse.notFound(ErrorCode.NOT_FOUND, "customer"));
    }

    @IsOperator
    Mono<ServerResponse> createCustomer(ServerRequest serverRequest) {
        Mono<CustomerRequest> customerRequestMono = serverRequest.bodyToMono(CustomerRequest.class);

        Mono<CustomerResponse> customerResponseMono = AuthenticationUtil.getMerchantId(serverRequest).flatMap(
                merchantId -> customerService.createCustomer(merchantId, customerRequestMono)
        );

        return customerResponseMono.flatMap(c -> ServerResponse.created(null)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(c)
        ).onErrorResume(
                DuplicateKeyException.class,
                throwable -> ErrorResponse.conflict(ErrorCode.DUPLICATE_VALUE, PARAM_MERCHANT_REFERENCE)
        );
    }
}
