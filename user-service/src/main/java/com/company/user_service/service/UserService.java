package com.company.user_service.service;


import com.company.common.dto.user.request.LoginRequest;
import com.company.common.dto.user.request.RegisterRequest;
import com.company.common.dto.user.response.LoginResponse;
import com.company.common.dto.user.response.UserResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserResponse> register(RegisterRequest request);

    Mono<UserResponse> getUserById(Long id);

    Flux<UserResponse> getAllUsers();

    Mono<Void> deleteUser(Long id);

    Mono<LoginResponse> login(LoginRequest request);

    Mono<UserResponse> approveUser(Long id);
}