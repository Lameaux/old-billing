package com.euromoby.api.user_api.rest.security;

import com.euromoby.api.user_api.model.Role;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling()
                .authenticationEntryPoint(
                        (req, res, e) -> res.setStatus(HttpStatus.UNAUTHORIZED.value())
                )
                .accessDeniedHandler(
                        (req, res, e) -> res.setStatus(HttpStatus.FORBIDDEN.value())
                )
                .and()
                .csrf().disable()
                .formLogin().disable()
                .authorizeRequests()
                .antMatchers("/v1/users/**").hasAuthority(Role.ADMIN.name())
                .and()
                .httpBasic();
    }
}
