package com.euromoby.api.security;

import com.euromoby.api.common.ErrorCode;
import com.euromoby.api.common.ErrorResponse;
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
    private AuthUserDetailsService authUserDetailsService;

    @Autowired
    public AuthHandler(JWTUtil jwtUtil, AuthUserDetailsService authUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.authUserDetailsService = authUserDetailsService;
    }

    Mono<ServerResponse> authenticate(ServerRequest serverRequest) {
        Mono<AuthRequest> authRequestMono = serverRequest.bodyToMono(AuthRequest.class);

        return authRequestMono.flatMap(
                authRequest -> authUserDetailsService.findByEmail(authRequest.getEmail()).flatMap(authUserDetails -> {
                            if (BCrypt.checkpw(authRequest.getPassword(), authUserDetails.getPassword())) {
                                return ServerResponse
                                        .ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(new AuthResponse(jwtUtil.generateToken(authUserDetails)));
                            } else {
                                return ErrorResponse.unauthorized(ErrorCode.INVALID_CREDENTIALS, EMAIL_PASSWORD);
                            }
                        }
                )).switchIfEmpty(ErrorResponse.unauthorized(ErrorCode.INVALID_CREDENTIALS, EMAIL_PASSWORD));
    }


    Mono<ServerResponse> refresh(ServerRequest serverRequest) {
        return ErrorResponse.unauthorized(ErrorCode.INVALID_CREDENTIALS, REFRESH_TOKEN);
    }
}
