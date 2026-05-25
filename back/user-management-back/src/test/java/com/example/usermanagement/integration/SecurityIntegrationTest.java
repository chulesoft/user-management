package com.example.usermanagement.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired WebTestClient client;

    @Test
    void apiReturns401WhenNoAuth() {
        client.get().uri("/api/v1/users").exchange().expectStatus().isUnauthorized();
    }

    @Test
    void apiReturns200WhenAuthenticated() {
        client.get().uri("/api/v1/users")
                .headers(h -> h.setBasicAuth("admin", "password"))
                .exchange()
                .expectStatus().isOk();
    }
}
