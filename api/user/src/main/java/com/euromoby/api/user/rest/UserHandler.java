package com.euromoby.api.user.rest;

import com.euromoby.api.user.model.User;
import com.euromoby.api.user.rest.dto.UserRequest;
import com.euromoby.api.user.service.UserService;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
class UserHandler {
    private final UserService userService;

    @Autowired
    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    Mono<ServerResponse> getById(ServerRequest serverRequest) {
        return defaultReadResponse(userService.get(id(serverRequest)));
    }

    Mono<ServerResponse> auth(ServerRequest serverRequest) {
        return defaultReadResponse(Flux.empty());
    }

    Mono<ServerResponse> all(ServerRequest serverRequest) {
        return defaultReadResponse(userService.all());
    }

    Mono<ServerResponse> deleteById(ServerRequest serverRequest) {
        return defaultReadResponse(userService.delete(id(serverRequest)));
    }

    Mono<ServerResponse> updateById(ServerRequest serverRequest) {
        Flux<User> id = serverRequest.bodyToFlux(UserRequest.class)
                .flatMap(userRequest -> userService.update(id(serverRequest), userRequest));
        return defaultReadResponse(id);
    }

    Mono<ServerResponse> create(ServerRequest serverRequest) {
        Flux<User> flux = serverRequest.bodyToFlux(UserRequest.class)
                .flatMap(userService::create);
        return defaultWriteResponse(flux);
    }

    private static Mono<ServerResponse> defaultWriteResponse(Publisher<User> users) {
        return Mono.from(users).flatMap(user -> ServerResponse
                .created(URI.create("/users/" + user.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .build()
        );
    }

    private static Mono<ServerResponse> defaultReadResponse(Publisher<User> profiles) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(profiles, User.class);
    }

    private static String id(ServerRequest r) {
        return r.pathVariable("id");
    }

}
