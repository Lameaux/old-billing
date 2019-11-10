package com.euromoby.api.user.service;

import com.euromoby.api.user.model.Role;
import com.euromoby.api.user.model.User;
import com.euromoby.api.user.repo.UserRepository;
import com.euromoby.api.user.rest.dto.UserRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Log4j2
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Flux<User> all() {
        return userRepository.findAll();
    }

    public Mono<User> get(String id) {
        return userRepository.findById(id);
    }

    public Mono<User> update(String id, UserRequest userRequest) {
        return userRepository.findById(id).map(user -> {
            if (userRequest.getEnabled() != null) {
                user.setEnabled(userRequest.getEnabled());
            }
            if (userRequest.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            }
            return user;
        }).flatMap(userRepository::save);
    }

    public Mono<User> delete(String id) {
        return userRepository.findById(id)
                .flatMap(user -> userRepository.deleteById(user.getId())
                        .thenReturn(user));
    }

    public Mono<User> create(UserRequest userRequest) {
        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setEnabled(userRequest.getEnabled());
        user.setRole(Role.ROLE_USER);
        return userRepository.save(user);
    }
}
