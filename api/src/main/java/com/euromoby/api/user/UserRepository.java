package com.euromoby.api.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends ReactiveCrudRepository<User, UUID> {
    Flux<User> findAllByIdNotNull(Pageable page);

    @Query("SELECT * FROM users WHERE email LIKE :email OR msisdn LIKE :msisdn OR name LIKE :name ORDER BY email LIMIT :limit OFFSET :offset")
    Flux<User> findAllByFilter(String email, String msisdn, String name, int limit, long offset);

    Mono<User> findByEmail(String email);
}
