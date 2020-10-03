package com.euromoby.payment.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class PaymentRouterTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testListPayments() {
        webTestClient.get().uri("/payments").exchange().expectStatus().isOk();
    }

    @Test
    public void testGetPayment() {
        webTestClient.get().uri("/payments/0").exchange().expectStatus().isNotFound();
    }

}
