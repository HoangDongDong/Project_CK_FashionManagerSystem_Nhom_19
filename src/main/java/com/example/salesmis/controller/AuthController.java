package com.example.salesmis.controller;

import com.example.salesmis.model.entity.Account;
import com.example.salesmis.service.AuthService;

public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public Account login(String username, String password) {
        return authService.authenticate(username, password);
    }
}
