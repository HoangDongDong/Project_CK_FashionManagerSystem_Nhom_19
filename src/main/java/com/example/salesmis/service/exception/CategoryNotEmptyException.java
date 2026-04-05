package com.example.salesmis.service.exception;

public class CategoryNotEmptyException extends RuntimeException {
    public CategoryNotEmptyException(String message) {
        super(message);
    }
}
