package com.euromoby.api.payment;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PaymentResponse {
    private UUID id;
    private String merchantReference;
    private UUID customerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
