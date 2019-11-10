package com.euromoby.api.user.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    private String email;
    private String password;

    public boolean isValid() {
        return !StringUtils.isEmpty(email) && !StringUtils.isEmpty(password);
    }
}
