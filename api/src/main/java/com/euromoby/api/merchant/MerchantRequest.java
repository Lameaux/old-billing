package com.euromoby.api.merchant;

import lombok.Data;

@Data
class MerchantRequest {
    private String name;
    private String description;
    private String env;
}
