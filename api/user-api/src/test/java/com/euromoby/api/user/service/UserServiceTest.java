package com.euromoby.api.user.service;

import com.euromoby.api.user.dto.UserDto;
import com.euromoby.api.user.model.User;
import com.euromoby.api.user.repo.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {
    private String user_email = UUID.randomUUID().toString() + "-test@euromoby.com";
    private String user_password = UUID.randomUUID().toString();

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Test
    public void returnsNewUser() {
        UserDto userDto = userService.create(user_email, user_password);
        assertNotNull(userDto);
        assertEquals(user_email, userDto.getEmail());
    }

    @Test
    public void createsNewUserInDatabase() {
        userService.create(user_email, user_password);
        User user = userRepository.findByEmail(user_email).get();
        assertEquals(user_email, user.getEmail());
    }

    @Test(expected = Exception.class)
    public void failsToCreateDuplicateUser() {
        userService.create(user_email, user_password);
        userService.create(user_email, user_password);
    }

    @Test
    public void findsExistingUser() {
        UserDto userDto = userService.create(user_email, user_password);
        assertTrue(userService.findById(userDto.getId()).isPresent());
        assertFalse(userService.findById(UUID.randomUUID()).isPresent());
    }

    @Test
    public void returnsAllUsers() {
        userService.create(user_email, user_password);
        assertFalse(userService.findAll().isEmpty());
        assertTrue(userService.findAll().stream().anyMatch(user -> user.getEmail().equals(user_email)));
    }
}
