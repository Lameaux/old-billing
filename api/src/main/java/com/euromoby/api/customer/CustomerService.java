package com.euromoby.api.customer;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;

@Service
public class CustomerService {
    private static final Function<Customer, CustomerResponse> TO_DTO = c -> {
        var dto = new CustomerResponse();
        dto.setId(c.getId());
        dto.setMerchantReference(c.getMerchantReference());
        dto.setEmail(c.getEmail());
        dto.setMsisdn(c.getMsisdn());
        dto.setName(c.getName());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setUpdatedAt(c.getUpdatedAt());
        return dto;
    };
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Flux<CustomerResponse> getAllCustomers(UUID merchantId) {
        return customerRepository.findAllByMerchantId(merchantId).map(TO_DTO);
    }

    public Mono<CustomerResponse> getCustomer(UUID id, UUID merchantId) {
        return customerRepository.findByIdAndMerchantId(id, merchantId).map(TO_DTO);
    }

    public Mono<CustomerResponse> getCustomerByMerchantReference(UUID merchantId, String merchantReference) {
        return customerRepository.findByMerchantIdAndMerchantReference(merchantId, merchantReference).map(TO_DTO);
    }

    public Mono<CustomerResponse> createCustomer(UUID merchantId, Mono<CustomerRequest> customerRequestMono) {
        return customerRequestMono.flatMap(customerRequest -> {
            Customer c = new Customer();
            c.setMerchantId(merchantId);
            c.setMerchantReference(customerRequest.getMerchantReference());
            c.setEmail(customerRequest.getEmail());
            c.setMsisdn(customerRequest.getMsisdn());
            c.setName(customerRequest.getName());
            return customerRepository.save(c).map(TO_DTO);
        });
    }
}
