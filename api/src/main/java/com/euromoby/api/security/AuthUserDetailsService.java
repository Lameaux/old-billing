package com.euromoby.api.security;

import com.euromoby.api.user.UserRole;
import com.euromoby.api.user.User;
import com.euromoby.api.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.function.Function;

// https://ard333.medium.com/authentication-and-authorization-using-jwt-on-spring-webflux-29b81f813e78
// https://www.baeldung.com/spring-security-method-security

@Service
public class AuthUserDetailsService {
    private static final Function<User, AuthUserDetails> TO_DTO = u -> {
        var dto = new AuthUserDetails();
        dto.setUsername(u.getEmail());
        dto.setPassword(u.getPasswordHash());
        dto.setEnabled(u.isActive());

        var roles = new ArrayList<UserRole>();
        roles.add(UserRole.ROLE_USER);
        if (u.isAdmin()) {
            roles.add(UserRole.ROLE_ADMIN);
        }
        dto.setRoles(roles);

        return dto;
    };

    private final UserRepository userRepository;

    @Autowired
    public AuthUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<AuthUserDetails> findByEmail(String email) {
        return userRepository.findByEmail(email).map(TO_DTO);
    }
}
