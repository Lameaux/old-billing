package com.euromoby.api.user.rest;

import com.euromoby.api.user.repo.UserRepository;
import com.euromoby.api.user.rest.dto.AuthRequest;
import com.euromoby.api.user.rest.dto.AuthResponse;
import com.euromoby.api.user.rest.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
class AuthHandler {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Autowired
    public AuthHandler(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    Mono<ServerResponse> auth(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(AuthRequest.class).flatMap(authRequest -> {
            if (!authRequest.isValid()) {
                return badRequestResponse();
            }

            return userRepository.findByEmailAndEnabled(authRequest.getEmail(), true)
                    .filter(user -> passwordEncoder.matches(authRequest.getPassword(), user.getPassword()))
                    .flatMap(user -> ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(
                                    new AuthResponse(jwtProvider.generateToken(user))
                            )
                    );

        }).switchIfEmpty(unauthorizedResponse());
    }

    private Mono<ServerResponse> unauthorizedResponse() {
        return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
    }

    private Mono<ServerResponse> badRequestResponse() {
        return ServerResponse.badRequest().build();
    }
}
