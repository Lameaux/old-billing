package com.euromoby.api.payment;

import com.euromoby.api.security.SecurityConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class PaymentRouter {

    private static final String API_ROOT = "/api/v1/payments";
    private static final String DOC_TAGS = "Payments";

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "listPayments", summary = "List all Payments", tags = {DOC_TAGS},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = PaymentResponse.class))))
            },
            security = {
                    @SecurityRequirement(name = SecurityConstants.HEADER_MERCHANT),
                    @SecurityRequirement(name = SecurityConstants.HEADER_API_KEY),
                    @SecurityRequirement(name = SecurityConstants.BEARER)
            }))
    public RouterFunction<ServerResponse> listPaymentsRoute(PaymentHandler paymentsHandler) {
        return RouterFunctions.route().path(API_ROOT, builder -> builder
                .GET("", RequestPredicates.accept(MediaType.APPLICATION_JSON), paymentsHandler::listPayments)
        ).build();
    }

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "findPaymentByMerchantReference", summary = "Get Payment by Merchant Reference", tags = {DOC_TAGS},
            parameters = {@Parameter(in = ParameterIn.QUERY, name = "merchant_reference", description = "Merchant Reference")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = PaymentResponse.class)))),
                    @ApiResponse(responseCode = "400", description = "Invalid Merchant Reference supplied"),
                    @ApiResponse(responseCode = "404", description = "Payment not found")
            },
            security = {
                    @SecurityRequirement(name = SecurityConstants.HEADER_MERCHANT),
                    @SecurityRequirement(name = SecurityConstants.HEADER_API_KEY),
                    @SecurityRequirement(name = SecurityConstants.BEARER)
            }))
    public RouterFunction<ServerResponse> findPaymentRoute(PaymentHandler paymentHandler) {
        return RouterFunctions.route().path(API_ROOT, builder -> builder
                .GET("/find_by", RequestPredicates.accept(MediaType.APPLICATION_JSON), paymentHandler::getPaymentByMerchantReference)
        ).build();
    }

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "getPaymentById", summary = "Get Payment by Id", tags = {DOC_TAGS},
            parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "Payment Id")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PaymentResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid Payment Id supplied"),
                    @ApiResponse(responseCode = "404", description = "Payment not found")
            },
            security = {
                    @SecurityRequirement(name = SecurityConstants.HEADER_MERCHANT),
                    @SecurityRequirement(name = SecurityConstants.HEADER_API_KEY),
                    @SecurityRequirement(name = SecurityConstants.BEARER)
            }))
    public RouterFunction<ServerResponse> getPaymentRoute(PaymentHandler paymentsHandler) {
        return RouterFunctions.route().path(API_ROOT, builder -> builder
                .GET("/{id}", RequestPredicates.accept(MediaType.APPLICATION_JSON), paymentsHandler::getPayment)
        ).build();
    }

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "createPayment", summary = "Create a new Payment", tags = {DOC_TAGS},
            requestBody = @RequestBody(description = "Payment", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PaymentRequest.class))),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PaymentResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid Payment"),
                    @ApiResponse(responseCode = "409", description = "Duplicate Payment"),
                    @ApiResponse(responseCode = "500", description = "Server Error")
            },
            security = {
                    @SecurityRequirement(name = SecurityConstants.HEADER_MERCHANT),
                    @SecurityRequirement(name = SecurityConstants.HEADER_API_KEY),
                    @SecurityRequirement(name = SecurityConstants.BEARER)
            }))
    public RouterFunction<ServerResponse> createPaymentRoute(PaymentHandler paymentHandler) {
        return RouterFunctions.route().path(API_ROOT, builder -> builder
                .POST("", RequestPredicates.accept(MediaType.APPLICATION_JSON), paymentHandler::createPayment)
        ).build();
    }
}
