package com.example.usermanagement.service;

import com.example.usermanagement.dto.PageResponse;
import com.example.usermanagement.dto.UserRequest;
import com.example.usermanagement.dto.UserResponse;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<PageResponse<UserResponse>> findAll(String search, Pageable pageable);
    Mono<UserResponse> findById(Long id);
    Mono<UserResponse> create(UserRequest request, String actor);
    Mono<UserResponse> update(Long id, UserRequest request, String actor);
    Mono<Void> delete(Long id, String actor);
}
