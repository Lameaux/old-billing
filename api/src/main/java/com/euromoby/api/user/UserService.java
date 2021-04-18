package com.euromoby.api.user;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;

@Service
public class UserService {
    private static final Function<User, UserResponse> TO_DTO = u -> {
        var dto = new UserResponse();
        dto.setId(u.getId());
        dto.setEmail(u.getEmail());
        dto.setMsisdn(u.getMsisdn());
        dto.setName(u.getName());
        dto.setActive(u.isActive());
        dto.setAdmin(u.isAdmin());
        dto.setEmailVerified(u.isEmailVerified());
        dto.setMsisdnVerified(u.isMsisdnVerified());
        dto.setCreatedAt(u.getCreatedAt());
        dto.setUpdatedAt(u.getUpdatedAt());
        return dto;
    };

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    Flux<UserResponse> getAllUsers() {
        return userRepository.findAll().map(TO_DTO);
    }

    Mono<UserResponse> getUser(UUID id) {
        return userRepository.findById(id).map(TO_DTO);
    }

    Mono<UserResponse> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(TO_DTO);
    }

    Mono<UserResponse> createUser(Mono<UserRequest> userRequestMono) {
        return userRequestMono.flatMap(userRequest -> {
            User u = new User();
            u.setEmail(userRequest.getEmail());
            u.setPasswordHash(BCrypt.hashpw(userRequest.getPassword(), BCrypt.gensalt()));
            u.setMsisdn(userRequest.getMsisdn());
            u.setName(userRequest.getName());
            u.setActive(true);
            u.setAdmin(false);
            u.setEmailVerified(false);
            u.setMsisdnVerified(false);
            return userRepository.save(u).map(TO_DTO);
        });
    }
}
