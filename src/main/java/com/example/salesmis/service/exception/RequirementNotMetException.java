package com.example.salesmis.service.exception;

public class RequirementNotMetException extends RuntimeException {
    public RequirementNotMetException(String message) {
        super(message);
    }
}
