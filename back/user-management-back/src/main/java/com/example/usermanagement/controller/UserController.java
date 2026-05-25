package com.example.usermanagement.controller;

import com.example.usermanagement.dto.PageResponse;
import com.example.usermanagement.dto.UserRequest;
import com.example.usermanagement.dto.UserResponse;
import com.example.usermanagement.exception.InvalidSortFieldException;
import com.example.usermanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Set;

@Tag(name = "Users", description = "User Management CRUD endpoints")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "firstName", "lastName", "email", "active", "createdAt", "updatedAt"
    );

    private final UserService userService;

    @Operation(summary = "Get users using pagination (optional search)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public Mono<ResponseEntity<PageResponse<UserResponse>>> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction) {

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            return Mono.error(new InvalidSortFieldException(sortBy));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return userService.findAll(search, pageable).map(ResponseEntity::ok);
    }

    @Operation(summary = "Get user by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> findById(@PathVariable Long id) {
        return userService.findById(id).map(ResponseEntity::ok);
    }

    @Operation(summary = "Create user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "Duplicate email"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public Mono<ResponseEntity<UserResponse>> create(@Valid @RequestBody UserRequest request, Principal principal) {
        String actor = principal == null ? "anonymous" : principal.getName();
        return userService.create(request, actor)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @Operation(summary = "Update user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Duplicate email"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> update(@PathVariable Long id, @Valid @RequestBody UserRequest request, Principal principal) {
        String actor = principal == null ? "anonymous" : principal.getName();
        return userService.update(id, request, actor).map(ResponseEntity::ok);
    }

    @Operation(summary = "Soft delete user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id, Principal principal) {
        String actor = principal == null ? "anonymous" : principal.getName();
        return userService.delete(id, actor).thenReturn(ResponseEntity.noContent().build());
    }
}
