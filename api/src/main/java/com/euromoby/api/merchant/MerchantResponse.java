package com.euromoby.api.merchant;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
class MerchantResponse {
    private UUID id;
    private String name;
    private String apiKey;
    private String description;
    private String env;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
