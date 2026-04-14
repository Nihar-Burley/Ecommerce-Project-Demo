package com.company.user_service.unit;

import com.company.common.dto.user.request.LoginRequest;
import com.company.common.dto.user.request.RegisterRequest;
import com.company.common.dto.user.response.LoginResponse;
import com.company.common.dto.user.response.UserResponse;
import com.company.user_service.controller.impl.UserControllerImpl;
import com.company.user_service.service.UserService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

@WebFluxTest(controllers = UserControllerImpl.class)
@Import(TestSecurityConfig.class)
class UserControllerImplTest {

    @Autowired
    private WebTestClient client;

    @MockBean
    private UserService userService;

    // ================= REGISTER =================
    @Test
    void register_success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("nihar");
        request.setEmail("nihar@test.com");
        request.setPassword("password");

        when(userService.register(any()))
                .thenReturn(Mono.just(UserResponse.builder()
                        .id(1L)
                        .username("nihar")
                        .email("nihar@test.com")
                        .build()));

        client.post()
                .uri("/api/v1/users/register")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponse.class)
                .value(res -> {
                    assertEquals(1L, res.getId());
                    assertEquals("nihar@test.com", res.getEmail());
                });
    }

    // ================= LOGIN =================
    @Test
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("nihar@test.com");
        request.setPassword("password");

        when(userService.login(any()))
                .thenReturn(Mono.just(LoginResponse.builder()
                        .token("test-token")
                        .type("Bearer")
                        .build()));

        client.post()
                .uri("/api/v1/users/login")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoginResponse.class)
                .value(res -> {
                    assertEquals("test-token", res.getToken());
                    assertEquals("Bearer", res.getType());
                });
    }

    // ================= GET USER =================
    @Test
    void getUser_success() {
        when(userService.getUserById(1L))
                .thenReturn(Mono.just(UserResponse.builder()
                        .id(1L)
                        .username("nihar")
                        .build()));

        client.get()
                .uri("/api/v1/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponse.class)
                .value(res -> assertEquals(1L, res.getId()));
    }

    // ================= GET ALL =================
    @Test
    void getAll_success() {
        when(userService.getAllUsers())
                .thenReturn(Flux.just(
                        UserResponse.builder().id(1L).build(),
                        UserResponse.builder().id(2L).build()
                ));

        client.get()
                .uri("/api/v1/users")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponse.class)
                .hasSize(2);
    }

    // ================= DELETE =================
    @Test
    void delete_success() {
        when(userService.deleteUser(1L)).thenReturn(Mono.empty());

        client.delete()
                .uri("/api/v1/users/1")
                .exchange()
                .expectStatus().isNoContent(); // ✅ correct (204)
    }

    // ================= APPROVE =================
    @Test
    void approve_success() {
        when(userService.approveUser(1L))
                .thenReturn(Mono.just(UserResponse.builder()
                        .id(1L)
                        .build()));

        client.put()
                .uri("/api/v1/users/1/approve")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponse.class)
                .value(res -> assertEquals(1L, res.getId()));
    }
}