package com.euromoby.api.user.dto;

import com.euromoby.api.user.model.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class UserDto {
    UUID id;
    String email;
    boolean active;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public static UserDto fromUser(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
