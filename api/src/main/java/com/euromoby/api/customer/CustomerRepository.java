package com.euromoby.api.customer;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, UUID> {
    Flux<Customer> findAllByMerchantId(UUID merchantId);
    Mono<Customer> findByIdAndMerchantId(UUID id, UUID merchantId);
    Mono<Customer> findByMerchantIdAndMerchantReference(UUID merchantId, String merchantReference);
}
