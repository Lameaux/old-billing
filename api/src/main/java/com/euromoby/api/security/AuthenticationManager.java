package com.euromoby.api.security;

import com.euromoby.api.merchant.Merchant;
import com.euromoby.api.merchant.MerchantRepository;
import com.euromoby.api.user.*;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {
    private JWTUtil jwtUtil;
    private UserRepository userRepository;
    private UserMerchantRepository userMerchantRepository;
    private MerchantRepository merchantRepository;

    @Autowired
    public AuthenticationManager(JWTUtil jwtUtil, UserRepository userRepository, UserMerchantRepository userMerchantRepository, MerchantRepository merchantRepository) {
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
        String merchantId = authentication.getPrincipal().toString();
        String jwtToken = authentication.getCredentials().toString();

        if (!jwtUtil.validateToken(jwtToken)) {
            return Mono.empty();
        }

        UUID userId = jwtUtil.getUserIdFromToken(jwtToken);
        Mono<User> user = userRepository.findById(userId).filter(User::isActive);

        if (!StringUtils.hasText(merchantId)) {
            return user.map(
                    u -> UserAuthentication.create(userId, u.isAdmin() ? UserRole.ROLE_ADMIN : UserRole.ROLE_USER)
            );
        }

        return user.flatMap(u -> {
            UUID merchantUUID = UUID.fromString(merchantId);
            Mono<UserMerchant> userMerchant = userMerchantRepository.findByUserIdAndMerchantId(userId, merchantUUID);
            return userMerchant.map(um -> MerchantAuthentication.create(merchantUUID, userId, um.getRole()));
        });
    }

    private Mono<Authentication> apiKey(ApiKeyAuthentication authentication) {
        String merchantId = authentication.getPrincipal().toString();
        String apiKey = authentication.getCredentials().toString();

        UUID merchantUUID = UUID.fromString(merchantId);

        Mono<Merchant> merchant = merchantRepository.findById(merchantUUID)
                .filter(Merchant::isActive)
                .filter(m -> BCrypt.checkpw(apiKey, m.getApiKey()));

        return merchant.map(
                m -> MerchantAuthentication.create(merchantUUID, null, MerchantRole.ROLE_OWNER)
        );
    }
}

