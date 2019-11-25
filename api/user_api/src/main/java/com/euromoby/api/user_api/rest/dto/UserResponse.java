package com.euromoby.api.user_api.rest.dto;

import com.euromoby.api.user_api.model.Role;
import com.euromoby.api.user_api.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String id;
    private String username;
    private boolean enabled;
    private Role role;

    public static UserResponse fromUser(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEnabled(user.isEnabled());
        userResponse.setRole(user.getRole());
        return userResponse;
    }
}
