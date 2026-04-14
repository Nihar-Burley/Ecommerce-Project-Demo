package com.company.user_service.unit;

import com.company.user_service.config.JwtUtil;
import com.company.user_service.dto.request.LoginRequest;
import com.company.user_service.dto.request.RegisterRequest;
import com.company.user_service.entity.User;
import com.company.user_service.exception.CustomException;
import com.company.user_service.repository.UserRepository;
import com.company.user_service.service.impl.UserServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl service;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setup() {

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("Nihar");
        registerRequest.setEmail("test@gmail.com");
        registerRequest.setPassword("1234");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@gmail.com");
        loginRequest.setPassword("1234");

        user = User.builder()
                .id(1L)
                .username("Nihar")
                .email("test@gmail.com")
                .password("encoded")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }


    @Test
    void shouldRegisterUser() {

        when(userRepository.existsByEmail("test@gmail.com"))
                .thenReturn(Mono.just(false));

        when(passwordEncoder.encode("1234"))
                .thenReturn("encoded");

        when(userRepository.save(any()))
                .thenReturn(Mono.just(user));

        StepVerifier.create(service.register(registerRequest))
                .expectNextMatches(res -> res.getEmail().equals("test@gmail.com"))
                .verifyComplete();
    }


    @Test
    void shouldThrowWhenEmailAlreadyExists() {

        when(userRepository.existsByEmail("test@gmail.com"))
                .thenReturn(Mono.just(true));

        StepVerifier.create(service.register(registerRequest))
                .expectError(CustomException.class)
                .verify();
    }


    @Test
    void shouldGetUserById() {

        when(userRepository.findById(1L))
                .thenReturn(Mono.just(user));

        StepVerifier.create(service.getUserById(1L))
                .expectNextMatches(res -> res.getId().equals(1L))
                .verifyComplete();
    }


    @Test
    void shouldThrowWhenUserNotFound() {

        when(userRepository.findById(1L))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.getUserById(1L))
                .expectError(CustomException.class)
                .verify();
    }


    @Test
    void shouldReturnAllUsers() {

        when(userRepository.findAll())
                .thenReturn(Flux.just(user));

        StepVerifier.create(service.getAllUsers())
                .expectNextCount(1)
                .verifyComplete();
    }


    @Test
    void shouldDeleteUser() {

        when(userRepository.findById(1L))
                .thenReturn(Mono.just(user));

        when(userRepository.delete(user))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.deleteUser(1L))
                .verifyComplete();
    }


    @Test
    void shouldThrowWhenDeletingNonExistingUser() {

        when(userRepository.findById(1L))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.deleteUser(1L))
                .expectError(CustomException.class)
                .verify();
    }


    @Test
    void shouldLoginSuccessfully() {

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Mono.just(user));

        when(passwordEncoder.matches("1234", "encoded"))
                .thenReturn(true);

        when(jwtUtil.generateToken(any(), any(), any()))
                .thenReturn("token123");

        StepVerifier.create(service.login(loginRequest))
                .expectNextMatches(res -> res.getToken().equals("token123"))
                .verifyComplete();
    }


    @Test
    void shouldThrowWhenUserNotFoundDuringLogin() {

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.login(loginRequest))
                .expectError(CustomException.class)
                .verify();
    }


    @Test
    void shouldThrowWhenInvalidPassword() {

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Mono.just(user));

        when(passwordEncoder.matches("1234", "encoded"))
                .thenReturn(false);

        StepVerifier.create(service.login(loginRequest))
                .expectError(CustomException.class)
                .verify();
    }
}