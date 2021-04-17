package com.euromoby.api.payment;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PaymentResponse {
    private UUID id;
    private UUID customerId;
    private String merchantReference;
    private PaymentState state;
    private String description;
    private String currency;
    private BigDecimal netAmount;
    private BigDecimal vatAmount;
    private BigDecimal vatRate;
    private BigDecimal totalAmount;
    private UUID instrumentId;
    private String providerReference;
    private String callbackUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
