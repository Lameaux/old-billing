package com.euromoby.api.auth_api.security;

import com.euromoby.api.auth_api.model.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class TokenAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtProvider jwtProvider;

    @Autowired
    public TokenAuthenticationManager(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String jwtToken = authentication.getCredentials().toString();

        try {
            User user = jwtProvider.parseToken(jwtToken);
            var userToken = new UserIdAuthenticationToken(
                    user.getId(),
                    user.getAuthorities()
            );
            return Mono.just(userToken);
        } catch (Exception e) {
            log.debug("Failed to parse JWT", e);
            return Mono.empty();
        }
    }
}
