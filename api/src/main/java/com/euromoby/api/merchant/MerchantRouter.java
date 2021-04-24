package com.euromoby.api.merchant;

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
public class MerchantRouter {

    private static final String API_ROOT = "/api/v1/merchants";
    private static final String DOC_TAGS = "Merchants";

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "listAllMerchants", summary = "List all merchants", tags = {DOC_TAGS},
            parameters = {
                    @Parameter(in = ParameterIn.QUERY, name = "page", description = "Page number"),
                    @Parameter(in = ParameterIn.QUERY, name = "size", description = "Page size"),
                    @Parameter(in = ParameterIn.QUERY, name = "order_by", description = "Order by"),
                    @Parameter(in = ParameterIn.QUERY, name = "order_direction", description = "Order direction (ASC, DESC)")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "List if merchants", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = MerchantResponse.class)))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            },
            security = {
                    @SecurityRequirement(name = SecurityConstants.BEARER)
            }
    ))
    public RouterFunction<ServerResponse> listAllMerchants(MerchantHandler handler) {
        return RouterFunctions.route().path(API_ROOT, builder -> builder
                .GET("", RequestPredicates.accept(MediaType.APPLICATION_JSON), handler::listAll)
        ).build();
    }

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "getMerchant", summary = "Get merchant by Id", tags = {DOC_TAGS},
            parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "Merchant Id")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Merchant", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MerchantResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid Id supplied"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Merchant not found")
            },
            security = {
                    @SecurityRequirement(name = SecurityConstants.BEARER)
            }
    ))
    public RouterFunction<ServerResponse> getMerchant(MerchantHandler handler) {
        return RouterFunctions.route().path(API_ROOT, builder -> builder
                .GET("/{id}", RequestPredicates.accept(MediaType.APPLICATION_JSON), handler::getMerchant)
        ).build();
    }

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "createMerchant", summary = "Create new merchant", tags = {DOC_TAGS},
            requestBody = @RequestBody(description = "Merchant", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MerchantRequest.class))),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MerchantResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid Merchant"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "409", description = "Duplicate Merchant"),
                    @ApiResponse(responseCode = "500", description = "Server Error")
            },
            security = {
                    @SecurityRequirement(name = SecurityConstants.BEARER)
            }
    ))
    public RouterFunction<ServerResponse> createMerchantRoute(MerchantHandler handler) {
        return RouterFunctions.route().path(API_ROOT, builder -> builder
                .POST("", RequestPredicates.accept(MediaType.APPLICATION_JSON), handler::createMerchant)
        ).build();
    }
}
