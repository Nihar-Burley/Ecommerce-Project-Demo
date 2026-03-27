package com.company.user_service.service.impl;

import com.company.user_service.dto.request.LoginRequest;
import com.company.user_service.dto.request.RegisterRequest;
import com.company.user_service.dto.response.LoginResponse;
import com.company.user_service.dto.response.UserResponse;
import com.company.user_service.entity.Role;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //REGISTER USER
    @Override
    public Mono<UserResponse> register(RegisterRequest request) {

        log.info("Register request received for email: {}", request.getEmail());

        return userRepository.existsByEmail(request.getEmail())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        log.error("Email already exists: {}", request.getEmail());
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
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();

                    return userRepository.save(user);
                })
                .map(UserMapper::toResponse)
                .doOnSuccess(res -> log.info("User registered successfully with id: {}", res.getId()))
                .doOnError(err -> log.error("Error while registering user: {}", err.getMessage()));
    }

    //GET USER BY ID
    @Override
    public Mono<UserResponse> getUserById(Long id) {

        log.info("Fetching user with id: {}", id);

        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomException(
                        "User not found",
                        "USER_NOT_FOUND",
                        404
                )))
                .map(UserMapper::toResponse);
    }

    //GET ALL USERS
    @Override
    public Flux<UserResponse> getAllUsers() {

        log.info("Fetching all users");

        return userRepository.findAll()
                .map(UserMapper::toResponse);
    }

    //DELETE USER
    @Override
    public Mono<Void> deleteUser(Long id) {

        log.info("Deleting user with id: {}", id);

        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomException(
                        "User not found",
                        "USER_NOT_FOUND",
                        404
                )))
                .flatMap(userRepository::delete)
                .doOnSuccess(v -> log.info("User deleted successfully: {}", id));
    }

    //LOGIN
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
                    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        return Mono.error(new CustomException(
                                "Invalid credentials",
                                "INVALID_CREDENTIALS",
                                401
                        ));
                    }

                    // JWT will be added later
                    return Mono.just(LoginResponse.builder()
                            .token("DUMMY_TOKEN")
                            .type("Bearer")
                            .build());
                });
    }
}