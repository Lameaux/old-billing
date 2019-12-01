package com.euromoby.api.auth_api.rest;

import com.euromoby.api.auth_api.repo.UserRepository;
import com.euromoby.api.auth_api.rest.dto.AuthResponse;
import com.euromoby.api.auth_api.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
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
        return serverRequest.formData().flatMap(form -> {
            String username = form.getFirst("username");
            String password = form.getFirst("password");

            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                return badRequestResponse();
            }

            return userRepository.findByUsernameAndEnabled(username, true)
                    .filter(user -> passwordEncoder.matches(password, user.getPassword()))
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
