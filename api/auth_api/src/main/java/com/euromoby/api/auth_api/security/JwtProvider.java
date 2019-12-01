package com.euromoby.api.auth_api.security;

import com.euromoby.api.auth_api.model.Role;
import com.euromoby.api.auth_api.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Component
public class JwtProvider {

    private static final long TOKEN_TIME_TO_LIVE = TimeUnit.HOURS.toMillis(1);
    private static final String CLAIM_ROLE = "role";

    private final SecretKey key;

    @Autowired
    public JwtProvider(@Value("${jwt.secret}") String secret) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(User user) {
        var claims = new HashMap<String, Object>();
        claims.put(CLAIM_ROLE, user.getRole());

        var validFrom = System.currentTimeMillis();
        var expiresAt = validFrom + TOKEN_TIME_TO_LIVE;

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getId())
                .setIssuedAt(new Date(validFrom))
                .setExpiration(new Date(expiresAt))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
    }

    public User parseToken(String token) {
        var claims = getClaimsFromToken(token);
        var user = new User();
        user.setId(claims.getSubject());

        String roleName = (String) claims.getOrDefault(CLAIM_ROLE, Role.USER.name());
        user.setRole(Role.valueOf(roleName));

        return user;
    }
}
