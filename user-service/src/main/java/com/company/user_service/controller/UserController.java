package com.company.user_service.controller;

import com.company.user_service.dto.request.LoginRequest;
import com.company.user_service.dto.request.RegisterRequest;
import com.company.user_service.dto.response.LoginResponse;
import com.company.user_service.dto.response.UserResponse;
import com.company.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User API", description = "Operations related to Users")
public class UserController {

    private final UserService userService;

    // REGISTER
    @Operation(summary = "Register user", description = "Create a new user")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        log.info("API: Register user");
        return userService.register(request);
    }

    // LOGIN
    @Operation(summary = "Login user", description = "Authenticate user and return token")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @PostMapping("/login")
    public Mono<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        log.info("API: Login user");
        return userService.login(request);
    }

    // GET USER
    @Operation(summary = "Get user by ID", description = "Fetch user details by ID")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping("/{id}")
    public Mono<UserResponse> getUserById(@PathVariable Long id) {

        log.info("API: Get user {}", id);
        return userService.getUserById(id);
    }

    // GET ALL USERS
    @Operation(summary = "Get all users", description = "Fetch all users")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @GetMapping
    public Flux<UserResponse> getAllUsers() {

        log.info("API: Get all users");
        return userService.getAllUsers();
    }

    // DELETE
    @Operation(summary = "Delete user", description = "Delete user by ID")
    @ApiResponse(responseCode = "204", description = "User deleted")
    @ApiResponse(responseCode = "404", description = "User not found")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteUser(@PathVariable Long id) {

        log.info("API: Delete user {}", id);
        return userService.deleteUser(id);
    }
}