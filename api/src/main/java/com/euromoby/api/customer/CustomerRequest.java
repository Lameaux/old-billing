package com.euromoby.api.customer;

import lombok.Data;

import java.util.UUID;

@Data
public class CustomerRequest {
    private String merchantReference;
    private String email;
    private String msisdn;
}
