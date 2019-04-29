package com.euromoby.api.user.rest;

import com.euromoby.api.user.dto.UserProfile;
import com.euromoby.api.user.service.UserProfileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest
public class UserProfileControllerTest {
    private static final String EMAIL = "test@euromoby.com";
    private static final String PASSWORD = UUID.randomUUID().toString();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserProfileService userProfileService;

    private UserProfile savedUserProfile = UserProfile.builder()
            .id(UUID.randomUUID())
            .email(EMAIL)
            .active(true)
            .build();

    @Test
    public void returnsUserProfile() throws Exception {
        Mockito.when(userProfileService.findById(savedUserProfile.getId())).thenReturn(Optional.of(savedUserProfile));

        mockMvc.perform(get(UserProfileController.BASE_URL + "/{id}", savedUserProfile.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id").value(savedUserProfile.getId().toString()))
                .andExpect(jsonPath("$.email").value(savedUserProfile.getEmail()))
                .andExpect(jsonPath("$.active").value(savedUserProfile.isActive()));
    }

    @Test
    public void createsUserProfile() throws Exception {
        Mockito.when(userProfileService.create(EMAIL, PASSWORD)).thenReturn(savedUserProfile);

        mockMvc.perform(post(UserProfileController.BASE_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", EMAIL)
                .param("password", PASSWORD))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id").value(savedUserProfile.getId().toString()))
                .andExpect(jsonPath("$.email").value(savedUserProfile.getEmail()))
                .andExpect(jsonPath("$.active").value(savedUserProfile.isActive()));
    }

    @Test
    public void missingContentType() throws Exception {
        mockMvc.perform(post(UserProfileController.BASE_URL)).andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void missingParameters() throws Exception {
        mockMvc.perform(post(UserProfileController.BASE_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void invalidEmail() throws Exception {
        mockMvc.perform(post(UserProfileController.BASE_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", UUID.randomUUID().toString())
                .param("password", PASSWORD))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void invalidPassword() throws Exception {
        mockMvc.perform(post(UserProfileController.BASE_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", EMAIL)
                .param("password", ""))
                .andExpect(status().isBadRequest());
    }
}
