package com.company.user_service.service.impl;

import com.company.common.constants.Role;
import com.company.common.constants.UserStatus;
import com.company.user_service.config.JwtUtil;
import com.company.common.dto.user.request.LoginRequest;
import com.company.common.dto.user.request.RegisterRequest;
import com.company.common.dto.user.response.LoginResponse;
import com.company.common.dto.user.response.UserResponse;
import com.company.user_service.entity.User;
import com.company.user_service.exception.CustomException;
import com.company.user_service.mapper.UserMapper;
import com.company.user_service.repository.UserRepository;
import com.company.user_service.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // ================= REGISTER =================
    @Override
    public Mono<UserResponse> register(RegisterRequest request) {

        log.info("Register request received for email: {}", request.getEmail());

        return userRepository.existsByEmail(request.getEmail())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new CustomException(
                                "Email already exists",
                                "USER_ALREADY_EXISTS",
                                400
                        ));
                    }

                    User user = User.builder()
                            .username(request.getUsername())
                            .email(request.getEmail())
                            .password(passwordEncoder.encode(request.getPassword()))
                            .role(Role.USER)
                            .status(UserStatus.PENDING)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();

                    return userRepository.save(user);
                })
                .map(UserMapper::toResponse)
                .doOnSuccess(res -> log.info("User registered successfully: {}", res.getId()));
    }

    // ================= LOGIN =================
    @Override
    public Mono<LoginResponse> login(LoginRequest request) {

        log.info("Login request for email: {}", request.getEmail());

        return userRepository.findByEmail(request.getEmail())
                .switchIfEmpty(Mono.error(new CustomException(
                        "Invalid credentials",
                        "INVALID_CREDENTIALS",
                        401
                )))
                .flatMap(user -> {

                    // 🔥 APPROVAL CHECK
                    if (user.getStatus() == null || user.getStatus() != UserStatus.APPROVED) {
                        return Mono.error(new CustomException(
                                "User not approved",
                                "USER_NOT_APPROVED",
                                403
                        ));
                    }

                    // 🔐 PASSWORD CHECK
                    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        return Mono.error(new CustomException(
                                "Invalid credentials",
                                "INVALID_CREDENTIALS",
                                401
                        ));
                    }

                    // 🔑 GENERATE TOKEN
                    String token = jwtUtil.generateToken(
                            user.getEmail(),
                            user.getRole().name(),
                            user.getId()
                    );

                    // 🕒 EXPIRY INFO
                    Date expiry = jwtUtil.extractExpiration(token);
                    long expiresAt = expiry.getTime();
                    long expiresIn = (expiresAt - System.currentTimeMillis()) / 1000;

                    return Mono.just(LoginResponse.builder()
                            .token(token)
                            .type("Bearer")
                            .expiresAt(expiresAt)
                            .expiresIn(expiresIn)
                            .build());
                });
    }

    // ================= GET USER =================
    @Override
    public Mono<UserResponse> getUserById(Long id) {

        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomException(
                        "User not found",
                        "USER_NOT_FOUND",
                        404
                )))
                .map(UserMapper::toResponse);
    }

    // ================= GET ALL =================
    @Override
    public Flux<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .map(UserMapper::toResponse);
    }

    // ================= DELETE =================
    @Override
    public Mono<Void> deleteUser(Long id) {

        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomException(
                        "User not found",
                        "USER_NOT_FOUND",
                        404
                )))
                .flatMap(userRepository::delete);
    }

    // ================= APPROVE USER =================
    @Override
    public Mono<UserResponse> approveUser(Long id) {

        log.info("Approving user: {}", id);

        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomException(
                        "User not found",
                        "USER_NOT_FOUND",
                        404
                )))
                .flatMap(user -> {

                    if (UserStatus.APPROVED.equals(user.getStatus())) {
                        return Mono.error(new CustomException(
                                "User already approved",
                                "USER_ALREADY_APPROVED",
                                400
                        ));
                    }

                    user.setStatus(UserStatus.APPROVED);
                    user.setUpdatedAt(LocalDateTime.now());

                    return userRepository.save(user);
                })
                .map(UserMapper::toResponse);
    }
}