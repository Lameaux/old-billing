package com.euromoby.api.sms.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class SmsRequest {
    UUID id;
    SmsRequestStatus status;

    String msisdn;
    String message;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
