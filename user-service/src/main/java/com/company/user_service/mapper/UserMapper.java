package com.company.user_service.mapper;

import com.company.user_service.dto.response.UserResponse;
import com.company.user_service.entity.User;

public class UserMapper {

    // Entity → Response DTO
    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}