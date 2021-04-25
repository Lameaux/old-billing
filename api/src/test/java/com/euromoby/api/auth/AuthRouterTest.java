package com.euromoby.api.auth;

import com.euromoby.api.common.ErrorCode;
import com.euromoby.api.common.ErrorResponse;
import com.euromoby.api.common.RouterTest;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.util.UUID;

class AuthRouterTest extends RouterTest {
    private final String API_ROOT = "/api/v1/auth";

    @Test
    void loginAsUnknownUser() {
        var loginRequest = newLoginRequest();

        webTestClient.post().uri(API_ROOT + "/login").body(Mono.just(loginRequest), LoginRequest.class).exchange()
                .expectStatus().isUnauthorized()
                .expectBody(ErrorResponse.class)
                .isEqualTo(ErrorResponse.of(ErrorCode.INVALID_CREDENTIALS, AuthHandler.EMAIL_PASSWORD));
    }

    @Test
    void loginAsKnownUser() {
        var loginRequest = newLoginRequest();
        loginRequest.setEmail(USER_EMAIL);
        loginRequest.setPassword("user");

        webTestClient.post().uri(API_ROOT + "/login").body(Mono.just(loginRequest), LoginRequest.class).exchange()
                .expectStatus().isOk()
                .expectBody(AuthResponse.class).value(AuthResponse::getToken, IsNull.notNullValue());
    }

    @Test
    void registerUserWithoutRecaptcha() {
        var registerRequest = newRegisterRequest();
        registerRequest.setRecaptcha(null);

        webTestClient.post().uri(API_ROOT + "/register").body(Mono.just(registerRequest), RegisterRequest.class).exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .isEqualTo(ErrorResponse.of(ErrorCode.INVALID_BODY_PARAM, AuthHandler.PARAM_RECAPTCHA));
    }

    @Test
    void registerUserWithInvalidRecaptcha() {
        var registerRequest = newRegisterRequest();
        registerRequest.setRecaptcha(UUID.randomUUID().toString());

        webTestClient.post().uri(API_ROOT + "/register")
                .body(Mono.just(registerRequest), RegisterRequest.class).exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .isEqualTo(ErrorResponse.of(ErrorCode.INVALID_BODY_PARAM, AuthHandler.PARAM_RECAPTCHA));
    }

    @Test
    void registerUserWithValidRecaptcha() {
        var registerRequest = newRegisterRequest();

        webTestClient.post().uri(API_ROOT + "/register").body(Mono.just(registerRequest), RegisterRequest.class).exchange()
                .expectStatus().isOk()
                .expectBody(AuthResponse.class).value(AuthResponse::getToken, IsNull.notNullValue());
    }

    @Test
    void registerDuplicateUser() {
        var registerRequest = newRegisterRequest();
        registerRequest.setEmail(getJUnitUser().getEmail());

        webTestClient.post().uri(API_ROOT + "/register").body(Mono.just(registerRequest), RegisterRequest.class).exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody(ErrorResponse.class)
                .isEqualTo(ErrorResponse.of(ErrorCode.DUPLICATE_VALUE, AuthHandler.PARAM_EMAIL));
    }

    private LoginRequest newLoginRequest() {
        var loginRequest = new LoginRequest();
        loginRequest.setEmail(UUID.randomUUID() + "@euromoby.com");
        loginRequest.setPassword(UUID.randomUUID().toString());
        return loginRequest;
    }

    private RegisterRequest newRegisterRequest() {
        var registerRequest = new RegisterRequest();
        registerRequest.setEmail(UUID.randomUUID() + "@euromoby.com");
        registerRequest.setPassword(UUID.randomUUID().toString());
        registerRequest.setName("Donald Trump");
        registerRequest.setMsisdn("+123456789");
        registerRequest.setRecaptcha("42");
        return registerRequest;
    }
}
