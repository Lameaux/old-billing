package com.euromoby.api.sms.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class SmsRequest {
    UUID id;
    SmsRequestStatus status;

    @NotNull Long msisdn;
    @NotNull String message;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
