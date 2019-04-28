package com.euromoby.api.sms.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class ErrorDetails {
    private LocalDateTime timestamp;
    private String message;
    private String details;
}
