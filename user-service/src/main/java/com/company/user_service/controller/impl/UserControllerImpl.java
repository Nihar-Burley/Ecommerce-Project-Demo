package com.company.user_service.controller.impl;

import com.company.common.controller.user.UserController;
import com.company.common.dto.user.request.LoginRequest;
import com.company.common.dto.user.request.RegisterRequest;
import com.company.common.dto.user.response.LoginResponse;
import com.company.common.dto.user.response.UserResponse;
import com.company.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    public Mono<UserResponse> register(RegisterRequest request) {
        log.info("API: Register user");
        return userService.register(request);
    }

    @Override
    public Mono<LoginResponse> login(LoginRequest request) {
        log.info("API: Login user");
        return userService.login(request);
    }

    @Override
    public Mono<UserResponse> getUserById(Long id) {
        log.info("API: Get user {}", id);
        return userService.getUserById(id);
    }

    @Override
    public Flux<UserResponse> getAllUsers() {
        log.info("API: Get all users");
        return userService.getAllUsers();
    }

    @Override
    public Mono<Void> deleteUser(Long id) {
        log.info("API: Delete user {}", id);
        return userService.deleteUser(id);
    }

    @Override
    public Mono<UserResponse> approveUser(Long id) {
        log.info("API: Approve user {}", id);

        return userService.approveUser(id);
    }
}