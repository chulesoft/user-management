package com.example.usermanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "User response")
public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        boolean active,
        boolean deleted,
        Long version,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String createdBy,
        String updatedBy
) {}
