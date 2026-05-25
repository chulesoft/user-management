package com.example.usermanagement.exception;

import com.example.usermanagement.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleNotFound(UserNotFoundException ex, ServerWebExchange exchange) {
        return Mono.just(error(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND, ex.getMessage(), exchange, List.of()));
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDuplicateEmail(DuplicateEmailException ex, ServerWebExchange exchange) {
        return Mono.just(error(HttpStatus.CONFLICT, ErrorCode.USER_EMAIL_ALREADY_EXISTS, ex.getMessage(), exchange, List.of()));
    }

    @ExceptionHandler(InvalidSortFieldException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInvalidSort(InvalidSortFieldException ex, ServerWebExchange exchange) {
        return Mono.just(error(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_SORT_FIELD, ex.getMessage(), exchange, List.of()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidation(MethodArgumentNotValidException ex, ServerWebExchange exchange) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();
        return Mono.just(error(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR, "Validation failed", exchange, details));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleConstraintViolation(ConstraintViolationException ex, ServerWebExchange exchange) {
        List<String> details = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage()).toList();
        return Mono.just(error(HttpStatus.BAD_REQUEST, ErrorCode.CONSTRAINT_VIOLATION, "Constraint violation", exchange, details));
    }

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleWebInput(ServerWebInputException ex, ServerWebExchange exchange) {
        return Mono.just(error(HttpStatus.BAD_REQUEST, ErrorCode.CONSTRAINT_VIOLATION, "Constraint violation", exchange, List.of(ex.getReason())));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleIllegalArgument(IllegalArgumentException ex, ServerWebExchange exchange) {
        return Mono.just(error(HttpStatus.BAD_REQUEST, ErrorCode.CONSTRAINT_VIOLATION, ex.getMessage(), exchange, List.of()));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneric(Exception ex, ServerWebExchange exchange) {
        Throwable cause = ex.getCause();
        if (cause instanceof IllegalArgumentException) {
            return Mono.just(error(HttpStatus.BAD_REQUEST, ErrorCode.CONSTRAINT_VIOLATION, cause.getMessage(), exchange, List.of()));
        }
        return Mono.just(error(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR, "Unexpected error", exchange, List.of(ex.getClass().getSimpleName())));
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, ErrorCode code, String message, ServerWebExchange exchange, List<String> details) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                code.name(),
                message,
                exchange.getRequest().getPath().value(),
                details
        );
        return ResponseEntity.status(status).body(body);
    }
}
