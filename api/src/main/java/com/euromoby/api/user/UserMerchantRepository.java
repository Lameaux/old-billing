package com.euromoby.api.user;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserMerchantRepository extends ReactiveCrudRepository<UserMerchant, UUID> {
    Flux<UserMerchant> findAllByUserId(UUID userId);
}
