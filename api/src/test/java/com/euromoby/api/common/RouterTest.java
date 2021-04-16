package com.euromoby.api.common;

import com.euromoby.api.merchant.Merchant;
import com.euromoby.api.merchant.MerchantRepository;
import com.euromoby.api.security.SecurityConstants;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class RouterTest extends BaseTest {
    private static final String MERCHANT_NAME = "junit";
    private static final String MERCHANT_API_KEY = "junit-api-key";

    @Autowired
    protected WebTestClient webTestClient;
    protected Merchant junitMerchant;
    @Autowired
    MerchantRepository merchantRepository;

    @BeforeEach
    public void setUp() {
        junitMerchant = merchantRepository.findByName(MERCHANT_NAME).block();
    }

    protected WebTestClient.RequestHeadersSpec authorizedGet(String uri, Object... uriVariables) {
        return webTestClient.get().uri(uri, uriVariables)
                .header(SecurityConstants.HEADER_MERCHANT, junitMerchant.getId().toString())
                .header(SecurityConstants.HEADER_API_KEY, MERCHANT_API_KEY);
    }

    protected WebTestClient.RequestBodySpec authorizedPost(String uri, Object... uriVariables) {
        return webTestClient.post().uri(uri, uriVariables)
                .header(SecurityConstants.HEADER_MERCHANT, junitMerchant.getId().toString())
                .header(SecurityConstants.HEADER_API_KEY, MERCHANT_API_KEY)
                .contentType(MediaType.APPLICATION_JSON);
    }
}
