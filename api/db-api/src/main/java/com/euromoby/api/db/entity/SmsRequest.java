package com.euromoby.api.db.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class SmsRequest {
    @Id
    private UUID id;

    private UUID userId;
    private String status;
    private Long msisdn;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
