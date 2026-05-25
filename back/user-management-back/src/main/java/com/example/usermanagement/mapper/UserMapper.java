package com.example.usermanagement.mapper;

import com.example.usermanagement.dto.PageResponse;
import com.example.usermanagement.dto.UserRequest;
import com.example.usermanagement.dto.UserResponse;
import com.example.usermanagement.model.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toNewEntity(UserRequest request) {
        return User.builder()
                .firstName(trim(request.firstName()))
                .lastName(trim(request.lastName()))
                .email(normalizeEmail(request.email()))
                .phone(normalizePhone(request.phone()))
                .active(request.active() == null || request.active())
                .deleted(false)
                .build();
    }

    public void applyUpdates(User user, UserRequest request) {
        user.setFirstName(trim(request.firstName()));
        user.setLastName(trim(request.lastName()));
        user.setEmail(normalizeEmail(request.email()));
        user.setPhone(normalizePhone(request.phone()));
        user.setActive(request.active() == null || request.active());
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.isActive(),
                user.isDeleted(),
                user.getVersion(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getCreatedBy(),
                user.getUpdatedBy()
        );
    }

    public PageResponse<UserResponse> toPageResponse(Page<User> page, int pageNumber, int size) {
        return new PageResponse<>(
                page.getContent().stream().map(this::toResponse).toList(),
                pageNumber,
                size,
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    private String normalizeEmail(String email) {
        return trim(email).toLowerCase();
    }

    private String normalizePhone(String phone) {
        if (phone == null) return null;
        String t = phone.trim();
        return t.isBlank() ? null : t;
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
