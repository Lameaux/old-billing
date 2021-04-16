package com.euromoby.api.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;

public class ApiKeyAuthentication extends AbstractAuthenticationToken {
    private String apiKey;
    private String merchant;

    ApiKeyAuthentication(String apiKey, String merchant) {
        super(Collections.emptyList());

        this.apiKey = apiKey;
        this.merchant = merchant;
    }

    @Override
    public Object getCredentials() {
        return apiKey;
    }

    @Override
    public Object getPrincipal() {
        return merchant;
    }
}
