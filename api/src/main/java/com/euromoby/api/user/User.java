package com.euromoby.api.user;

import com.euromoby.api.common.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("users")
public class User extends Entity {
    private String email;
    private String passwordHash;
    private String msisdn;
    private String name;
    private boolean active;
    private boolean admin;
    private boolean emailVerified;
    private boolean msisdnVerified;

}
