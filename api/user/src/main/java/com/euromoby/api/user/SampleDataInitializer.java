package com.euromoby.api.user;

import com.euromoby.api.user.model.Role;
import com.euromoby.api.user.model.User;
import com.euromoby.api.user.repo.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Log4j2
@Component
public class SampleDataInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SampleDataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        var newUser = new User(
                null,
                "user1@gmail.com",
                passwordEncoder.encode("password1"),
                true,
                Role.ROLE_USER
        );

        userRepository.deleteAll()
                .thenMany(Flux.just(newUser).flatMap(userRepository::save))
                .thenMany(userRepository.findAll())
                .subscribe(
                        user -> log.info("Saving {}", user.toString())
                );
    }
}
