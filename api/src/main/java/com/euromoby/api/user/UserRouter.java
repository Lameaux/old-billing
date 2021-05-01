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
            operationId = "listAllUsers", summary = "List all users", tags = {DOC_TAGS},
            parameters = {
                    @Parameter(in = ParameterIn.QUERY, name = "page", description = "Page number"),
                    @Parameter(in = ParameterIn.QUERY, name = "size", description = "Page size"),
                    @Parameter(in = ParameterIn.QUERY, name = "order_by", description = "Order by"),
                    @Parameter(in = ParameterIn.QUERY, name = "order_direction", description = "Order direction (ASC, DESC)")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of users", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = UserResponse.class)))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
            },
            security = {
                    @SecurityRequirement(name = SecurityConstants.BEARER)
            }
    ))
    public RouterFunction<ServerResponse> listAllUsers(UserHandler handler) {
        return RouterFunctions.route().path(API_ROOT, builder -> builder
                .GET("", RequestPredicates.accept(MediaType.APPLICATION_JSON), handler::listAll)
        ).build();
    }

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "findUsersByFilter", summary = "Find users by filter", tags = {DOC_TAGS},
            parameters = {
                    @Parameter(in = ParameterIn.QUERY, name = "email", description = "E-mail"),
                    @Parameter(in = ParameterIn.QUERY, name = "msisdn", description = "MSISDN"),
                    @Parameter(in = ParameterIn.QUERY, name = "name", description = "Name"),
                    @Parameter(in = ParameterIn.QUERY, name = "page", description = "Page number"),
                    @Parameter(in = ParameterIn.QUERY, name = "size", description = "Page size")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of users", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = UserResponse.class)))),
                    @ApiResponse(responseCode = "400", description = "Invalid query"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
            },
            security = {
                    @SecurityRequirement(name = SecurityConstants.BEARER)
            }
    ))
    public RouterFunction<ServerResponse> findUsersByFilter(UserHandler handler) {
        return RouterFunctions.route().path(API_ROOT, builder -> builder
                .GET("/find_by", RequestPredicates.accept(MediaType.APPLICATION_JSON), handler::findByFilter)
        ).build();
    }

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "getAuthenticatedUser", summary = "Get authenticated user", tags = {DOC_TAGS},
            responses = {
                    @ApiResponse(responseCode = "200", description = "User", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserAndMerchantsResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
            },
            security = {
                    @SecurityRequirement(name = SecurityConstants.BEARER)
            }
    ))
    public RouterFunction<ServerResponse> getAuthenticatedUser(UserHandler handler) {
        return RouterFunctions.route().path(API_ROOT, builder -> builder
                .GET("/me", RequestPredicates.accept(MediaType.APPLICATION_JSON), handler::getAuthenticatedUser)
        ).build();
    }

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "getUser", summary = "Get user by Id", tags = {DOC_TAGS},
            parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "User Id")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "User", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserAndMerchantsResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid Id supplied"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            },
            security = {
                    @SecurityRequirement(name = SecurityConstants.BEARER)
            }
    ))
    public RouterFunction<ServerResponse> getUser(UserHandler handler) {
        return RouterFunctions.route().path(API_ROOT, builder -> builder
                .GET("/{id}", RequestPredicates.accept(MediaType.APPLICATION_JSON), handler::getUser)
        ).build();
    }

    @Bean
    @RouterOperation(operation = @Operation(
            operationId = "createUser", summary = "Create new user", tags = {DOC_TAGS},
            requestBody = @RequestBody(description = "User", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserRequest.class))),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid body"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "409", description = "Duplicate User"),
                    @ApiResponse(responseCode = "500", description = "Server Error")
            },
            security = {
                    @SecurityRequirement(name = SecurityConstants.BEARER)
            }
    ))
    public RouterFunction<ServerResponse> createUser(UserHandler userHandler) {
        return RouterFunctions.route().path(API_ROOT, builder -> builder
                .POST("", RequestPredicates.accept(MediaType.APPLICATION_JSON), userHandler::createUser)
        ).build();
    }
}
