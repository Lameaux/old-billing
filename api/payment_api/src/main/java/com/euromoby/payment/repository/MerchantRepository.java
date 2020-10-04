package com.euromoby.payment.repository;

import com.euromoby.payment.entity.Merchant;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface MerchantRepository extends ReactiveCrudRepository<Merchant, UUID> {
}
