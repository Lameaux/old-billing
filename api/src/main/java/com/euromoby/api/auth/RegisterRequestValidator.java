package com.euromoby.api.auth;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Objects;
import java.util.Set;

public class RegisterRequestValidator {
    public static String validate(RegisterRequest registerRequest) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);

        for (ConstraintViolation<RegisterRequest> violation : violations) {
            return violation.getPropertyPath().toString();
        }

        // FIXME
        if (!Objects.equals(registerRequest.getRecaptcha(), "42")) {
            return "recaptcha";
        }

        return null;
    }
}
