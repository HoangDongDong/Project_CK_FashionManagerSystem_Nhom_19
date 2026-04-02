package com.example.salesmis.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import com.example.salesmis.model.entity.Account;

import jakarta.persistence.EntityManager;

public interface AccountRepository extends GenericRepository<Account, Integer> {
    Optional<Account> findByUsername(EntityManager entityManager, String username);
    void updateLastLogin(EntityManager entityManager, Integer accountId, LocalDateTime lastLogin);
}
