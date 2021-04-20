package com.euromoby.api.user;

import com.euromoby.api.common.ErrorCode;
import com.euromoby.api.common.ErrorResponse;
import com.euromoby.api.common.UUIDValidator;
import com.euromoby.api.security.AuthenticationUtil;
import com.euromoby.api.security.IsAdmin;
import com.euromoby.api.security.IsUser;
import com.euromoby.api.security.UserAuthentication;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserHandler {
    static final String PARAM_EMAIL = "email";
    private static final String PARAM_ID = "id";
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    @IsAdmin
    Mono<ServerResponse> listUsers(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.getAllUsers(), UserResponse.class);
    }

    @IsUser
    Mono<ServerResponse> getUser(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable(PARAM_ID);
        if (!UUIDValidator.isValid(id)) {
            return ErrorResponse.badRequest(ErrorCode.INVALID_UUID, PARAM_ID);
        }
        UUID userId = UUID.fromString(id);

        return serverRequest.principal().flatMap(principal -> {
            UserAuthentication userAuthentication = (UserAuthentication) principal;
            if (!AuthenticationUtil.isAdmin(userAuthentication) && !Objects.equals(userAuthentication.getPrincipal(), userId)) {
                return ErrorResponse.forbidden(ErrorCode.ACCESS_DENIED, "user");
            }

            Mono<UserResponse> userResponseMono = userService.getUser(userId);

            return userResponseMono.flatMap(u -> ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(u)
            ).switchIfEmpty(ErrorResponse.notFound(ErrorCode.NOT_FOUND, "user"));
        });
    }

    @IsAdmin
    Mono<ServerResponse> getUserByEmail(ServerRequest serverRequest) {
        Optional<String> oEmail = serverRequest.queryParam(PARAM_EMAIL);
        if (oEmail.isEmpty()) {
            return ErrorResponse.badRequest(ErrorCode.MISSING_QUERY_PARAM, PARAM_EMAIL);
        }

        Mono<UserResponse> userResponseMono = userService.getUserByEmail(oEmail.get());

        return userResponseMono.flatMap(u -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(u)
        ).switchIfEmpty(ErrorResponse.notFound(ErrorCode.NOT_FOUND, "user"));
    }

    @IsAdmin
    Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        Mono<UserRequest> userRequestMono = serverRequest.bodyToMono(UserRequest.class);
        Mono<UserResponse> userResponseMono = userService.createUser(userRequestMono);

        return userResponseMono.flatMap(u -> ServerResponse.created(null)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(u)
        ).onErrorResume(
                DataIntegrityViolationException.class,
                throwable -> ErrorResponse.conflict(ErrorCode.DUPLICATE_VALUE, PARAM_EMAIL)
        );
    }
}
