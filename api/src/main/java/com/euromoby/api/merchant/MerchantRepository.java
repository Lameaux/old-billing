package com.euromoby.api.merchant;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MerchantRepository extends ReactiveCrudRepository<Merchant, UUID> {
    Mono<Merchant> findByName(String name);
}
