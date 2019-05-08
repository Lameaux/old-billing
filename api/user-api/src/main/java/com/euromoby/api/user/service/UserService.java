package com.euromoby.api.user.service;

import com.euromoby.api.user.dto.UserDto;
import com.euromoby.api.user.model.User;
import com.euromoby.api.user.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDto create(String email, String password) {
        User newUser = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .password(passwordEncoder.encode(password))
                .active(true)
                .internal(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return UserDto.fromUser(repository.save(newUser));
    }

    public Optional<UserDto> findById(UUID id) {
        return repository.findById(id).map(UserDto::fromUser);
    }

    public List<UserDto> findAll() {
        return repository.findAll().stream().map(UserDto::fromUser).collect(Collectors.toList());
    }
}
