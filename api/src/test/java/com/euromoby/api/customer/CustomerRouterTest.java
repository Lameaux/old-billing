package com.euromoby.api.customer;

import com.euromoby.api.common.RouterTest;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class CustomerRouterTest extends RouterTest {
    private final String API_ROOT = "/api/v1/customers";

    @Test
    public void testListCustomersUnauthorized() {
        webTestClient.get().uri(API_ROOT).exchange().expectStatus().isUnauthorized();
    }

    @Test
    public void testListCustomers() {
        authorizedGet(API_ROOT).exchange().expectStatus().isOk();
    }

    @Test
    public void testGetCustomerUnauthorized() {
        webTestClient.get().uri(API_ROOT + "/{id}", UUID.randomUUID()).exchange().expectStatus().isUnauthorized();
    }

    @Test
    public void testGetCustomer() {
        authorizedGet(API_ROOT + "/{id}", UUID.randomUUID()).exchange().expectStatus().isNotFound();
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
    public void testFindCustomer() {
        authorizedGet(API_ROOT + "/find_by?merchant_reference={merchant_reference}", UUID.randomUUID()).exchange().expectStatus().isNotFound();
    }

    @Test
    public void testCreateCustomerUnauthorized() {
        webTestClient.post().uri(API_ROOT).exchange().expectStatus().isUnauthorized();
    }

    @Test
    public void testCreateCustomer() {
        var customerDto = new CustomerRequest();
        authorizedPost(API_ROOT).body(Mono.just(customerDto), CustomerRequest.class).exchange().expectStatus().isCreated();
    }


}
