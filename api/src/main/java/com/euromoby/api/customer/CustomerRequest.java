package com.euromoby.api.customer;

import lombok.Data;

@Data
public class CustomerRequest {
    private String merchantReference;
    private String email;
    private String msisdn;
    private String name;
}
