package com.euromoby.api.user.repo;

import com.euromoby.api.user.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Mono<User> findByEmailAndEnabled(String email, Boolean enabled);
}
