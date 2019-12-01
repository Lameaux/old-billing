package com.euromoby.api.auth_api.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserIdAuthenticationToken extends AbstractAuthenticationToken {

    private final String principal;

    public UserIdAuthenticationToken(String userId, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = userId;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
