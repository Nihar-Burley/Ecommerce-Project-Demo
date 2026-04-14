package com.company.common.controller.user;


import com.company.common.dto.user.request.LoginRequest;
import com.company.common.dto.user.request.RegisterRequest;
import com.company.common.dto.user.response.LoginResponse;
import com.company.common.dto.user.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RequestMapping("/api/v1/users")
@Tag(name = "User API", description = "Operations related to Users")
public interface UserController {

    // REGISTER
    @Operation(summary = "Register user", description = "Create a new user")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    Mono<UserResponse> register(
            @Valid @RequestBody RegisterRequest request);

    // LOGIN
    @Operation(summary = "Login user", description = "Authenticate user and return token")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @PostMapping("/login")
    Mono<LoginResponse> login(
            @Valid @RequestBody LoginRequest request);

    // GET USER
    @Operation(summary = "Get user by ID", description = "Fetch user details by ID")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    Mono<UserResponse> getUserById(@PathVariable Long id);

    // GET ALL USERS
    @Operation(summary = "Get all users", description = "Fetch all users")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    Flux<UserResponse> getAllUsers();

    // DELETE
    @Operation(summary = "Delete user", description = "Delete user by ID")
    @ApiResponse(responseCode = "204", description = "User deleted")
    @ApiResponse(responseCode = "404", description = "User not found")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    Mono<Void> deleteUser(@PathVariable Long id);

    @Operation(summary = "Approve user", description = "Admin approves a registered user")
    @ApiResponse(responseCode = "200", description = "User approved successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    Mono<UserResponse> approveUser(
            @PathVariable Long id);
}