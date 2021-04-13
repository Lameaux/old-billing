package com.euromoby.api.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@NoArgsConstructor
@Table("user_permissions")
public class UserPermission {
    UUID userId;
    UUID merchantId;
    UserRole role;
}
