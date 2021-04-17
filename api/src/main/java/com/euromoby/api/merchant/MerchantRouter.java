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
            operationId = "listMerchants", summary = "List all Merchants", tags = {DOC_TAGS},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Merchants", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = MerchantResponse.class))))
            },
            security = {
                    @SecurityRequirement(name = SecurityConstants.BEARER)
            }
    ))
    public RouterFunction<ServerResponse> listMerchantsRoute(MerchantHandler merchantHandler) {
        return RouterFunctions.route().path(API_ROOT, builder -> builder
                .GET("", RequestPredicates.accept(MediaType.APPLICATION_JSON), merchantHandler::listMerchants)
        ).build();
    }

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "findMerchantById", summary = "Get Merchant by Id", tags = {DOC_TAGS},
            parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "Merchant Id")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MerchantResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid Merchant Id supplied"),
                    @ApiResponse(responseCode = "404", description = "Merchant not found")
            },
            security = {
                    @SecurityRequirement(name = SecurityConstants.HEADER_MERCHANT),
                    @SecurityRequirement(name = SecurityConstants.HEADER_API_KEY),
                    @SecurityRequirement(name = SecurityConstants.BEARER)
            }
    ))
    public RouterFunction<ServerResponse> getMerchantRoute(MerchantHandler merchantHandler) {
        return RouterFunctions.route().path(API_ROOT, builder -> builder
                .GET("/{id}", RequestPredicates.accept(MediaType.APPLICATION_JSON), merchantHandler::getMerchant)
        ).build();
    }

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "createMerchant", summary = "Create a new Merchant", tags = {DOC_TAGS},
            requestBody = @RequestBody(description = "Merchant", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MerchantRequest.class))),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MerchantResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid Merchant"),
                    @ApiResponse(responseCode = "409", description = "Duplicate Merchant"),
                    @ApiResponse(responseCode = "500", description = "Server Error")
            },
            security = {
                    @SecurityRequirement(name = SecurityConstants.BEARER)
            }
    ))
    public RouterFunction<ServerResponse> createMerchantRoute(MerchantHandler merchantHandler) {
        return RouterFunctions.route().path(API_ROOT, builder -> builder
                .POST("", RequestPredicates.accept(MediaType.APPLICATION_JSON), merchantHandler::createMerchant)
        ).build();
    }
}
