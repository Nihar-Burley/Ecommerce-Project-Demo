package com.company.user_service.unit;


import com.company.common.constants.Role;
import com.company.common.constants.UserStatus;
import com.company.common.dto.user.request.LoginRequest;
import com.company.common.dto.user.request.RegisterRequest;
import com.company.user_service.config.JwtUtil;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;
import java.util.Date;
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .username("nihar")
                .email("nihar@test.com")
                .password("encoded")
                .role(Role.USER)
                .status(UserStatus.APPROVED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ================= REGISTER =================

    @Test
    void register_success() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("nihar");
        req.setEmail("nihar@test.com");
        req.setPassword("password");

        when(userRepository.existsByEmail(req.getEmail())).thenReturn(Mono.just(false));
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(Mono.just(user));

        StepVerifier.create(userService.register(req))
                .assertNext(res -> {
                    assertEquals("nihar@test.com", res.getEmail());
                    assertEquals("nihar", res.getUsername());
                })
                .verifyComplete();
    }

    @Test
    void register_emailAlreadyExists() {
        when(userRepository.existsByEmail(any())).thenReturn(Mono.just(true));

        StepVerifier.create(userService.register(new RegisterRequest()))
                .expectErrorMatches(ex ->
                        ex instanceof CustomException &&
                                ((CustomException) ex).getErrorCode().equals("USER_ALREADY_EXISTS"))
                .verify();
    }

    // ================= LOGIN =================

    @Test
    void login_success() {
        LoginRequest req = new LoginRequest();
        req.setEmail("nihar@test.com");
        req.setPassword("password");

        when(userRepository.findByEmail(req.getEmail())).thenReturn(Mono.just(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtUtil.generateToken(any(), any(), any())).thenReturn("token");

        Date expiry = new Date(System.currentTimeMillis() + 10000);
        when(jwtUtil.extractExpiration(any())).thenReturn(expiry);

        StepVerifier.create(userService.login(req))
                .assertNext(res -> {
                    assertEquals("token", res.getToken());
                    assertEquals("Bearer", res.getType());
                    assertTrue(res.getExpiresIn() > 0);
                })
                .verifyComplete();
    }

    @Test
    void login_userNotFound() {
        when(userRepository.findByEmail(any())).thenReturn(Mono.empty());

        StepVerifier.create(userService.login(new LoginRequest()))
                .expectErrorMatches(ex ->
                        ex instanceof CustomException &&
                                ((CustomException) ex).getErrorCode().equals("INVALID_CREDENTIALS"))
                .verify();
    }

    @Test
    void login_wrongPassword() {
        when(userRepository.findByEmail(any())).thenReturn(Mono.just(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        StepVerifier.create(userService.login(new LoginRequest()))
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    void login_notApproved() {
        user.setStatus(UserStatus.PENDING);
        when(userRepository.findByEmail(any())).thenReturn(Mono.just(user));

        StepVerifier.create(userService.login(new LoginRequest()))
                .expectErrorMatches(ex ->
                        ((CustomException) ex).getErrorCode().equals("USER_NOT_APPROVED"))
                .verify();
    }

    // ================= GET USER =================

    @Test
    void getUser_success() {
        when(userRepository.findById(1L)).thenReturn(Mono.just(user));

        StepVerifier.create(userService.getUserById(1L))
                .expectNextMatches(res -> res.getId().equals(1L))
                .verifyComplete();
    }

    @Test
    void getUser_notFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Mono.empty());

        StepVerifier.create(userService.getUserById(1L))
                .expectErrorMatches(ex ->
                        ((CustomException) ex).getErrorCode().equals("USER_NOT_FOUND"))
                .verify();
    }

    // ================= GET ALL =================

    @Test
    void getAllUsers_success() {
        when(userRepository.findAll()).thenReturn(Flux.just(user));

        StepVerifier.create(userService.getAllUsers())
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getAllUsers_empty() {
        when(userRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(userService.getAllUsers())
                .expectNextCount(0)
                .verifyComplete();
    }

    // ================= DELETE =================

    @Test
    void delete_success() {
        when(userRepository.findById(1L)).thenReturn(Mono.just(user));
        when(userRepository.delete(any())).thenReturn(Mono.empty());

        StepVerifier.create(userService.deleteUser(1L))
                .verifyComplete();
    }

    @Test
    void delete_notFound() {
        when(userRepository.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(userService.deleteUser(1L))
                .expectError(CustomException.class)
                .verify();
    }

    // ================= APPROVE =================

    @Test
    void approve_success() {
        user.setStatus(UserStatus.PENDING);

        when(userRepository.findById(1L)).thenReturn(Mono.just(user));
        when(userRepository.save(any())).thenReturn(Mono.just(user));

        StepVerifier.create(userService.approveUser(1L))
                .expectNextMatches(res -> res.getId().equals(1L))
                .verifyComplete();
    }

    @Test
    void approve_alreadyApproved() {
        user.setStatus(UserStatus.APPROVED);

        when(userRepository.findById(1L)).thenReturn(Mono.just(user));

        StepVerifier.create(userService.approveUser(1L))
                .expectErrorMatches(ex ->
                        ((CustomException) ex).getErrorCode().equals("USER_ALREADY_APPROVED"))
                .verify();
    }
}