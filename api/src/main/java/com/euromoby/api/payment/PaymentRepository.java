package com.euromoby.api.payment;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PaymentRepository extends ReactiveCrudRepository<Payment, UUID> {
    Flux<Payment> findAllByMerchantId(UUID merchantId);

    Mono<Payment> findByIdAndMerchantId(UUID id, UUID merchantId);

    Mono<Payment> findByMerchantIdAndMerchantReference(UUID merchantId, String merchantReference);
}
