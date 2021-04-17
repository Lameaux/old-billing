package com.euromoby.api.payment;

import com.euromoby.api.common.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@Table("payments")
public class Payment extends Entity {
    private UUID merchantId;
    private UUID customerId;
    private PaymentState state;
    private String merchantReference;
    private String description;
    private String currency;
    private BigDecimal netAmount;
    private BigDecimal vatAmount;
    private BigDecimal vatRate;
    private BigDecimal totalAmount;
    private UUID instrumentId;
    private String providerReference;
    private String callbackUrl;
}
