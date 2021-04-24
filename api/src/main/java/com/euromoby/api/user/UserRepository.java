package com.euromoby.api.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends ReactiveCrudRepository<User, UUID> {
    Flux<User> findAllByIdNotNull(Pageable page);

    Flux<User> findAllByEmailOrMsisdnOrName(String email, String msisdn, String name, Pageable page);

    Mono<User> findByEmail(String email);
}
