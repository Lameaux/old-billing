package com.euromoby.api.user.rest.security;

import com.euromoby.api.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {
    public String generateToken(User user) {
        return "thetoken";
    }

    public User parseToken(String authToken) {
        return new User();
    }
}
