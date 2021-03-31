package com.euromoby.api.payment;

import com.euromoby.api.common.RouterTest;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class PaymentRouterTest extends RouterTest {
    private final String API_ROOT = "/api/v1/payments";

    @Test
    public void testListPaymentsUnauthorized() {
        webTestClient.get().uri(API_ROOT).exchange().expectStatus().isUnauthorized();
    }

    @Test
    public void testListPayments() {
        authorizedGet(API_ROOT).exchange().expectStatus().isOk();
    }

    @Test
    public void testGetPaymentUnauthorized() {
        webTestClient.get().uri(API_ROOT + "/{id}", UUID.randomUUID()).exchange().expectStatus().isUnauthorized();
    }

    @Test
    public void testGetPayment() {
        authorizedGet(API_ROOT + "/{id}", UUID.randomUUID()).exchange().expectStatus().isNotFound();
    }

    @Test
    public void testFindPaymentUnauthorized() {
        webTestClient.get().uri(API_ROOT + "/find_by", UUID.randomUUID()).exchange().expectStatus().isUnauthorized();
    }

    @Test
    public void testFindPaymentBadRequst() {
        authorizedGet(API_ROOT + "/find_by", UUID.randomUUID()).exchange().expectStatus().isBadRequest();
    }

    @Test
    public void testFindPayment() {
        authorizedGet(API_ROOT + "/find_by?merchant_reference={merchant_reference}", UUID.randomUUID()).exchange().expectStatus().isNotFound();
    }

    @Test
    public void testCreatePaymentUnauthorized() {
        webTestClient.post().uri(API_ROOT).exchange().expectStatus().isUnauthorized();
    }

    @Test
    public void testCreatePayment() {
        var paymentDto = new PaymentRequest();
        authorizedPost(API_ROOT).body(Mono.just(paymentDto), PaymentRequest.class).exchange().expectStatus().isCreated();
    }
}
