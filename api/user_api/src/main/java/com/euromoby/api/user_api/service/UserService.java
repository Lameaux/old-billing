package com.euromoby.api.user_api.service;

import com.euromoby.api.user_api.model.User;
import com.euromoby.api.user_api.repo.UserRepository;
import com.euromoby.api.user_api.rest.dto.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User create(UserRequest userRequest) {
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setRole(userRequest.getRole());
        return userRepository.save(user);
    }

    public Optional<User> update(String id, UserRequest userRequest) {
        return userRepository.findById(id).map(user -> {
            if (userRequest.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            }
            if (userRequest.getEnabled() != null) {
                user.setEnabled(userRequest.getEnabled());
            }
            if (userRequest.getRole() != null) {
                user.setRole(userRequest.getRole());
            }
            return userRepository.save(user);
        });
    }

    public Optional<User> delete(String id) {
        return userRepository.findById(id).map(user -> {
            user.setEnabled(false);
            return userRepository.save(user);
        });
    }
}
