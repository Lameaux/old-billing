package com.euromoby.api.customer;

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
public class CustomerRouter {

    private static final String API_ROOT = "/api/v1/customers";
    private static final String DOC_TAGS = "Customers";

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "listAllCustomers", summary = "List all Customers", tags = {DOC_TAGS},
            parameters = {
                    @Parameter(in = ParameterIn.QUERY, name = "page", description = "Page number"),
                    @Parameter(in = ParameterIn.QUERY, name = "size", description = "Page size"),
                    @Parameter(in = ParameterIn.QUERY, name = "order_by", description = "Order by"),
                    @Parameter(in = ParameterIn.QUERY, name = "order_direction", description = "Order direction (ASC, DESC)")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of users", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = CustomerResponse.class)))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
            },
            security = {
                    @SecurityRequirement(name = SecurityConstants.HEADER_MERCHANT),
                    @SecurityRequirement(name = SecurityConstants.HEADER_API_KEY),
                    @SecurityRequirement(name = SecurityConstants.BEARER)
            }
    ))
    public RouterFunction<ServerResponse> listAllCustomers(CustomerHandler customerHandler) {
        return RouterFunctions.route().path(API_ROOT, builder -> builder
                .GET("", RequestPredicates.accept(MediaType.APPLICATION_JSON), customerHandler::listAll)
        ).build();
    }

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "findCustomersByFilter", summary = "Get Customer by filter", tags = {DOC_TAGS},
            parameters = {
                    @Parameter(in = ParameterIn.QUERY, name = "merchant_reference", description = "Merchant Reference"),
                    @Parameter(in = ParameterIn.QUERY, name = "email", description = "E-mail"),
                    @Parameter(in = ParameterIn.QUERY, name = "msisdn", description = "MSISDN"),
                    @Parameter(in = ParameterIn.QUERY, name = "name", description = "Name"),
                    @Parameter(in = ParameterIn.QUERY, name = "page", description = "Page number"),
                    @Parameter(in = ParameterIn.QUERY, name = "size", description = "Page size")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of customers", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = CustomerResponse.class)))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Customer not found")
            },
            security = {
                    @SecurityRequirement(name = SecurityConstants.HEADER_MERCHANT),
                    @SecurityRequirement(name = SecurityConstants.HEADER_API_KEY),
                    @SecurityRequirement(name = SecurityConstants.BEARER)
            }
    ))
    public RouterFunction<ServerResponse> findCustomersByFilter(CustomerHandler customerHandler) {
        return RouterFunctions.route().path(API_ROOT, builder -> builder
                .GET("/find_by", RequestPredicates.accept(MediaType.APPLICATION_JSON), customerHandler::findByFilter)
        ).build();
    }

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "getCustomer", summary = "Get Customer by Id", tags = {DOC_TAGS},
            parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "Customer Id")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CustomerResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid Customer Id supplied"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Customer not found")
            },
            security = {
                    @SecurityRequirement(name = SecurityConstants.HEADER_MERCHANT),
                    @SecurityRequirement(name = SecurityConstants.HEADER_API_KEY),
                    @SecurityRequirement(name = SecurityConstants.BEARER)
            }
    ))
    public RouterFunction<ServerResponse> getCustomer(CustomerHandler customerHandler) {
        return RouterFunctions.route().path(API_ROOT, builder -> builder
                .GET("/{id}", RequestPredicates.accept(MediaType.APPLICATION_JSON), customerHandler::getCustomer)
        ).build();
    }

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "createCustomer", summary = "Create a new Customer", tags = {DOC_TAGS},
            requestBody = @RequestBody(description = "Customer", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CustomerRequest.class))),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CustomerResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid Customer"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "409", description = "Duplicate Customer"),
                    @ApiResponse(responseCode = "500", description = "Server Error")
            },
            security = {
                    @SecurityRequirement(name = SecurityConstants.HEADER_MERCHANT),
                    @SecurityRequirement(name = SecurityConstants.HEADER_API_KEY),
                    @SecurityRequirement(name = SecurityConstants.BEARER)
            }
    ))
    public RouterFunction<ServerResponse> createCustomerRoute(CustomerHandler customerHandler) {
        return RouterFunctions.route().path(API_ROOT, builder -> builder
                .POST("", RequestPredicates.accept(MediaType.APPLICATION_JSON), customerHandler::createCustomer)
        ).build();
    }
}
