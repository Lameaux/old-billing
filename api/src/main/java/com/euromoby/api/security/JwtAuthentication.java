package com.euromoby.api.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;

public class JwtAuthentication extends AbstractAuthenticationToken {
    private String jwt;
    private String merchantName;

    JwtAuthentication(String jwt, String merchantName) {
        super(Collections.emptyList());

        this.jwt = jwt;
        this.merchantName = merchantName;
    }

    @Override
    public Object getCredentials() {
        return jwt;
    }

    @Override
    public Object getPrincipal() {
        return merchantName;
    }
}
