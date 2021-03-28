package com.euromoby.api.customer;

import com.euromoby.api.common.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@NoArgsConstructor
@Table("customers")
public class Customer extends Entity {
    private UUID merchantId;
    private String merchantReference;
    private String email;
    private String msisdn;
}
