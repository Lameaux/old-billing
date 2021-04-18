package com.euromoby.api.user;

import com.euromoby.api.common.ErrorCode;
import com.euromoby.api.common.ErrorResponse;
import com.euromoby.api.common.UUIDValidator;
import com.euromoby.api.security.*;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserHandler {
    private static final String PARAM_ID = "id";
    private static final String PARAM_EMAIL = "email";
    static final String PARAM_RECAPTCHA = "recaptcha";

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

        return serverRequest.principal().flatMap(principal-> {
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

    @IsAnonymous
    Mono<ServerResponse> createUser(ServerRequest serverRequest) {



        return serverRequest.principal().flatMap(principal-> {
            UserAuthentication userAuthentication = (UserAuthentication) principal;

            if (!AuthenticationUtil.isAdmin(userAuthentication)) {
                return ErrorResponse.forbidden(ErrorCode.ACCESS_DENIED, "user");
            }

            return doCreateUser(serverRequest);

        }).switchIfEmpty(createUserWithRecaptcha(serverRequest));
    }

    private Mono<ServerResponse> createUserWithRecaptcha(ServerRequest serverRequest) {
        Optional<String> oRecaptcha = serverRequest.queryParam(PARAM_RECAPTCHA);

        if (oRecaptcha.isEmpty()) {
            return ErrorResponse.badRequest(ErrorCode.MISSING_QUERY_PARAM, PARAM_RECAPTCHA);
        }

        if (!validateRecaptcha(oRecaptcha.get())) {
            return ErrorResponse.badRequest(ErrorCode.INVALID_QUERY_PARAM, PARAM_RECAPTCHA);
        }

        return doCreateUser(serverRequest);
    }

    private Mono<ServerResponse> doCreateUser(ServerRequest serverRequest) {
        Mono<UserRequest> userRequestMono = serverRequest.bodyToMono(UserRequest.class);
        Mono<UserResponse> userResponseMono = userService.createUser(userRequestMono);

        return userResponseMono.flatMap(u -> ServerResponse.created(null)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(u)
        ).onErrorResume(
                DuplicateKeyException.class,
                throwable -> ErrorResponse.conflict(ErrorCode.DUPLICATE_VALUE, PARAM_EMAIL)
        );
    }

    private boolean validateRecaptcha(String recaptcha) {
        return Objects.equals(recaptcha, "42"); // FIXME
    }
}
