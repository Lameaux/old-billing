package com.euromoby.api.merchant;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MerchantResponse {
    private UUID id;
    private String name;
    private String apiKey;
    private String description;
    private MerchantEnv env;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
