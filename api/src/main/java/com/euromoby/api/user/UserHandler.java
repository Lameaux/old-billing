package com.euromoby.api.user;

import com.euromoby.api.common.ErrorCode;
import com.euromoby.api.common.ErrorResponse;
import com.euromoby.api.common.UUIDValidator;
import com.euromoby.api.security.AuthenticationUtil;
import com.euromoby.api.security.IsAdmin;
import com.euromoby.api.security.IsUser;
import com.euromoby.api.security.UserAuthentication;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
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
    private static final int DEFAULT_PAGE_NUM = 0;
    private static final int DEFAULT_PAGE_SIZE = 50;
    private static final String PARAM_ORDER_BY = "order_by";
    private static final String PARAM_ORDER_DIRECTION = "order_direction";
    private static final String PARAM_PAGE = "page";
    private static final String PARAM_SIZE = "size";
    private static final String PARAM_ID = "id";
    private static final String PARAM_MSISDN = "msisdn";
    private static final String PARAM_NAME = "name";

    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    @IsAdmin
    Mono<ServerResponse> listAll(ServerRequest serverRequest) {
        Optional<String> orderBy = serverRequest.queryParam(PARAM_ORDER_BY);
        Optional<String> orderDirection = serverRequest.queryParam(PARAM_ORDER_DIRECTION);
        Optional<String> page = serverRequest.queryParam(PARAM_PAGE);
        Optional<String> size = serverRequest.queryParam(PARAM_SIZE);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.getAllUsers(
                        orderBy.orElse(PARAM_EMAIL),
                        orderDirection.orElse(Sort.Direction.ASC.toString()),
                        page.map(Integer::valueOf).orElse(DEFAULT_PAGE_NUM),
                        size.map(Integer::valueOf).orElse(DEFAULT_PAGE_SIZE)
                ), UserResponse.class);
    }

    @IsAdmin
    Mono<ServerResponse> findByFilter(ServerRequest serverRequest) {
        Optional<String> email = serverRequest.queryParam(PARAM_EMAIL);
        Optional<String> msisdn = serverRequest.queryParam(PARAM_MSISDN);
        Optional<String> name = serverRequest.queryParam(PARAM_NAME);
        Optional<String> page = serverRequest.queryParam(PARAM_PAGE);
        Optional<String> size = serverRequest.queryParam(PARAM_SIZE);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.findUsersByFilter(
                        email.orElse(""),
                        msisdn.orElse(""),
                        name.orElse(""),
                        page.map(Integer::valueOf).orElse(DEFAULT_PAGE_NUM),
                        size.map(Integer::valueOf).orElse(DEFAULT_PAGE_SIZE)
                ), UserResponse.class);
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

            return userService.getUserAndMerchants(userId)
                    .flatMap(u -> ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(u)
                    ).switchIfEmpty(ErrorResponse.notFound(ErrorCode.NOT_FOUND, "user"));
        });
    }

    @IsUser
    Mono<ServerResponse> getAuthenticatedUser(ServerRequest serverRequest) {
        return serverRequest.principal().flatMap(principal -> {
            UserAuthentication userAuthentication = (UserAuthentication) principal;

            return userService.getUserAndMerchants((UUID) userAuthentication.getPrincipal())
                    .flatMap(u -> ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(u)
                    );
        });
    }

    @IsAdmin
    Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        Mono<UserRequest> userRequestMono = serverRequest.bodyToMono(UserRequest.class);

        return userService.createUser(userRequestMono)
                .flatMap(u -> ServerResponse.created(null)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(u)
                ).onErrorResume(
                        DataIntegrityViolationException.class,
                        throwable -> ErrorResponse.conflict(ErrorCode.DUPLICATE_VALUE, PARAM_EMAIL)
                );
    }
}
