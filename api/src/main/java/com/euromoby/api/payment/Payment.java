package com.euromoby.api.payment;

import com.euromoby.api.common.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@NoArgsConstructor
@Table("payments")
public class Payment extends Entity {
    public static final String STATE_NEW = "new";

    private UUID merchantId;
    private UUID customerId;
    private String merchantReference;
    private String state;
}
