package com.euromoby.api.security;

import com.euromoby.api.user.MerchantRole;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class MerchantAuthentication extends AbstractAuthenticationToken {
    private UUID merchant;

    MerchantAuthentication(UUID merchant, UUID userId, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        setDetails(userId);
        setAuthenticated(true);

        this.merchant = merchant;
    }

    public static MerchantAuthentication create(UUID merchant, UUID userId, MerchantRole... merchantRoles) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (MerchantRole userRole : merchantRoles) {
            authorities.add(new SimpleGrantedAuthority(userRole.name()));
        }

        return new MerchantAuthentication(merchant, userId, authorities);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return merchant;
    }
}
