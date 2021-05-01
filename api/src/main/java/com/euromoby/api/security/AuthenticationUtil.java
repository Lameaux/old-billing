package com.euromoby.api.security;

import com.euromoby.api.user.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class AuthenticationUtil {
    public static boolean isAdmin(Authentication authentication) {
        Set<UserRole> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(UserRole::valueOf)
                .collect(Collectors.toSet());

        return roles.contains(UserRole.ROLE_ADMIN);
    }

    public static Mono<UUID> getMerchantId(ServerRequest serverRequest) {
        return serverRequest.principal().map(principal -> {
            MerchantAuthentication merchantAuthentication = (MerchantAuthentication) principal;
            return (UUID) merchantAuthentication.getPrincipal();
        }).switchIfEmpty(Mono.error(new RuntimeException("Authentication failed")));
    }
}
