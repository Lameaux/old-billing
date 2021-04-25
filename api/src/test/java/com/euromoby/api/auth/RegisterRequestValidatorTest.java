package com.euromoby.api.auth;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;

class RegisterRequestValidatorTest {
    @Test
    void validRegisterRequest() {
        var request = newRegisterRequest();
        assertThat(RegisterRequestValidator.validate(request), IsNull.nullValue());
    }

    @Test
    void invalidEmail() {
        var request = newRegisterRequest();
        request.setEmail(null);
        assertThat(RegisterRequestValidator.validate(request), IsEqual.equalTo("email"));
    }

    @Test
    void invalidPassword() {
        var request = newRegisterRequest();
        request.setPassword(null);
        assertThat(RegisterRequestValidator.validate(request), IsEqual.equalTo("password"));
    }

    @Test
    void invalidMsisdn() {
        var request = newRegisterRequest();
        request.setMsisdn("123");
        assertThat(RegisterRequestValidator.validate(request), IsEqual.equalTo("msisdn"));
    }

    @Test
    void invalidName() {
        var request = newRegisterRequest();
        request.setName(null);
        assertThat(RegisterRequestValidator.validate(request), IsEqual.equalTo("name"));
    }

    @Test
    void invalidRecaptcha() {
        var request = newRegisterRequest();
        request.setRecaptcha("43");
        assertThat(RegisterRequestValidator.validate(request), IsEqual.equalTo("recaptcha"));
    }

    private RegisterRequest newRegisterRequest() {
        var request = new RegisterRequest();
        request.setEmail("admin@euromoby.com");
        request.setPassword("password");
        request.setMsisdn("+420123456789");
        request.setName("Donald Trump");
        request.setRecaptcha("42");
        return request;
    }

}
