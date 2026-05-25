package com.example.usermanagement.controller;

import com.example.usermanagement.dto.PageResponse;
import com.example.usermanagement.dto.UserRequest;
import com.example.usermanagement.dto.UserResponse;
import com.example.usermanagement.exception.DuplicateEmailException;
import com.example.usermanagement.exception.GlobalExceptionHandler;
import com.example.usermanagement.exception.UserNotFoundException;
import com.example.usermanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    UserService userService;
    WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        webTestClient = WebTestClient.bindToController(new UserController(userService))
                .controllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void findAllReturnsPageResponse() {
        when(userService.findAll(any(), any())).thenReturn(Mono.just(pageResponse()));

        webTestClient.get()
                .uri("/api/v1/users?page=0&size=10&sortBy=id&direction=ASC")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content[0].email").isEqualTo("omar@example.com")
                .jsonPath("$.totalElements").isEqualTo(1);
    }

    @Test
    void findAllReturns400WhenSortFieldInvalid() {
        webTestClient.get()
                .uri("/api/v1/users?page=0&size=10&sortBy=doesNotExist&direction=ASC")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("INVALID_SORT_FIELD");
    }

    @Test
    void findAllReturns400WhenPageNegative() {
        when(userService.findAll(any(), any())).thenReturn(Mono.error(new IllegalArgumentException("Page index must not be negative")));

        webTestClient.get().
                uri("/api/v1/users?page=-1&size=10&sortBy=id&direction=ASC")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("CONSTRAINT_VIOLATION");
//        webTestClient.get()
//                .uri("/api/v1/users?page=-1&size=10&sortBy=id&direction=ASC")
//                .exchange()
//                .expectStatus().is5xxServerError()
//                .expectBody()
//                .jsonPath("$.code").isEqualTo("CONSTRAINT_VIOLATION");
    }

    @Test
    void findByIdReturns404WhenMissing() {
        when(userService.findById(99L)).thenReturn(Mono.error(new UserNotFoundException(99L)));

        webTestClient.get().uri("/api/v1/users/99")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo("USER_NOT_FOUND");
    }

    @Test
    void createReturns201() {
        when(userService.create(any(UserRequest.class), anyString())).thenReturn(Mono.just(response()));

        webTestClient.post().uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validJson())
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.email").isEqualTo("omar@example.com");
    }

    @Test
    void createReturns409ForDuplicateEmail() {
        when(userService.create(any(UserRequest.class), anyString())).thenReturn(Mono.error(new DuplicateEmailException("omar@example.com")));

        webTestClient.post().uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validJson())
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.code").isEqualTo("USER_EMAIL_ALREADY_EXISTS");
    }

    @Test
    void findByIdReturns200() {
        when(userService.findById(1L)).thenReturn(Mono.just(response()));

        webTestClient.get().uri("/api/v1/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.email").isEqualTo("omar@example.com");
    }

    @Test
    void updateReturns200() {
        when(userService.update(eq(1L), any(UserRequest.class), anyString())).thenReturn(Mono.just(response()));

        webTestClient.put().uri("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validJson())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.email").isEqualTo("omar@example.com");
    }

    @Test
    void updateReturns404WhenMissing() {
        when(userService.update(eq(99L), any(UserRequest.class), anyString())).thenReturn(Mono.error(new UserNotFoundException(99L)));

        webTestClient.put().uri("/api/v1/users/99")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validJson())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo("USER_NOT_FOUND");
    }

    @Test
    void updateReturns409ForDuplicateEmail() {
        when(userService.update(eq(1L), any(UserRequest.class), anyString())).thenReturn(Mono.error(new DuplicateEmailException("omar@example.com")));

        webTestClient.put().uri("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validJson())
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.code").isEqualTo("USER_EMAIL_ALREADY_EXISTS");
    }

    @Test
    void deleteReturns204() {
        when(userService.delete(eq(1L), anyString())).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/v1/users/1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void deleteReturns404WhenMissing() {
        when(userService.delete(eq(99L), anyString())).thenReturn(Mono.error(new UserNotFoundException(99L)));

        webTestClient.delete().uri("/api/v1/users/99")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo("USER_NOT_FOUND");
    }

    private String validJson() {
        return "{\"firstName\":\"Omar\",\"lastName\":\"Garcia\",\"email\":\"omar@example.com\",\"phone\":\"8112345678\",\"active\":true}";
    }

    private UserResponse response() {
        return new UserResponse(1L, "Omar", "Garcia", "omar@example.com", "8112345678", true, false, 0L,
                LocalDateTime.now(), LocalDateTime.now(), "admin", "admin");
    }

    private PageResponse<UserResponse> pageResponse() {
        return new PageResponse<>(List.of(response()), 0, 10, 1, 1, true, true);
    }
}
