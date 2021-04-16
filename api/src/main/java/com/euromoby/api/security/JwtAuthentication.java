package com.euromoby.api.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;

public class JwtAuthentication extends AbstractAuthenticationToken {
    private String jwt;
    private String merchant;

    JwtAuthentication(String jwt, String merchant) {
        super(Collections.emptyList());

        this.jwt = jwt;
        this.merchant = merchant;
    }

    @Override
    public Object getCredentials() {
        return jwt;
    }

    @Override
    public Object getPrincipal() {
        return merchant;
    }
}
