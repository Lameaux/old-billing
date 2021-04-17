package com.euromoby.api.payment;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PaymentRequest {
    private UUID customerId;
    private String merchantReference;
    private String description;
    private String currency;
    private BigDecimal netAmount;
    private BigDecimal vatAmount;
    private BigDecimal vatRate;
    private BigDecimal totalAmount;
    private String callbackUrl;
}
