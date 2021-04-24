package com.euromoby.api.user;

import com.euromoby.api.merchant.MerchantResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MerchantWithRoleResponse {
    MerchantResponse merchant;
    MerchantRole role;
}
