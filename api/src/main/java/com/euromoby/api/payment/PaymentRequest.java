package com.euromoby.api.payment;

import lombok.Data;

import java.util.UUID;

@Data
public class PaymentRequest {
    private String merchantReference;
    private UUID customerId;
}
