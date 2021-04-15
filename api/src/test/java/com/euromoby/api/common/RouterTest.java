package com.euromoby.api.common;

import com.euromoby.api.merchant.Merchant;
import com.euromoby.api.merchant.MerchantRepository;
import com.euromoby.api.security.AuthFilter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class RouterTest extends BaseTest {
    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    MerchantRepository merchantRepository;

    protected Merchant junitMerchant;

    @BeforeEach
    public void setUp() {
        junitMerchant = merchantRepository.findByName("junit").block();
    }

    protected WebTestClient.RequestHeadersSpec authorizedGet(String uri, Object... uriVariables) {
        return webTestClient.get().uri(uri, uriVariables)
                .header(AuthFilter.HEADER_MERCHANT, junitMerchant.getId().toString())
                .header(AuthFilter.HEADER_API_KEY, junitMerchant.getApiKey());
    }

    protected WebTestClient.RequestBodySpec authorizedPost(String uri, Object... uriVariables) {
        return webTestClient.post().uri(uri, uriVariables)
                .header(AuthFilter.HEADER_MERCHANT, junitMerchant.getId().toString())
                .header(AuthFilter.HEADER_API_KEY, junitMerchant.getApiKey())
                .contentType(MediaType.APPLICATION_JSON);
    }
}
