package com.euromoby.api.payment;

import com.euromoby.api.security.AuthFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class PaymentRouterTest {
    private final String MERCHANT = "63997f34-66d5-4e49-82a3-065dca2ff149";
    private final String SECRET = "065dca2ff149";
    private final String API_ROOT = "/api/v1/payments";

    @Autowired
    private WebTestClient webTestClient;

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
    public void testCreatePaymentUnauthorized() {
        webTestClient.post().uri(API_ROOT).exchange().expectStatus().isUnauthorized();
    }

    @Test
    public void testCreatePayment() {
        var paymentDto = new PaymentRequest();
        authorizedPost(API_ROOT).body(Mono.just(paymentDto), PaymentRequest.class).exchange().expectStatus().isCreated();
    }

    WebTestClient.RequestHeadersSpec authorizedGet(String uri, Object... uriVariables) {
        return webTestClient.get().uri(uri, uriVariables)
                .header(AuthFilter.HEADER_MERCHANT, MERCHANT)
                .header(AuthFilter.HEADER_SECRET, SECRET);
    }

    WebTestClient.RequestBodySpec authorizedPost(String uri, Object... uriVariables) {
        return webTestClient.post().uri(uri, uriVariables)
                .header(AuthFilter.HEADER_MERCHANT, MERCHANT)
                .header(AuthFilter.HEADER_SECRET, SECRET)
                .contentType(MediaType.APPLICATION_JSON);
    }

}
