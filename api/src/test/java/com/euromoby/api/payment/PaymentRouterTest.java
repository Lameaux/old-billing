package com.euromoby.api.payment;

import com.euromoby.api.common.RouterTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;

public class PaymentRouterTest extends RouterTest {
    private final String API_ROOT = "/api/v1/payments";

    private PaymentService paymentService;

    @Autowired
    public PaymentRouterTest(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public PaymentResponse createNewPayment() {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setMerchantReference(UUID.randomUUID().toString());
        Mono<PaymentResponse> response = paymentService.createPayment(
                junitMerchant.getId(),
                Mono.just(paymentRequest)
        );
        return response.block();
    }

    @Test
    public void testListPaymentsUnauthorized() {
        webTestClient.get().uri(API_ROOT).exchange().expectStatus().isUnauthorized();
    }

    @Test
    public void testListPayments() {
        PaymentResponse newPayment = createNewPayment();

        authorizedGet(API_ROOT).exchange()
                .expectStatus().isOk()
                .expectBodyList(PaymentResponse.class).contains(newPayment);
    }

    @Test
    public void testGetPaymentUnauthorized() {
        webTestClient.get().uri(API_ROOT + "/{id}", UUID.randomUUID()).exchange().expectStatus().isUnauthorized();
    }

    @Test
    public void testGetPaymentNotFound() {
        authorizedGet(API_ROOT + "/{id}", UUID.randomUUID()).exchange().expectStatus().isNotFound();
    }

    @Test
    public void testGetPayment() {
        PaymentResponse newPayment = createNewPayment();

        authorizedGet(API_ROOT + "/{id}", newPayment.getId()).exchange()
                .expectStatus().isOk()
                .expectBody(PaymentResponse.class).isEqualTo(newPayment);
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
    public void testFindPaymentNotFound() {
        authorizedGet(API_ROOT + "/find_by?merchant_reference={merchant_reference}", UUID.randomUUID()).exchange().expectStatus().isNotFound();
    }

    @Test
    public void testFindPayment() {
        PaymentResponse newPayment = createNewPayment();

        authorizedGet(API_ROOT + "/find_by?merchant_reference={merchant_reference}", newPayment.getMerchantReference()).exchange()
                .expectStatus().isOk()
                .expectBody(PaymentResponse.class).isEqualTo(newPayment);
    }

    @Test
    public void testCreatePaymentUnauthorized() {
        webTestClient.post().uri(API_ROOT).exchange().expectStatus().isUnauthorized();
    }

    @Test
    public void testCreatePayment() {
        var paymentRequest = new PaymentRequest();
        paymentRequest.setMerchantReference(UUID.randomUUID().toString());

        authorizedPost(API_ROOT).body(Mono.just(paymentRequest), PaymentRequest.class).exchange()
                .expectStatus().isCreated()
                .expectBody(PaymentResponse.class)
                .value(PaymentResponse::getMerchantReference, equalTo(paymentRequest.getMerchantReference()));

    }
}
