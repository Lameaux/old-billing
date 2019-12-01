package com.euromoby.api.auth_api.repo;

import com.euromoby.api.auth_api.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Mono<User> findByUsernameAndEnabled(String username, Boolean enabled);
}
