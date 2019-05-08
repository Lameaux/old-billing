package com.euromoby.api.user.security;

import com.euromoby.api.user.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OAuthUserDetails implements UserDetails {
    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private List<GrantedAuthority> authorities = new ArrayList<>();
    private String password;
    private String username;
    private boolean active;

    public OAuthUserDetails(User user) {
        username = user.getEmail();
        password = user.getPassword();
        active = user.isActive();

        authorities.add(new SimpleGrantedAuthority(ROLE_USER));

        if (user.isInternal()) {
            authorities.add(new SimpleGrantedAuthority(ROLE_ADMIN));
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
