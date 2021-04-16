package com.euromoby.api.user;

import com.euromoby.api.payment.PaymentRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Service
public class UserService {
    private static final Function<User, UserResponse> TO_DTO = u -> {
        var dto = new UserResponse();
        dto.setId(u.getId());

        dto.setCreatedAt(u.getCreatedAt());
        dto.setUpdatedAt(u.getUpdatedAt());
        return dto;
    };

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<UserResponse> createUser(Mono<PaymentRequest> userRequestMono) {
        return userRequestMono.flatMap(userRequest -> {
            User p = new User();
            return userRepository.save(p).map(TO_DTO);
        });
    }
}
