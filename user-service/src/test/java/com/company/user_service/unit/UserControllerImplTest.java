package com.company.user_service.unit;

import com.company.user_service.dto.request.LoginRequest;
import com.company.user_service.dto.request.RegisterRequest;
import com.company.user_service.dto.response.LoginResponse;
import com.company.user_service.dto.response.UserResponse;
import com.company.user_service.exception.CustomException;
import com.company.user_service.exception.GlobalExceptionHandler;
import com.company.user_service.service.UserService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(UserController.class)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
class UserControllerImplTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    @Test
    void shouldRegisterUserWhenValidRequest() {

        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@gmail.com");
        request.setUsername("nihar");
        request.setPassword("123456");

        UserResponse response = UserResponse.builder()
                .id(1L)
                .email("test@gmail.com")
                .build();

        when(userService.register(any()))
                .thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.email").isEqualTo("test@gmail.com");

        verify(userService).register(any());
    }

    @Test
    void shouldFailRegisterWhenInvalidInput() {

        RegisterRequest request = new RegisterRequest(); // empty

        webTestClient.post()
                .uri("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }


    @Test
    void shouldLoginUserWhenValidCredentials() {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("123456");

        LoginResponse response = LoginResponse.builder()
                .token("token123")
                .type("Bearer")
                .build();

        when(userService.login(any()))
                .thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.token").isEqualTo("token123");

        verify(userService).login(any());
    }

    @Test
    void shouldReturnUnauthorizedWhenInvalidCredentials() {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("wrongpassword"); // valid format, wrong logically

        when(userService.login(any()))
                .thenReturn(Mono.error(new CustomException("Invalid credentials", "UNAUTHORIZED", 401)));

        webTestClient.post()
                .uri("/api/v1/users/login")
                .bodyValue(request)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldReturnUserWhenUserExists() {

        UserResponse response = UserResponse.builder()
                .id(1L)
                .email("test@gmail.com")
                .build();

        when(userService.getUserById(1L))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri("/api/v1/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1);

        verify(userService).getUserById(1L);
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() {

        when(userService.getUserById(1L))
                .thenReturn(Mono.error(new CustomException("Not found", "USER_NOT_FOUND", 404)));

        webTestClient.get()
                .uri("/api/v1/users/1")
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    void shouldReturnAllUsers() {

        UserResponse response = UserResponse.builder().id(1L).build();

        when(userService.getAllUsers())
                .thenReturn(Flux.just(response));

        webTestClient.get()
                .uri("/api/v1/users")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponse.class)
                .hasSize(1);

        verify(userService).getAllUsers();
    }

    @Test
    void shouldDeleteUserWhenUserExists() {

        when(userService.deleteUser(1L))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1/users/1")
                .exchange()
                .expectStatus().isNoContent();

        verify(userService).deleteUser(1L);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingUser() {

        when(userService.deleteUser(1L))
                .thenReturn(Mono.error(new CustomException("Not found", "USER_NOT_FOUND", 404)));

        webTestClient.delete()
                .uri("/api/v1/users/1")
                .exchange()
                .expectStatus().isNotFound();
    }
}