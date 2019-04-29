package com.euromoby.api.db.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class UserProfile {
    @Id
    private UUID id;

    private String email;
    private String password;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
