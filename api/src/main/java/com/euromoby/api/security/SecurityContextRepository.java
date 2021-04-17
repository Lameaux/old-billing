package com.euromoby.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {


    private AuthenticationManager authenticationManager;

    @Autowired
    public SecurityContextRepository(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Mono<Void> save(ServerWebExchange swe, SecurityContext sc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange swe) {
        ServerHttpRequest request = swe.getRequest();

        String merchantName = request.getHeaders().getFirst(SecurityConstants.HEADER_MERCHANT);

        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            var jwt = authorization.substring(7);
            Authentication auth = new JwtAuthentication(jwt, merchantName);
            return this.authenticationManager.authenticate(auth).map(SecurityContextImpl::new);
        }

        String apiKey = request.getHeaders().getFirst(SecurityConstants.HEADER_API_KEY);

        if (StringUtils.hasText(merchantName) && StringUtils.hasText(apiKey)) {
            Authentication auth = new ApiKeyAuthentication(apiKey, merchantName);
            return this.authenticationManager.authenticate(auth).map(SecurityContextImpl::new);
        }

        return Mono.empty();
    }
}
