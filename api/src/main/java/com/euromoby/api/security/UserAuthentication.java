package com.euromoby.api.security;

import com.euromoby.api.user.UserRole;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class UserAuthentication extends AbstractAuthenticationToken {
    private UUID userId;

    UserAuthentication(UUID userId, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        setAuthenticated(true);

        this.userId = userId;
    }

    public static UserAuthentication create(UUID userId, UserRole... userRoles) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (UserRole userRole : userRoles) {
            authorities.add(new SimpleGrantedAuthority(userRole.name()));
        }

        return new UserAuthentication(userId, authorities);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }
}
