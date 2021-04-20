package com.euromoby.api.user;

import com.euromoby.api.common.ErrorCode;
import com.euromoby.api.common.ErrorResponse;
import com.euromoby.api.common.RouterTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;

class UserRouterTest extends RouterTest {
    private final String API_ROOT = "/api/v1/users";

    private UserService userService;

    @Autowired
    UserRouterTest(UserService userService) {
        this.userService = userService;
    }

    UserResponse createNewUser() {
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail(UUID.randomUUID() + "@euromoby.com");
        userRequest.setPassword(UUID.randomUUID().toString());
        userRequest.setName("New User");
        userRequest.setMsisdn("+420123456789");
        return userService.createUser(Mono.just(userRequest)).block();
    }

    @Test
    void listUsersAsAnonymous() {
        webTestClient.get().uri(API_ROOT).exchange().expectStatus().isUnauthorized();
    }

    @Test
    void listUsersAsUser() {
        authorizedUserGet(API_ROOT).exchange().expectStatus().isForbidden();
    }

    @Test
    void listUsersAsAdmin() {
        var newUser = createNewUser();

        authorizedAdminGet(API_ROOT).exchange()
                .expectStatus().isOk().expectBodyList(UserResponse.class).contains(newUser);
    }

    @Test
    void getUserAsAnonymous() {
        webTestClient.get().uri(API_ROOT + "/{id}", UUID.randomUUID()).exchange().expectStatus().isUnauthorized();
    }

    @Test
    void getUserAsAdmin() {
        var newUser = createNewUser();

        authorizedAdminGet(API_ROOT + "/{id}", newUser.getId()).exchange()
                .expectStatus().isOk().expectBody(UserResponse.class).isEqualTo(newUser);
    }

    @Test
    void getUserNotFound() {
        authorizedAdminGet(API_ROOT + "/{id}", UUID.randomUUID()).exchange().expectStatus().isNotFound();
    }

    @Test
    void getUserAsSameUser() {
        authorizedUserGet(API_ROOT + "/{id}", getJUnitUser().getId()).exchange().expectStatus().isOk();
    }

    @Test
    void getUserAsAnotherUser() {
        authorizedUserGet(API_ROOT + "/{id}", UUID.randomUUID()).exchange().expectStatus().isForbidden();
    }

    @Test
    void findUserAsAnonymous() {
        webTestClient.get().uri(API_ROOT + "/find_by", UUID.randomUUID()).exchange().expectStatus().isUnauthorized();
    }

    @Test
    void findUserBadRequest() {
        authorizedAdminGet(API_ROOT + "/find_by").exchange().expectStatus().isBadRequest();
    }

    @Test
    void findUserNotFound() {
        authorizedAdminGet(API_ROOT + "/find_by?email={email}", UUID.randomUUID()).exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void findUserAsUser() {
        authorizedUserGet(API_ROOT + "/find_by?email={email}", UUID.randomUUID()).exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void findUserAsAdmin() {
        var newUser = createNewUser();

        authorizedAdminGet(API_ROOT + "/find_by?email={email}", newUser.getEmail()).exchange()
                .expectStatus().isOk().expectBody(UserResponse.class).isEqualTo(newUser);
    }

    @Test
    void createUserAsAnonymous() {
        var userRequest = newUserRequest();

        webTestClient.post().uri(API_ROOT).body(Mono.just(userRequest), UserRequest.class).exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void createUserAsUser() {
        var userRequest = newUserRequest();

        authorizedUserPost(API_ROOT).body(Mono.just(userRequest), UserRequest.class).exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void createUserAsAdmin() {
        var userRequest = newUserRequest();

        authorizedAdminPost(API_ROOT).body(Mono.just(userRequest), UserRequest.class).exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponse.class).value(UserResponse::getEmail, equalTo(userRequest.getEmail()));
    }

    @Test
    void createDuplicateUser() {
        var userRequest = newUserRequest();
        userRequest.setEmail(getJUnitUser().getEmail());

        authorizedAdminPost(API_ROOT).body(Mono.just(userRequest), UserRequest.class).exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody(ErrorResponse.class)
                .isEqualTo(ErrorResponse.of(ErrorCode.DUPLICATE_VALUE, UserHandler.PARAM_EMAIL));
    }

    private UserRequest newUserRequest() {
        var userRequest = new UserRequest();
        userRequest.setEmail(UUID.randomUUID() + "@euromoby.com");
        userRequest.setPassword(UUID.randomUUID().toString());
        userRequest.setName("Donald Trump");
        userRequest.setMsisdn("+123456789");
        return userRequest;
    }
}