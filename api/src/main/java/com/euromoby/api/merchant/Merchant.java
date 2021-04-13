package com.euromoby.api.merchant;

import com.euromoby.api.common.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("merchants")
public class Merchant extends Entity {
    private String apiKey;
    private String env;
    private String name;
    private boolean active;
}
