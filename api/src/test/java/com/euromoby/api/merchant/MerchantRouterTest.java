package com.euromoby.api.merchant;

import com.euromoby.api.common.RouterTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;

class MerchantRouterTest extends RouterTest {
    private final String API_ROOT = "/api/v1/merchants";

    private MerchantService merchantService;

    @Autowired
    MerchantRouterTest(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    MerchantResponse createNewMerchant() {
        var merchantRequest = new MerchantRequest();
        merchantRequest.setName(UUID.randomUUID().toString());
        merchantRequest.setDescription("For unit testing");
        merchantRequest.setEnv(MerchantEnv.TEST);

        Mono<MerchantResponse> response = merchantService.createMerchant(
                Mono.just(merchantRequest),
                getJUnitUser().getId()
        );
        return response.block();
    }

    @Test
    void listMerchantsAsAnonymous() {
        webTestClient.get().uri(API_ROOT).exchange().expectStatus().isUnauthorized();
    }

    @Test
    void listMerchantsAsUser() {
        MerchantResponse newMerchant = createNewMerchant();

        authorizedAdminGet(API_ROOT).exchange()
                .expectStatus().isOk()
                .expectBodyList(MerchantResponse.class).contains(newMerchant);
    }

    @Test
    void listMerchantsAsAdmin() {
        MerchantResponse newMerchant = createNewMerchant();

        authorizedAdminGet(API_ROOT).exchange()
                .expectStatus().isOk()
                .expectBodyList(MerchantResponse.class).contains(newMerchant);
    }

    @Test
    void getMerchantAsAnonymous() {
        webTestClient.get().uri(API_ROOT + "/{id}", UUID.randomUUID()).exchange().expectStatus().isUnauthorized();
    }

    @Test
    void getMerchantAsOwner() {
        authorizedUserGet(API_ROOT + "/{id}", getJUnitMerchantId()).exchange()
                .expectStatus().isOk()
                .expectBody(MerchantResponse.class)
                .value(MerchantResponse::getId, equalTo(getJUnitMerchantId()));
    }

    @Test
    void getMerchantAsOtherUser() {
        MerchantResponse newMerchant = createNewMerchant();

        authorizedUserGet(API_ROOT + "/{id}", newMerchant.getId()).exchange().expectStatus().isNotFound();
    }

    @Test
    void getMerchantAsAdmin() {
        MerchantResponse newMerchant = createNewMerchant();

        authorizedAdminGet(API_ROOT + "/{id}", newMerchant.getId()).exchange()
                .expectStatus().isOk()
                .expectBody(MerchantResponse.class).isEqualTo(newMerchant);
    }

    @Test
    void getMerchantNotFound() {
        authorizedAdminGet(API_ROOT + "/{id}", UUID.randomUUID()).exchange().expectStatus().isNotFound();
    }

    @Test
    void createMerchantAsAnonymous() {
        webTestClient.post().uri(API_ROOT).exchange().expectStatus().isUnauthorized();
    }

    @Test
    void createMerchant() {
        var merchantRequest = new MerchantRequest();
        merchantRequest.setName(UUID.randomUUID().toString());
        merchantRequest.setDescription("For unit testing");
        merchantRequest.setEnv(MerchantEnv.TEST);

        authorizedUserPost(API_ROOT).body(Mono.just(merchantRequest), MerchantRequest.class).exchange()
                .expectStatus().isCreated()
                .expectBody(MerchantResponse.class)
                .value(MerchantResponse::getName, equalTo(merchantRequest.getName()));
    }
}
