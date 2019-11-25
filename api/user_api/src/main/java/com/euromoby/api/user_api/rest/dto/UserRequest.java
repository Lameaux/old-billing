package com.euromoby.api.user_api.rest.dto;

import com.euromoby.api.user_api.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String username;
    private String password;
    private Boolean enabled;
    private Role role;
}
