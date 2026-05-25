package com.example.usermanagement.exception;

public class InvalidSortFieldException extends RuntimeException {
    public InvalidSortFieldException(String sortBy) {
        super("Invalid sort field: " + sortBy);
    }
}
