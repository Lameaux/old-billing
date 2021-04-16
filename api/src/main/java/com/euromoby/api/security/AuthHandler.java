package com.euromoby.api.security;

import com.euromoby.api.common.ErrorCode;
import com.euromoby.api.common.ErrorResponse;
import com.euromoby.api.user.User;
import com.euromoby.api.user.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class AuthHandler {
    private static final String EMAIL_PASSWORD = "email,password";
    private static final String REFRESH_TOKEN = "refresh_token";

    private JWTUtil jwtUtil;
    private UserRepository userRepository;

    @Autowired
    public AuthHandler(JWTUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    Mono<ServerResponse> authenticate(ServerRequest serverRequest) {
        Mono<AuthRequest> authRequestMono = serverRequest.bodyToMono(AuthRequest.class);

        return authRequestMono.flatMap(
                authRequest -> userRepository.findByEmail(authRequest.getEmail()).filter(User::isActive)
                        .flatMap(
                                user -> {
                                    if (BCrypt.checkpw(authRequest.getPassword(), user.getPasswordHash())) {
                                        return ServerResponse
                                                .ok()
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .bodyValue(new AuthResponse(jwtUtil.generateToken(user)));
                                    } else {
                                        return ErrorResponse.unauthorized(ErrorCode.INVALID_CREDENTIALS, EMAIL_PASSWORD);
                                    }
                                }
                        )
        ).switchIfEmpty(ErrorResponse.unauthorized(ErrorCode.INVALID_CREDENTIALS, EMAIL_PASSWORD));
    }

    Mono<ServerResponse> refresh(ServerRequest serverRequest) {
        return ErrorResponse.unauthorized(ErrorCode.INVALID_CREDENTIALS, REFRESH_TOKEN);
    }
}
