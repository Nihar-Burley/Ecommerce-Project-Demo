package com.company.user_service.service;


import com.company.user_service.dto.request.LoginRequest;
import com.company.user_service.dto.request.RegisterRequest;
import com.company.user_service.dto.response.LoginResponse;
import com.company.user_service.dto.response.UserResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserResponse> register(RegisterRequest request);

    Mono<UserResponse> getUserById(Long id);

    Flux<UserResponse> getAllUsers();

    Mono<Void> deleteUser(Long id);

    Mono<LoginResponse> login(LoginRequest request);
}