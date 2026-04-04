package com.example.salesmis.service.exception;

public class InvalidVoucherException extends RuntimeException {
    public InvalidVoucherException(String message) {
        super(message);
    }
}
