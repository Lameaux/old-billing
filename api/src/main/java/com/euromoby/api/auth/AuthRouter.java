package com.euromoby.api.auth;

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
    private static final String DOC_TAGS = "User Authentication";

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "login", summary = "Login", tags = {DOC_TAGS},
            requestBody = @RequestBody(description = "Credentials", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LoginRequest.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AuthResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid Credentials"),
                    @ApiResponse(responseCode = "500", description = "Server Error")
            }
    ))
    public RouterFunction<ServerResponse> login(AuthHandler authHandler) {
        return RouterFunctions.route().path(API_ROOT, builder ->
                builder.POST("/login", RequestPredicates.accept(MediaType.APPLICATION_JSON), authHandler::login)
        ).build();
    }

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "register", summary = "New User Registration", tags = {DOC_TAGS},
            requestBody = @RequestBody(description = "Form", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RegisterRequest.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AuthResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid User"),
                    @ApiResponse(responseCode = "409", description = "Duplicate User"),
                    @ApiResponse(responseCode = "500", description = "Server Error")
            }
    ))
    public RouterFunction<ServerResponse> register(AuthHandler authHandler) {
        return RouterFunctions.route().path(API_ROOT, builder ->
                builder.POST("/register", RequestPredicates.accept(MediaType.APPLICATION_JSON), authHandler::register)
        ).build();
    }
}
