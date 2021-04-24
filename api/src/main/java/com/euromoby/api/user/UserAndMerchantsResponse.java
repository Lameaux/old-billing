package com.euromoby.api.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
class UserAndMerchantsResponse {
    private UserResponse user;
    private List<MerchantWithRoleResponse> merchants;
}
