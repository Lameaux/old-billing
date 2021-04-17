package com.euromoby.api.user;

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
public class UserRouter {

    private static final String API_ROOT = "/api/v1/users";
    private static final String DOC_TAGS = "Users";

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "listUsers", summary = "List all Users", tags = {DOC_TAGS},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = UserResponse.class))))
            },
            security = {
                    @SecurityRequirement(name = SecurityConstants.BEARER)
            }
    ))
    public RouterFunction<ServerResponse> listUsersRoute(UserHandler userHandler) {
        return RouterFunctions.route().path(API_ROOT, builder -> builder
                .GET("", RequestPredicates.accept(MediaType.APPLICATION_JSON), userHandler::listUsers)
        ).build();
    }

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "getUserByEmail", summary = "Get User by E-mail", tags = {DOC_TAGS},
            parameters = {@Parameter(in = ParameterIn.QUERY, name = "email", description = "E-mail")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = UserResponse.class)))),
                    @ApiResponse(responseCode = "400", description = "Invalid E-mail supplied"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            },
            security = {
                    @SecurityRequirement(name = SecurityConstants.BEARER)
            }
    ))
    public RouterFunction<ServerResponse> findUserRoute(UserHandler userHandler) {
        return RouterFunctions.route().path(API_ROOT, builder -> builder
                .GET("/find_by", RequestPredicates.accept(MediaType.APPLICATION_JSON), userHandler::getUserByEmail)
        ).build();
    }

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "findUserById", summary = "Get User by Id", tags = {DOC_TAGS},
            parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "User Id")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid User Id supplied"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            },
            security = {
                    @SecurityRequirement(name = SecurityConstants.HEADER_MERCHANT),
                    @SecurityRequirement(name = SecurityConstants.HEADER_API_KEY),
                    @SecurityRequirement(name = SecurityConstants.BEARER)
            }
    ))
    public RouterFunction<ServerResponse> getUserRoute(UserHandler userHandler) {
        return RouterFunctions.route().path(API_ROOT, builder -> builder
                .GET("/{id}", RequestPredicates.accept(MediaType.APPLICATION_JSON), userHandler::getUser)
        ).build();
    }

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "createUser", summary = "Create a new User", tags = {DOC_TAGS},
            requestBody = @RequestBody(description = "User", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserRequest.class))),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid User"),
                    @ApiResponse(responseCode = "409", description = "Duplicate User"),
                    @ApiResponse(responseCode = "500", description = "Server Error")
            }
    ))
    public RouterFunction<ServerResponse> createUserRoute(UserHandler userHandler) {
        return RouterFunctions.route().path(API_ROOT, builder -> builder
                .POST("", RequestPredicates.accept(MediaType.APPLICATION_JSON), userHandler::createUser)
        ).build();
    }
}
