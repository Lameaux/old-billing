package com.euromoby.api.customer;

import com.euromoby.api.common.RouterTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;

class CustomerRouterTest extends RouterTest {
    private final String API_ROOT = "/api/v1/customers";

    private CustomerService customerService;

    @Autowired
    CustomerRouterTest(CustomerService customerService) {
        this.customerService = customerService;
    }

    CustomerResponse createNewCustomer() {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setMerchantReference(UUID.randomUUID().toString());
        Mono<CustomerResponse> response = customerService.createCustomer(
                getJUnitMerchantId(),
                Mono.just(customerRequest)
        );
        return response.block();
    }

    @Test
    void testListCustomersUnauthorized() {
        webTestClient.get().uri(API_ROOT).exchange().expectStatus().isUnauthorized();
    }

    @Test
    void testListCustomers() {
        CustomerResponse newCustomer = createNewCustomer();

        authorizedMerchantGet(API_ROOT).exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerResponse.class).contains(newCustomer);
    }

    @Test
    void testGetCustomerUnauthorized() {
        webTestClient.get().uri(API_ROOT + "/{id}", UUID.randomUUID()).exchange().expectStatus().isUnauthorized();
    }

    @Test
    void testGetCustomerNotFound() {
        authorizedMerchantGet(API_ROOT + "/{id}", UUID.randomUUID()).exchange().expectStatus().isNotFound();
    }

    @Test
    void testGetCustomer() {
        CustomerResponse newCustomer = createNewCustomer();

        authorizedMerchantGet(API_ROOT + "/{id}", newCustomer.getId()).exchange()
                .expectStatus().isOk()
                .expectBody(CustomerResponse.class).isEqualTo(newCustomer);
    }

    @Test
    void testFindCustomerUnauthorized() {
        webTestClient.get().uri(API_ROOT + "/find_by", UUID.randomUUID()).exchange().expectStatus().isUnauthorized();
    }

    @Test
    void testFindCustomerBadRequest() {
        authorizedMerchantGet(API_ROOT + "/find_by", UUID.randomUUID()).exchange().expectStatus().isBadRequest();
    }

    @Test
    void testFindCustomerNotFound() {
        authorizedMerchantGet(API_ROOT + "/find_by?merchant_reference={merchant_reference}", UUID.randomUUID()).exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testFindCustomer() {
        CustomerResponse newCustomer = createNewCustomer();

        authorizedMerchantGet(API_ROOT + "/find_by?merchant_reference={merchant_reference}", newCustomer.getMerchantReference()).exchange()
                .expectStatus().isOk()
                .expectBody(CustomerResponse.class).isEqualTo(newCustomer);
    }

    @Test
    void testCreateCustomerUnauthorized() {
        webTestClient.post().uri(API_ROOT).exchange().expectStatus().isUnauthorized();
    }

    @Test
    void testCreateCustomer() {
        var customerRequest = new CustomerRequest();
        customerRequest.setMerchantReference(UUID.randomUUID().toString());

        authorizedMerchantPost(API_ROOT).body(Mono.just(customerRequest), CustomerRequest.class).exchange()
                .expectStatus().isCreated()
                .expectBody(CustomerResponse.class)
                .value(CustomerResponse::getMerchantReference, equalTo(customerRequest.getMerchantReference()));
    }
}
