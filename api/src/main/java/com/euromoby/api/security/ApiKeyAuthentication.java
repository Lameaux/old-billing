package com.euromoby.api.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;

public class ApiKeyAuthentication extends AbstractAuthenticationToken {
    private String apiKey;
    private String merchantName;

    ApiKeyAuthentication(String apiKey, String merchantName) {
        super(Collections.emptyList());

        this.apiKey = apiKey;
        this.merchantName = merchantName;
    }

    @Override
    public Object getCredentials() {
        return apiKey;
    }

    @Override
    public Object getPrincipal() {
        return merchantName;
    }
}
