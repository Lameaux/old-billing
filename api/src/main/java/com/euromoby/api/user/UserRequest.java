package com.euromoby.api.user;

import lombok.Data;

import java.util.UUID;

@Data
public class UserRequest {
    private String email;
    private String password;
    private String msisdn;
    private String name;
}
