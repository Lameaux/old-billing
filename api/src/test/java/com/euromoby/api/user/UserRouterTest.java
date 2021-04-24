package com.euromoby.api.user;

import com.euromoby.api.common.ErrorCode;
import com.euromoby.api.common.ErrorResponse;
import com.euromoby.api.common.RouterTest;
import com.euromoby.api.merchant.MerchantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;

class UserRouterTest extends RouterTest {
    private final String API_ROOT = "/api/v1/users";

    @Autowired
    private UserService userService;
    @Autowired
    private MerchantService merchantService;

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
        var usersAndMerchants = new UserAndMerchantsResponse();
        usersAndMerchants.setUser(UserService.TO_DTO.apply(getJUnitUser()));
        var response = new MerchantWithRoleResponse();
        response.setMerchant(merchantService.getMerchant(getJUnitMerchantId()).block());
        response.setRole(MerchantRole.ROLE_OWNER);
        usersAndMerchants.setMerchants(List.of(response));

        authorizedAdminGet(API_ROOT + "/{id}", getJUnitUser().getId()).exchange()
                .expectStatus().isOk()
                .expectBody(UserAndMerchantsResponse.class)
                .isEqualTo(usersAndMerchants);
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
    void getAuthenticatedUserAsAnonymous() {
        webTestClient.get().uri(API_ROOT + "/me").exchange().expectStatus().isUnauthorized();
    }

    @Test
    void getAuthenticatedUserAsUser() {
        var usersAndMerchants = new UserAndMerchantsResponse();
        usersAndMerchants.setUser(UserService.TO_DTO.apply(getJUnitUser()));
        var response = new MerchantWithRoleResponse();
        response.setMerchant(merchantService.getMerchant(getJUnitMerchantId()).block());
        response.setRole(MerchantRole.ROLE_OWNER);
        usersAndMerchants.setMerchants(List.of(response));

        authorizedUserGet(API_ROOT + "/me").exchange()
                .expectStatus().isOk()
                .expectBody(UserAndMerchantsResponse.class)
                .isEqualTo(usersAndMerchants);
    }

    @Test
    void findUsersAsAnonymous() {
        webTestClient.get().uri(API_ROOT + "/find_by", UUID.randomUUID()).exchange().expectStatus().isUnauthorized();
    }

    @Test
    void findUsersAsUser() {
        authorizedUserGet(API_ROOT + "/find_by?email={email}", UUID.randomUUID()).exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void findUsersByEmail() {
        var newUser = createNewUser();

        authorizedAdminGet(API_ROOT + "/find_by?email={email}", newUser.getEmail()).exchange()
                .expectStatus().isOk().expectBodyList(UserResponse.class).contains(newUser);
    }

    @Test
    void findUsersByMsisdn() {
        var newUser = createNewUser();

        authorizedAdminGet(API_ROOT + "/find_by?msisdn={msisdn}", newUser.getMsisdn()).exchange()
                .expectStatus().isOk().expectBodyList(UserResponse.class).contains(newUser);
    }

    @Test
    void findUsersByName() {
        var newUser = createNewUser();

        authorizedAdminGet(API_ROOT + "/find_by?name={name}", newUser.getName()).exchange()
                .expectStatus().isOk().expectBodyList(UserResponse.class).contains(newUser);
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
