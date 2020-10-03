package com.euromoby.payment.repository;

import com.euromoby.payment.entity.Payment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PaymentRepository extends ReactiveCrudRepository<Payment, String> {
    Flux<Payment> findAllByMerchantId(String merchantId);

    Mono<Payment> findByIdAndMerchantId(String id, String merchantId);
}
