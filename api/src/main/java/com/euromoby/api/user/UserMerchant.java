package com.euromoby.api.user;

import com.euromoby.api.common.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@NoArgsConstructor
@Table("users_merchants")
public class UserMerchant extends Entity {
    UUID userId;
    UUID merchantId;
    MerchantRole role;
}
