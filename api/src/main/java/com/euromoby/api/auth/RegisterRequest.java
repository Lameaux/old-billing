package com.euromoby.api.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @Email
    @NotEmpty
    private String email;

    @NotEmpty
    @Size(min=8)
    private String password;

    @Pattern(regexp = "\\+\\d{8,15}")
    @NotEmpty
    private String msisdn;

    @NotBlank
    private String name;

    @NotEmpty
    private String recaptcha;
}
