package com.euromoby.api.user;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String email;
    private String msisdn;
    private String name;
    private boolean active;
    private boolean admin;
    private boolean emailVerified;
    private boolean msisdnVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
