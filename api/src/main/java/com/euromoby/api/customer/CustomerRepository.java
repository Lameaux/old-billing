package com.euromoby.api.customer;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, UUID> {
    Flux<Customer> findAllByMerchantIdAndIdNotNull(UUID merchantId, Pageable page);

    @Query("SELECT * FROM customers WHERE merchant_id = :merchantId AND (merchant_reference LIKE :merchantReference OR email LIKE :email OR msisdn LIKE :msisdn OR name LIKE :name) ORDER BY name LIMIT :limit OFFSET :offset")
    Flux<Customer> findAllByMerchantIdAndFilter(UUID merchantId, String merchantReference, String email, String msisdn, String name, int limit, long offset);

    Mono<Customer> findByIdAndMerchantId(UUID id, UUID merchantId);
}
