package com.euromoby.api.common;

import com.euromoby.api.security.AuthFilter;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class RouterTest {
    private final String MERCHANT = "63997f34-66d5-4e49-82a3-065dca2ff149";
    private final String SECRET = "065dca2ff149";

    @Autowired
    protected WebTestClient webTestClient;

    protected WebTestClient.RequestHeadersSpec authorizedGet(String uri, Object... uriVariables) {
        return webTestClient.get().uri(uri, uriVariables)
                .header(AuthFilter.HEADER_MERCHANT, MERCHANT)
                .header(AuthFilter.HEADER_SECRET, SECRET);
    }

    protected WebTestClient.RequestBodySpec authorizedPost(String uri, Object... uriVariables) {
        return webTestClient.post().uri(uri, uriVariables)
                .header(AuthFilter.HEADER_MERCHANT, MERCHANT)
                .header(AuthFilter.HEADER_SECRET, SECRET)
                .contentType(MediaType.APPLICATION_JSON);
    }
}
