package com.euromoby.api.auth;

import com.euromoby.api.common.ErrorCode;
import com.euromoby.api.common.ErrorResponse;
import com.euromoby.api.security.IsAnonymous;
import com.euromoby.api.security.JwtUtil;
import com.euromoby.api.user.*;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class AuthHandler {
    static final String EMAIL_PASSWORD = "email,password";
    static final String PARAM_EMAIL = "email";
    static final String PARAM_RECAPTCHA = "recaptcha";

    private JwtUtil jwtUtil;
    private UserRepository userRepository;
    private UserService userService;

    @Autowired
    public AuthHandler(JwtUtil jwtUtil, UserRepository userRepository, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @IsAnonymous
    Mono<ServerResponse> login(ServerRequest serverRequest) {
        Mono<LoginRequest> authRequestMono = serverRequest.bodyToMono(LoginRequest.class);

        return authRequestMono.flatMap(
                authRequest -> userRepository.findByEmail(authRequest.getEmail()).filter(User::isActive)
                        .flatMap(
                                user -> {
                                    if (BCrypt.checkpw(authRequest.getPassword(), user.getPasswordHash())) {
                                        return ServerResponse
                                                .ok()
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .bodyValue(jwtUtil.buildAuthResponse(user));
                                    } else {
                                        return ErrorResponse.unauthorized(ErrorCode.INVALID_CREDENTIALS, EMAIL_PASSWORD);
                                    }
                                }
                        )
        ).switchIfEmpty(ErrorResponse.unauthorized(ErrorCode.INVALID_CREDENTIALS, EMAIL_PASSWORD));
    }

    @IsAnonymous
    Mono<ServerResponse> register(ServerRequest serverRequest) {
        Mono<RegisterRequest> registerRequestMono = serverRequest.bodyToMono(RegisterRequest.class);

        return registerRequestMono.flatMap(
                registerRequest -> {
                    Mono<ServerResponse> validationResponse = validate(registerRequest);
                    if (validationResponse != null) {
                        return validationResponse;
                    }

                    Mono<UserResponse> userResponseMono = userService.createUser(
                            Mono.just(toUserRequest(registerRequest))
                    );

                    return userResponseMono.flatMap(userResponse -> ServerResponse
                            .ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(jwtUtil.buildAuthResponse(toUser(userResponse)))
                    ).onErrorResume(
                            DataIntegrityViolationException.class,
                            throwable -> ErrorResponse.conflict(ErrorCode.DUPLICATE_VALUE, PARAM_EMAIL)
                    );
                }
        );
    }

    private UserRequest toUserRequest(RegisterRequest registerRequest) {
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail(registerRequest.getEmail());
        userRequest.setPassword(registerRequest.getPassword());
        userRequest.setMsisdn(registerRequest.getMsisdn());
        userRequest.setName(registerRequest.getName());
        return userRequest;
    }

    private User toUser(UserResponse userResponse) {
        User user = new User();
        user.setId(userResponse.getId());
        user.setEmail(userResponse.getEmail());
        user.setName(userResponse.getName());
        user.setAdmin(userResponse.isAdmin());
        return user;
    }

    private Mono<ServerResponse> validate(RegisterRequest registerRequest) {
        if (!StringUtils.hasText(registerRequest.getRecaptcha())) {
            return ErrorResponse.badRequest(ErrorCode.MISSING_BODY_PARAM, PARAM_RECAPTCHA);
        }

        // FIXME
        if (!Objects.equals(registerRequest.getRecaptcha(), "42")) {
            return ErrorResponse.badRequest(ErrorCode.INVALID_BODY_PARAM, PARAM_RECAPTCHA);
        }

        return null;
    }
}
