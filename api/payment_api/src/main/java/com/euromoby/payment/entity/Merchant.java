package com.euromoby.payment.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Table("merchants")
public class Merchant {
    @Id
    private UUID id;
    private String env;
    private String secret;
    private String name;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
