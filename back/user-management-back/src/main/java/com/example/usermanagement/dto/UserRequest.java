package com.example.usermanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "User creation/update request")
public record UserRequest(
        @Schema(example = "Omar")
        @NotBlank(message = "firstName is required") @Size(max = 80) String firstName,

        @Schema(example = "Garcia")
        @NotBlank(message = "lastName is required") @Size(max = 80) String lastName,

        @Schema(example = "omar@example.com")
        @NotBlank(message = "email is required") @Email(message = "email must be valid") @Size(max = 160) String email,

        @Schema(example = "8112345678")
        @Size(max = 30) String phone,

        @Schema(example = "true", description = "Defaults to true when omitted")
        Boolean active
) {}
