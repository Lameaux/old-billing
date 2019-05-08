package com.euromoby.api.user.rest;

import com.euromoby.api.user.dto.UserDto;
import com.euromoby.api.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest
public class UserControllerTest {
    private static final String EMAIL = "test@euromoby.com";
    private static final String PASSWORD = UUID.randomUUID().toString();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserDto savedUser = UserDto.builder()
            .id(UUID.randomUUID())
            .email(EMAIL)
            .active(true)
            .build();

    @Test
    public void returnsAllUsers() throws Exception {
        Mockito.when(userService.findAll()).thenReturn(Arrays.asList(savedUser));

        mockMvc.perform(get(UserController.BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.content[0].id").value(savedUser.getId().toString()))
                .andExpect(jsonPath("$.content[0].email").value(savedUser.getEmail()))
                .andExpect(jsonPath("$.content[0].active").value(savedUser.isActive()));
    }

    @Test
    public void returnsUsers() throws Exception {
        Mockito.when(userService.findById(savedUser.getId())).thenReturn(Optional.of(savedUser));

        mockMvc.perform(get(UserController.BASE_URL + "/{id}", savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id").value(savedUser.getId().toString()))
                .andExpect(jsonPath("$.email").value(savedUser.getEmail()))
                .andExpect(jsonPath("$.active").value(savedUser.isActive()));
    }

    @Test
    public void createsUsers() throws Exception {
        Mockito.when(userService.create(EMAIL, PASSWORD)).thenReturn(savedUser);

        mockMvc.perform(post(UserController.BASE_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", EMAIL)
                .param("password", PASSWORD))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id").value(savedUser.getId().toString()))
                .andExpect(jsonPath("$.email").value(savedUser.getEmail()))
                .andExpect(jsonPath("$.active").value(savedUser.isActive()));
    }

    @Test
    public void missingContentType() throws Exception {
        mockMvc.perform(post(UserController.BASE_URL)).andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void missingParameters() throws Exception {
        mockMvc.perform(post(UserController.BASE_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void invalidEmail() throws Exception {
        mockMvc.perform(post(UserController.BASE_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", UUID.randomUUID().toString())
                .param("password", PASSWORD))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void invalidPassword() throws Exception {
        mockMvc.perform(post(UserController.BASE_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", EMAIL)
                .param("password", ""))
                .andExpect(status().isBadRequest());
    }
}
