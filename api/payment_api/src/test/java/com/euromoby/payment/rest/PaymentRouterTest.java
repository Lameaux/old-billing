package com.euromoby.payment.rest;

import com.euromoby.payment.dto.PaymentDto;
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

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testListPaymentsUnauthorized() {
        webTestClient.get().uri("/payments").exchange().expectStatus().isUnauthorized();
    }

    @Test
    public void testListPayments() {
        authorizedGet("/payments").exchange().expectStatus().isOk();
    }

    @Test
    public void testGetPaymentUnauthorized() {
        webTestClient.get().uri("/payments/{id}", UUID.randomUUID()).exchange().expectStatus().isUnauthorized();
    }

    @Test
    public void testGetPayment() {
        authorizedGet("/payments/{id}", UUID.randomUUID()).exchange().expectStatus().isNotFound();
    }

    @Test
    public void testCreatePaymentUnauthorized() {
        webTestClient.post().uri("/payments").exchange().expectStatus().isUnauthorized();
    }

    @Test
    public void testCreatePayment() {
        var paymentDto = new PaymentDto();
        authorizedPost("/payments").body(Mono.just(paymentDto), PaymentDto.class).exchange().expectStatus().isCreated();
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
