package com.euromoby.api.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AuthRouter {
    private static final String API_ROOT = "/api/v1/auth";
    private static final String DOC_TAGS = "Authentication";

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "token", summary = "Get Token", tags = {DOC_TAGS},
            requestBody = @RequestBody(description = "Credentials", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AuthRequest.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AuthResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid Credentials"),
                    @ApiResponse(responseCode = "500", description = "Server Error")
            }
    ))
    public RouterFunction<ServerResponse> authenticate(AuthHandler authHandler) {
        return RouterFunctions.route().path(API_ROOT, builder ->
                builder.POST("/token", RequestPredicates.accept(MediaType.APPLICATION_JSON), authHandler::authenticate)
        ).build();
    }

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "refresh", summary = "Refresh Token", tags = {DOC_TAGS},
            requestBody = @RequestBody(description = "Refresh Token", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RefreshTokenRequest.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AuthResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid Refresh Token"),
                    @ApiResponse(responseCode = "500", description = "Server Error")
            }
    ))
    public RouterFunction<ServerResponse> refresh(AuthHandler authHandler) {
        return RouterFunctions.route().path(API_ROOT, builder ->
                builder.POST("/token/refresh", RequestPredicates.accept(MediaType.APPLICATION_JSON), authHandler::refresh)
        ).build();
    }

}
