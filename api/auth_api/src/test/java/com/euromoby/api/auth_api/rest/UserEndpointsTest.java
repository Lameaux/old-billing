package com.euromoby.api.auth_api.rest;

import com.euromoby.api.auth_api.model.Role;
import com.euromoby.api.auth_api.model.User;
import com.euromoby.api.auth_api.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest
@Import({EndpointsConfiguration.class, UserHandler.class})
class UserEndpointsTest {
    private final WebTestClient webTestClient;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    UserEndpointsTest(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    @Test
    void getMe() {
        Mockito.when(userRepository.findById(Mockito.anyString())).thenReturn(Mono.just(
                new User("1", "user1", null, true, Role.USER)
        ));

        webTestClient.get()
                .uri("/me")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo("1")
                .jsonPath("$.email").isEqualTo("user1");

    }
}
