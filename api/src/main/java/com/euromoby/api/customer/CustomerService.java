package com.euromoby.api.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    Flux<CustomerResponse> getAllCustomers(UUID merchantId, String orderBy, String orderDirection, int page, int size) {
        return customerRepository.findAllByMerchantIdAndIdNotNull(
                merchantId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(orderDirection), orderBy))
        ).map(TO_DTO);
    }

    Flux<CustomerResponse> findCustomersByFilter(UUID merchantId, String merchantReference, String email, String msisdn, String name, int page, int size) {
        var pageRequest = PageRequest.of(page, size);

        return customerRepository.findAllByMerchantIdAndFilter(
                merchantId,
                merchantReference,
                email,
                msisdn,
                name,
                pageRequest.getPageSize(),
                pageRequest.getOffset()
        ).map(TO_DTO);
    }

    Mono<CustomerResponse> getCustomer(UUID id, UUID merchantId) {
        return customerRepository.findByIdAndMerchantId(id, merchantId).map(TO_DTO);
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
