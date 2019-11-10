package com.euromoby.api.user.rest;

import com.euromoby.api.user.model.User;
import com.euromoby.api.user.repo.UserRepository;
import com.euromoby.api.user.rest.dto.UserRequest;
import com.euromoby.api.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest
@Import({EndpointsConfiguration.class, UserHandler.class, UserService.class})
class UserEndpointsTest {
    private final WebTestClient webTestClient;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    UserEndpointsTest(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    @Test
    void getAll() {
        Mockito.when(userRepository.findAll()).thenReturn(Flux.just(
                new User("1", "user1", null),
                new User("2", "user2", null)
        ));

        webTestClient.get()
                .uri("/users")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo("1")
                .jsonPath("$.[0].email").isEqualTo("user1")
                .jsonPath("$.[1].id").isEqualTo("2")
                .jsonPath("$.[1].email").isEqualTo("user2");

    }

    @Test
    void save() {
        User user = new User("1", "user1", "password1");
        UserRequest userRequest = new UserRequest("user1", "password1");

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(Mono.just(user));

        webTestClient.post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(userRequest), UserRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);

    }
}
