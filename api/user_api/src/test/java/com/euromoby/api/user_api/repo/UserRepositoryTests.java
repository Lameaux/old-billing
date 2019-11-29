package com.euromoby.api.user_api.repo;

import com.euromoby.api.user_api.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;


@SpringBootTest
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithMockUser
    public void findByUsername() {
        String userName = UUID.randomUUID().toString();

        User user = new User();
        user.setUsername(userName);
        User savedUser = userRepository.save(user);

        User foundUser = userRepository.findByUsername(userName).orElseThrow();

        assertThat(savedUser.getId(), is(equalTo(foundUser.getId())));
    }

}
