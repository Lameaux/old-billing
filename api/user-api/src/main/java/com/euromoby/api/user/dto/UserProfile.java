package com.euromoby.api.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class UserProfile {
    UUID id;
    String email;
    boolean active;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
