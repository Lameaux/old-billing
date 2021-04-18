package com.euromoby.api.config;

import com.euromoby.api.security.AuthenticationManager;
import com.euromoby.api.security.SecurityContextRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurityConfig {
    private static final String[] SWAGGER_PATTERNS = new String[]{
            "/api-definitions/**",
            "/webjars/**",
            "/apidoc.html"
//            "/configuration/ui",
//            "/swagger-resources/**",
//            "/configuration/security",

    };
    private AuthenticationManager authenticationManager;
    private SecurityContextRepository securityContextRepository;

    @Autowired
    public WebSecurityConfig(AuthenticationManager authenticationManager, SecurityContextRepository securityContextRepository) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .exceptionHandling()
                .authenticationEntryPoint((swe, e) -> Mono.fromRunnable(() -> {
                    swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                })).accessDeniedHandler((swe, e) -> Mono.fromRunnable(() -> {
                    swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                })).and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers("/actuator/health").permitAll()
                .pathMatchers("/api/v1/auth/**").permitAll()
                .pathMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                .pathMatchers(SWAGGER_PATTERNS).permitAll()

                .anyExchange().authenticated()
                .and().build();
    }
}
