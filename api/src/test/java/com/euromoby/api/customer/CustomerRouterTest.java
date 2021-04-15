package com.euromoby.api.customer;

import com.euromoby.api.common.RouterTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;

public class CustomerRouterTest extends RouterTest {
    private final String API_ROOT = "/api/v1/customers";

    private CustomerService customerService;

    @Autowired
    public CustomerRouterTest(CustomerService customerService) {
        this.customerService = customerService;
    }

    public CustomerResponse createNewCustomer() {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setMerchantReference(UUID.randomUUID().toString());
        Mono<CustomerResponse> response = customerService.createCustomer(
                junitMerchant.getId(),
                Mono.just(customerRequest)
        );
        return response.block();
    }

    @Test
    public void testListCustomersUnauthorized() {
        webTestClient.get().uri(API_ROOT).exchange().expectStatus().isUnauthorized();
    }

    @Test
    public void testListCustomers() {
        CustomerResponse newCustomer = createNewCustomer();

        authorizedGet(API_ROOT).exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerResponse.class).contains(newCustomer);
    }

    @Test
    public void testGetCustomerUnauthorized() {
        webTestClient.get().uri(API_ROOT + "/{id}", UUID.randomUUID()).exchange().expectStatus().isUnauthorized();
    }

    @Test
    public void testGetCustomerNotFound() {
        authorizedGet(API_ROOT + "/{id}", UUID.randomUUID()).exchange().expectStatus().isNotFound();
    }

    @Test
    public void testGetCustomer() {
        CustomerResponse newCustomer = createNewCustomer();

        authorizedGet(API_ROOT + "/{id}", newCustomer.getId()).exchange()
                .expectStatus().isOk()
                .expectBody(CustomerResponse.class).isEqualTo(newCustomer);
    }

    @Test
    public void testFindCustomerUnauthorized() {
        webTestClient.get().uri(API_ROOT + "/find_by", UUID.randomUUID()).exchange().expectStatus().isUnauthorized();
    }

    @Test
    public void testFindCustomerBadRequst() {
        authorizedGet(API_ROOT + "/find_by", UUID.randomUUID()).exchange().expectStatus().isBadRequest();
    }

    @Test
    public void testFindCustomerNotFound() {
        authorizedGet(API_ROOT + "/find_by?merchant_reference={merchant_reference}", UUID.randomUUID()).exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void testFindCustomer() {
        CustomerResponse newCustomer = createNewCustomer();

        authorizedGet(API_ROOT + "/find_by?merchant_reference={merchant_reference}", newCustomer.getMerchantReference()).exchange()
                .expectStatus().isOk()
                .expectBody(CustomerResponse.class).isEqualTo(newCustomer);
    }

    @Test
    public void testCreateCustomerUnauthorized() {
        webTestClient.post().uri(API_ROOT).exchange().expectStatus().isUnauthorized();
    }

    @Test
    public void testCreateCustomer() {
        var customerRequest = new CustomerRequest();
        customerRequest.setMerchantReference(UUID.randomUUID().toString());

        authorizedPost(API_ROOT).body(Mono.just(customerRequest), CustomerRequest.class).exchange()
                .expectStatus().isCreated()
                .expectBody(CustomerResponse.class)
                .value(CustomerResponse::getMerchantReference, equalTo(customerRequest.getMerchantReference()));
    }
}
