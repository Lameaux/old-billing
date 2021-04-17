package com.euromoby.api.payment;

import com.euromoby.api.common.RouterTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

class PaymentRouterTest extends RouterTest {
    private final String API_ROOT = "/api/v1/payments";

    private PaymentService paymentService;

    @Autowired
    PaymentRouterTest(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    PaymentResponse createNewPayment() {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setMerchantReference(UUID.randomUUID().toString());
        paymentRequest.setCurrency("EUR");
        paymentRequest.setNetAmount(new BigDecimal("1.00"));
        paymentRequest.setVatAmount(new BigDecimal("0.00"));
        paymentRequest.setVatRate(new BigDecimal("0.00"));
        paymentRequest.setTotalAmount(new BigDecimal("1.00"));

        Mono<PaymentResponse> response = paymentService.createPayment(
                getJUnitMerchantId(),
                Mono.just(paymentRequest)
        );
        return response.block();
    }

    @Test
    void testListPaymentsUnauthorized() {
        webTestClient.get().uri(API_ROOT).exchange().expectStatus().isUnauthorized();
    }

    @Test
    void testListPayments() {
        PaymentResponse newPayment = createNewPayment();

        authorizedGet(API_ROOT).exchange()
                .expectStatus().isOk()
                .expectBodyList(PaymentResponse.class)
                .value(list -> {
                    assertThat(list.stream().anyMatch(
                            p -> p.equals(newPayment)
                    ));
                });
    }

    @Test
    void testGetPaymentUnauthorized() {
        webTestClient.get().uri(API_ROOT + "/{id}", UUID.randomUUID()).exchange().expectStatus().isUnauthorized();
    }

    @Test
    void testGetPaymentNotFound() {
        authorizedGet(API_ROOT + "/{id}", UUID.randomUUID()).exchange().expectStatus().isNotFound();
    }

    @Test
    void testGetPayment() {
        PaymentResponse newPayment = createNewPayment();

        authorizedGet(API_ROOT + "/{id}", newPayment.getId()).exchange()
                .expectStatus().isOk()
                .expectBody(PaymentResponse.class).isEqualTo(newPayment);
    }

    @Test
    void testFindPaymentUnauthorized() {
        webTestClient.get().uri(API_ROOT + "/find_by", UUID.randomUUID()).exchange().expectStatus().isUnauthorized();
    }

    @Test
    void testFindPaymentBadRequst() {
        authorizedGet(API_ROOT + "/find_by", UUID.randomUUID()).exchange().expectStatus().isBadRequest();
    }

    @Test
    void testFindPaymentNotFound() {
        authorizedGet(API_ROOT + "/find_by?merchant_reference={merchant_reference}", UUID.randomUUID()).exchange().expectStatus().isNotFound();
    }

    @Test
    void testFindPayment() {
        PaymentResponse newPayment = createNewPayment();

        authorizedGet(API_ROOT + "/find_by?merchant_reference={merchant_reference}", newPayment.getMerchantReference()).exchange()
                .expectStatus().isOk()
                .expectBody(PaymentResponse.class).isEqualTo(newPayment);
    }

    @Test
    void testCreatePaymentUnauthorized() {
        webTestClient.post().uri(API_ROOT).exchange().expectStatus().isUnauthorized();
    }

    @Test
    void testCreatePayment() {
        var paymentRequest = new PaymentRequest();
        paymentRequest.setMerchantReference(UUID.randomUUID().toString());
        paymentRequest.setCurrency("EUR");
        paymentRequest.setTotalAmount(new BigDecimal("1.00"));

        authorizedPost(API_ROOT).body(Mono.just(paymentRequest), PaymentRequest.class).exchange()
                .expectStatus().isCreated()
                .expectBody(PaymentResponse.class)
                .value(PaymentResponse::getMerchantReference, equalTo(paymentRequest.getMerchantReference()));

    }
}
