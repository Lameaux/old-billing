package com.euromoby.api.merchant;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface MerchantRepository extends ReactiveCrudRepository<Merchant, UUID> {
}
