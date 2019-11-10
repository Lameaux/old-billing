package com.euromoby.api.user.service;

import com.euromoby.api.user.model.User;
import com.euromoby.api.user.repo.UserRepository;
import com.euromoby.api.user.rest.dto.UserRequest;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@Import(UserService.class)
class UserServiceTest {
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    UserServiceTest(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Test
    void save() {
        Mono<User> userMono = this.userService.create(new UserRequest("email", "password"));
        StepVerifier
                .create(userMono)
                .expectNextMatches(saved -> StringUtils.isNotBlank(saved.getId()))
                .verifyComplete();
    }

}
