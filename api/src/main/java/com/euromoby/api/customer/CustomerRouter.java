package com.euromoby.api.customer;

import com.euromoby.api.security.AuthFilter;
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
import org.springframework.beans.factory.annotation.Autowired;
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

    private final AuthFilter authFilter;

    @Autowired
    public CustomerRouter(AuthFilter authFilter) {
        this.authFilter = authFilter;
    }

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "listCustomers", summary = "List all Customers", tags = {DOC_TAGS},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = CustomerResponse.class))))
            },
            security = {
                    @SecurityRequirement(name = AuthFilter.HEADER_MERCHANT),
                    @SecurityRequirement(name = AuthFilter.HEADER_API_KEY),
                    @SecurityRequirement(name = AuthFilter.BEARER)
            }
    ))
    public RouterFunction<ServerResponse> listCustomersRoute(CustomerHandler customerHandler) {
        return RouterFunctions.route().path(API_ROOT, builder -> builder.filter(authFilter)
                .GET("", RequestPredicates.accept(MediaType.APPLICATION_JSON), customerHandler::listCustomers)
        ).build();
    }

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "getCustomerByMerchantReference", summary = "Get Customer by Merchant Reference", tags = {DOC_TAGS},
            parameters = {@Parameter(in = ParameterIn.QUERY, name = "merchant_reference", description = "Merchant Reference")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = CustomerResponse.class)))),
                    @ApiResponse(responseCode = "400", description = "Invalid Merchant Reference supplied"),
                    @ApiResponse(responseCode = "404", description = "Customer not found")
            },
            security = {
                    @SecurityRequirement(name = AuthFilter.HEADER_MERCHANT),
                    @SecurityRequirement(name = AuthFilter.HEADER_API_KEY),
                    @SecurityRequirement(name = AuthFilter.BEARER)
            }
    ))
    public RouterFunction<ServerResponse> findCustomerRoute(CustomerHandler customerHandler) {
        return RouterFunctions.route().path(API_ROOT, builder -> builder.filter(authFilter)
                .GET("/find_by", RequestPredicates.accept(MediaType.APPLICATION_JSON), customerHandler::getCustomerByMerchantReference)
        ).build();
    }

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "findCustomerById", summary = "Get Customer by Id", tags = {DOC_TAGS},
            parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "Customer Id")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CustomerResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid Customer Id supplied"),
                    @ApiResponse(responseCode = "404", description = "Customer not found")
            },
            security = {
                    @SecurityRequirement(name = AuthFilter.HEADER_MERCHANT),
                    @SecurityRequirement(name = AuthFilter.HEADER_API_KEY),
                    @SecurityRequirement(name = AuthFilter.BEARER)
            }
    ))
    public RouterFunction<ServerResponse> getCustomerRoute(CustomerHandler customerHandler) {
        return RouterFunctions.route().path(API_ROOT, builder -> builder.filter(authFilter)
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
                    @ApiResponse(responseCode = "409", description = "Duplicate Customer"),
                    @ApiResponse(responseCode = "500", description = "Server Error")
            },
            security = {
                    @SecurityRequirement(name = AuthFilter.HEADER_MERCHANT),
                    @SecurityRequirement(name = AuthFilter.HEADER_API_KEY),
                    @SecurityRequirement(name = AuthFilter.BEARER)
            }
    ))
    public RouterFunction<ServerResponse> createCustomerRoute(CustomerHandler customerHandler) {
        return RouterFunctions.route().path(API_ROOT, builder -> builder.filter(authFilter)
                .POST("", RequestPredicates.accept(MediaType.APPLICATION_JSON), customerHandler::createCustomer)
        ).build();
    }
}
