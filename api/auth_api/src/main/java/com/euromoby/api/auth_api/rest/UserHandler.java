package com.euromoby.api.auth_api.rest;

import com.euromoby.api.auth_api.repo.UserRepository;
import com.euromoby.api.auth_api.rest.dto.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Component
class UserHandler {
    private final UserRepository userRepository;

    @Autowired
    public UserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    Mono<ServerResponse> currentUser(ServerRequest serverRequest) {
        var userResponseMono = serverRequest.principal()
                .map(Principal::getName)
                .flatMap(userRepository::findById)
                .map(UserResponse::fromUser);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userResponseMono, UserResponse.class)
                .switchIfEmpty(unauthorizedResponse());
    }

    private Mono<ServerResponse> unauthorizedResponse() {
        return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
    }
}
