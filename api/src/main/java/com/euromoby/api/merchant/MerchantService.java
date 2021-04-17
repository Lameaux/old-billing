package com.euromoby.api.merchant;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;

@Service
public class MerchantService {
    private static final Function<Merchant, MerchantResponse> TO_DTO = m -> {
        var dto = new MerchantResponse();
        dto.setId(m.getId());
        dto.setName(m.getName());
        dto.setCreatedAt(m.getCreatedAt());
        dto.setUpdatedAt(m.getUpdatedAt());
        return dto;
    };
    private final MerchantRepository merchantRepository;

    public MerchantService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    Flux<MerchantResponse> getAllMerchants() {
        return merchantRepository.findAll().map(TO_DTO);
    }

    Flux<MerchantResponse> getAllMerchantsByUserId(UUID userId) {
        return merchantRepository.findAll().map(TO_DTO); // FIXME
    }

    Mono<MerchantResponse> getMerchant(UUID id) {
        return merchantRepository.findById(id).map(TO_DTO);
    }

    Mono<MerchantResponse> createMerchant(Mono<MerchantRequest> merchantRequestMono) {
        return merchantRequestMono.flatMap(merchantRequest -> {
            Merchant m = new Merchant();
            m.setName(merchantRequest.getName());
            return merchantRepository.save(m).map(TO_DTO);
        });
    }
}
