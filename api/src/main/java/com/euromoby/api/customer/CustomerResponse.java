package com.euromoby.api.customer;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CustomerResponse {
    private String id;
    private String merchantReference;
    private String email;
    private String msisdn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
