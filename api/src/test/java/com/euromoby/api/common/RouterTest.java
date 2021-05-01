package com.euromoby.api.common;

import com.euromoby.api.TestDataLoader;
import com.euromoby.api.merchant.Merchant;
import com.euromoby.api.merchant.MerchantRepository;
import com.euromoby.api.security.JwtUtil;
import com.euromoby.api.security.SecurityConstants;
import com.euromoby.api.user.User;
import com.euromoby.api.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class RouterTest extends BaseTest {
    public static final String USER_EMAIL = "user@euromoby.com";
    private static final String MERCHANT_NAME = "junit";
    private static final String MERCHANT_API_KEY = "junit-api-key";
    private static final String ADMIN_EMAIL = "admin@euromoby.com";

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    MerchantRepository merchantRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    TestDataLoader testDataLoader;

    @BeforeEach
    public void setUp() {
        super.setUp();

        testDataLoader.loadData();
    }

    protected UUID getJUnitMerchantId() {
        return merchantRepository.findByName(MERCHANT_NAME).map(Merchant::getId).block();
    }

    protected User getJUnitUser() {
        return userRepository.findByEmail(USER_EMAIL).block();
    }

    protected WebTestClient.RequestHeadersSpec authorizedMerchantGet(String uri, Object... uriVariables) {
        return webTestClient.get().uri(uri, uriVariables)
                .header(SecurityConstants.HEADER_MERCHANT, MERCHANT_NAME)
                .header(SecurityConstants.HEADER_API_KEY, MERCHANT_API_KEY);
    }

    protected WebTestClient.RequestBodySpec authorizedMerchantPost(String uri, Object... uriVariables) {
        return webTestClient.post().uri(uri, uriVariables)
                .header(SecurityConstants.HEADER_MERCHANT, MERCHANT_NAME)
                .header(SecurityConstants.HEADER_API_KEY, MERCHANT_API_KEY)
                .contentType(MediaType.APPLICATION_JSON);
    }

    protected WebTestClient.RequestHeadersSpec authorizedAdminGet(String uri, Object... uriVariables) {
        return webTestClient.get().uri(uri, uriVariables)
                .header(HttpHeaders.AUTHORIZATION, getBearerTokenForUser(ADMIN_EMAIL));
    }

    protected WebTestClient.RequestBodySpec authorizedAdminPost(String uri, Object... uriVariables) {
        return webTestClient.post().uri(uri, uriVariables)
                .header(HttpHeaders.AUTHORIZATION, getBearerTokenForUser(ADMIN_EMAIL))
                .contentType(MediaType.APPLICATION_JSON);
    }

    protected WebTestClient.RequestHeadersSpec authorizedUserGet(String uri, Object... uriVariables) {
        return webTestClient.get().uri(uri, uriVariables)
                .header(HttpHeaders.AUTHORIZATION, getBearerTokenForUser(USER_EMAIL));
    }

    protected WebTestClient.RequestBodySpec authorizedUserPost(String uri, Object... uriVariables) {
        return webTestClient.post().uri(uri, uriVariables)
                .header(HttpHeaders.AUTHORIZATION, getBearerTokenForUser(USER_EMAIL))
                .contentType(MediaType.APPLICATION_JSON);
    }

    private String getBearerTokenForUser(String email) {
        return userRepository.findByEmail(email).map(
                user -> SecurityConstants.BEARER + " " + jwtUtil.buildAuthResponse(user).getToken()
        ).block();
    }
}
