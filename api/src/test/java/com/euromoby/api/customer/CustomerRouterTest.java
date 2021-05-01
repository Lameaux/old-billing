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
        return createNewCustomer(null, null, null, null);
    }

    CustomerResponse createNewCustomer(String merchantReference, String email, String msisdn, String name) {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setMerchantReference(merchantReference);
        customerRequest.setEmail(email);
        customerRequest.setMsisdn(msisdn);
        customerRequest.setName(name);
        return customerService.createCustomer(
                getJUnitMerchantId(),
                Mono.just(customerRequest)
        ).block();
    }

    @Test
    void listCustomersAsAnonymous() {
        webTestClient.get().uri(API_ROOT).exchange().expectStatus().isUnauthorized();
    }

    @Test
    void listCustomers() {
        CustomerResponse newCustomer = createNewCustomer();

        authorizedMerchantGet(API_ROOT).exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerResponse.class).contains(newCustomer);
    }

    @Test
    void getCustomerAsAnonymous() {
        webTestClient.get().uri(API_ROOT + "/{id}", UUID.randomUUID()).exchange().expectStatus().isUnauthorized();
    }

    @Test
    void getCustomerNotFound() {
        authorizedMerchantGet(API_ROOT + "/{id}", UUID.randomUUID()).exchange().expectStatus().isNotFound();
    }

    @Test
    void getCustomer() {
        CustomerResponse newCustomer = createNewCustomer();

        authorizedMerchantGet(API_ROOT + "/{id}", newCustomer.getId()).exchange()
                .expectStatus().isOk()
                .expectBody(CustomerResponse.class).isEqualTo(newCustomer);
    }

    @Test
    void findCustomersAsAnonymous() {
        webTestClient.get().uri(API_ROOT + "/find_by", UUID.randomUUID()).exchange().expectStatus().isUnauthorized();
    }

    @Test
    void findCustomersNotFound() {
        authorizedMerchantGet(API_ROOT + "/find_by?merchant_reference={merchant_reference}", UUID.randomUUID()).exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerResponse.class).hasSize(0);
    }

    @Test
    void findCustomersByMerchantReference() {
        CustomerResponse newCustomer = createNewCustomer(UUID.randomUUID().toString(), null, null, null);

        authorizedMerchantGet(API_ROOT + "/find_by?merchant_reference={merchant_reference}", newCustomer.getMerchantReference()).exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerResponse.class).hasSize(1).contains(newCustomer);
    }

    @Test
    void findCustomersByEmail() {
        CustomerResponse newCustomer = createNewCustomer(null, "customer@euromoby.com", null, null);

        authorizedMerchantGet(API_ROOT + "/find_by?email={email}", newCustomer.getEmail()).exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerResponse.class).hasSize(1).contains(newCustomer);
    }

    @Test
    void findCustomersByMsisdn() {
        CustomerResponse newCustomer = createNewCustomer(null, null, "+100500", null);

        authorizedMerchantGet(API_ROOT + "/find_by?msisdn={msisdn}", newCustomer.getMsisdn()).exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerResponse.class).hasSize(1).contains(newCustomer);
    }

    @Test
    void findCustomersByName() {
        CustomerResponse newCustomer = createNewCustomer(null, null, null, "James Bond");

        authorizedMerchantGet(API_ROOT + "/find_by?name={name}", newCustomer.getName()).exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerResponse.class).hasSize(1).contains(newCustomer);
    }

    @Test
    void createCustomerAsAnonymous() {
        webTestClient.post().uri(API_ROOT).exchange().expectStatus().isUnauthorized();
    }

    @Test
    void createCustomer() {
        var customerRequest = new CustomerRequest();
        customerRequest.setMerchantReference(UUID.randomUUID().toString());

        authorizedMerchantPost(API_ROOT).body(Mono.just(customerRequest), CustomerRequest.class).exchange()
                .expectStatus().isCreated()
                .expectBody(CustomerResponse.class)
                .value(CustomerResponse::getMerchantReference, equalTo(customerRequest.getMerchantReference()));
    }
}
