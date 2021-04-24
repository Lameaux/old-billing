package com.euromoby.api.merchant;

import com.euromoby.api.user.UserMerchant;
import com.euromoby.api.user.UserMerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;

@Service
public class MerchantService {
    public static final Function<Merchant, MerchantResponse> TO_DTO = m -> {
        var dto = new MerchantResponse();
        dto.setId(m.getId());
        dto.setName(m.getName());
        dto.setApiKey(m.getApiKey());
        dto.setDescription(m.getDescription());
        dto.setEnv(m.getEnv());
        dto.setActive(m.isActive());
        dto.setCreatedAt(m.getCreatedAt());
        dto.setUpdatedAt(m.getUpdatedAt());
        return dto;
    };
    private final MerchantRepository merchantRepository;
    private final UserMerchantRepository userMerchantRepository;

    @Autowired
    public MerchantService(MerchantRepository merchantRepository, UserMerchantRepository userMerchantRepository) {
        this.merchantRepository = merchantRepository;
        this.userMerchantRepository = userMerchantRepository;
    }

    Flux<MerchantResponse> getAllMerchants() {
        return merchantRepository.findAll().map(TO_DTO);
    }

    Flux<MerchantResponse> getAllMerchantsForUserId(UUID userId) {

        Flux<UserMerchant> userMerchants = userMerchantRepository.findAllByUserId(userId);

        return userMerchants.flatMap(
                userMerchant -> merchantRepository.findById(userMerchant.getMerchantId()).map(TO_DTO)
        );
    }

    public Mono<MerchantResponse> getMerchant(UUID id) {
        return merchantRepository.findById(id).map(TO_DTO);
    }

    Mono<MerchantResponse> getMerchantForUserId(UUID userId, UUID merchantId) {
        Mono<UserMerchant> userMerchantMono = userMerchantRepository.findByUserIdAndMerchantId(userId, merchantId);
        return userMerchantMono.flatMap(
                userMerchant -> merchantRepository.findById(userMerchant.getMerchantId()).map(TO_DTO)
        );
    }

    Mono<MerchantResponse> createMerchant(Mono<MerchantRequest> merchantRequestMono, UUID owner) {
        return merchantRequestMono.flatMap(merchantRequest -> {
            Merchant m = new Merchant();
            m.setName(merchantRequest.getName());
            m.setApiKey(UUID.randomUUID().toString());
            m.setDescription(merchantRequest.getDescription());
            m.setEnv(merchantRequest.getEnv());

            if (merchantRequest.getEnv() == MerchantEnv.TEST) {
                m.setActive(true);
            } else {
                m.setActive(false);
            }

            return merchantRepository.save(m).map(TO_DTO);
        });
    }
}
