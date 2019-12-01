package com.euromoby.api.user_api.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithAnonymousUser
    public void optionsForAnonymous() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.options("/v1/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void listForAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/users")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void listForUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/users")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}
