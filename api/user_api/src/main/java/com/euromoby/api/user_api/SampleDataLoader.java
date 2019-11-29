package com.euromoby.api.user_api;

import com.euromoby.api.user_api.model.Role;
import com.euromoby.api.user_api.model.User;
import com.euromoby.api.user_api.repo.UserRepository;
import com.euromoby.api.user_api.rest.dto.UserRequest;
import com.euromoby.api.user_api.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@Profile("development")
public class SampleDataLoader implements ApplicationListener<ApplicationReadyEvent> {
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public SampleDataLoader(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        userRepository.deleteAll();

        User adminUser = userService.create(new UserRequest(
                "admin",
                "admin",
                false,
                Role.ADMIN
        ));

        log.info("Admin user created: {}.", adminUser.toString());
    }
}
