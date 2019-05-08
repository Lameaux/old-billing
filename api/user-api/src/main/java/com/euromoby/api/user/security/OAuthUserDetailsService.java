package com.euromoby.api.user.security;

import com.euromoby.api.user.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class OAuthUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    org.springframework.security.authentication.dao.DaoAuthenticationProvider a;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).map(OAuthUserDetails::new).orElseThrow(
                () -> new UsernameNotFoundException("User " + email + " not found")
        );
    }
}
