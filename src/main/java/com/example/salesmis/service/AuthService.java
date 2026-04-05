package com.example.salesmis.service;

import com.example.salesmis.model.entity.Account;

public interface AuthService {
    Account authenticate(String username, String password);
    void logout(String username);
}
