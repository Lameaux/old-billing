package com.euromoby.payment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentDto {
    private String id;
    private String merchantId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
