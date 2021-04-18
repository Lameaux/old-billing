package com.euromoby.api.security;

import com.euromoby.api.user.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public class AuthenticationUtil {
    public static boolean isAdmin(Authentication authentication) {
        Set<UserRole> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(UserRole::valueOf)
                .collect(Collectors.toSet());

        return roles.contains(UserRole.ROLE_ADMIN);
    }
}
