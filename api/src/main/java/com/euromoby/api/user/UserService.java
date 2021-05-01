package com.euromoby.api.user;

import com.euromoby.api.merchant.MerchantRepository;
import com.euromoby.api.merchant.MerchantService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;

@Service
public class UserService {
    public static final Function<User, UserResponse> TO_DTO = u -> {
        var dto = new UserResponse();
        dto.setId(u.getId());
        dto.setEmail(u.getEmail());
        dto.setMsisdn(u.getMsisdn());
        dto.setName(u.getName());
        dto.setActive(u.isActive());
        dto.setAdmin(u.isAdmin());
        dto.setEmailVerified(u.isEmailVerified());
        dto.setMsisdnVerified(u.isMsisdnVerified());
        dto.setCreatedAt(u.getCreatedAt());
        dto.setUpdatedAt(u.getUpdatedAt());
        return dto;
    };

    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;
    private final UserMerchantRepository userMerchantRepository;


    @Autowired
    public UserService(UserRepository userRepository, MerchantRepository merchantRepository, UserMerchantRepository userMerchantRepository) {
        this.userRepository = userRepository;
        this.merchantRepository = merchantRepository;
        this.userMerchantRepository = userMerchantRepository;
    }

    Flux<UserResponse> getAllUsers(String orderBy, String orderDirection, int page, int size) {
        return userRepository.findAllByIdNotNull(
                PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(orderDirection), orderBy))
        ).map(TO_DTO);
    }

    Flux<UserResponse> findUsersByFilter(String email, String msisdn, String name, int page, int size) {
        var pageRequest = PageRequest.of(page, size);

        return userRepository.findAllByFilter(
                email,
                msisdn,
                name,
                pageRequest.getPageSize(),
                pageRequest.getOffset()
        ).map(TO_DTO);
    }

    Mono<UserAndMerchantsResponse> getUserAndMerchants(UUID userId) {
        return userRepository.findById(userId).map(TO_DTO)
                .flatMap(
                        user -> userMerchantRepository.findAllByUserId(userId)
                                .flatMap(
                                        userMerchant -> merchantRepository.findById(userMerchant.getMerchantId())
                                                .map(MerchantService.TO_DTO)
                                                .map(merchantResponse -> new MerchantWithRoleResponse(
                                                                merchantResponse,
                                                                userMerchant.getRole()
                                                        )
                                                )
                                )
                                .collectList()
                                .map(list -> new UserAndMerchantsResponse(user, list))
                );
    }

    public Mono<UserResponse> createUser(Mono<UserRequest> userRequestMono) {
        return userRequestMono.flatMap(userRequest -> {
            User u = new User();
            u.setEmail(userRequest.getEmail());
            u.setPasswordHash(BCrypt.hashpw(userRequest.getPassword(), BCrypt.gensalt()));
            u.setMsisdn(userRequest.getMsisdn());
            u.setName(userRequest.getName());
            u.setActive(true);
            u.setAdmin(false);
            u.setEmailVerified(false);
            u.setMsisdnVerified(false);
            return userRepository.save(u).map(TO_DTO);
        });
    }
}
