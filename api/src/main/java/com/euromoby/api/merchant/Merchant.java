package com.euromoby.api.merchant;

import com.euromoby.api.common.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("merchants")
public class Merchant extends Entity {
    private String name;
    private String apiKey;
    private String description;
    private String env;
    private boolean active;
}
