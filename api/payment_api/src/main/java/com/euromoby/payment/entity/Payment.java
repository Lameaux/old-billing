package com.euromoby.payment.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("payments")
public class Payment {
    @Id
    private String id;
    private String merchantId;
}
