package com.euromoby.api.security;

import com.euromoby.api.merchant.Merchant;
import com.euromoby.api.merchant.MerchantRepository;
import com.euromoby.api.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

// https://ard333.medium.com/authentication-and-authorization-using-jwt-on-spring-webflux-29b81f813e78
// https://www.baeldung.com/spring-security-method-security

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {
    private JwtUtil jwtUtil;
    private UserRepository userRepository;
    private UserMerchantRepository userMerchantRepository;
    private MerchantRepository merchantRepository;

    @Autowired
    public AuthenticationManager(JwtUtil jwtUtil, UserRepository userRepository, UserMerchantRepository userMerchantRepository, MerchantRepository merchantRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.userMerchantRepository = userMerchantRepository;
        this.merchantRepository = merchantRepository;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        if (authentication instanceof JwtAuthentication) {
            return jwt((JwtAuthentication) authentication);
        }

        if (authentication instanceof ApiKeyAuthentication) {
            return apiKey((ApiKeyAuthentication) authentication);
        }

        return Mono.empty();
    }

    private Mono<Authentication> jwt(JwtAuthentication authentication) {
        String merchantName = (String) authentication.getPrincipal();
        String jwtToken = (String) authentication.getCredentials();

        if (!jwtUtil.validateToken(jwtToken)) {
            return Mono.empty();
        }

        UUID userId = jwtUtil.getUserIdFromToken(jwtToken);
        Mono<User> user = userRepository.findById(userId).filter(User::isActive);

        if (!StringUtils.hasText(merchantName)) {
            return user.map(
                    u -> UserAuthentication.create(userId, u.isAdmin() ? UserRole.ROLE_ADMIN : UserRole.ROLE_USER)
            );
        }

        return user.flatMap(u -> {
            Mono<Merchant> merchant = merchantRepository.findByName(merchantName);

            return merchant.flatMap(
                    m -> {
                        if (u.isAdmin()) {
                            return Mono.just(MerchantAuthentication.create(m.getId(), userId, MerchantRole.ROLE_OWNER));
                        }

                        Mono<UserMerchant> userMerchant = userMerchantRepository.findByUserIdAndMerchantId(userId, m.getId());
                        return userMerchant.map(um -> MerchantAuthentication.create(m.getId(), userId, um.getRole()));
                    }
            );
        });
    }

    private Mono<Authentication> apiKey(ApiKeyAuthentication authentication) {
        String merchantName = (String) authentication.getPrincipal();
        String apiKey = (String) authentication.getCredentials();

        Mono<Merchant> merchant = merchantRepository.findByName(merchantName)
                .filter(Merchant::isActive)
                .filter(m -> Objects.equals(apiKey, m.getApiKey()));

        return merchant.map(
                m -> MerchantAuthentication.create(m.getId(), null, MerchantRole.ROLE_OPERATOR)
        );
    }
}

