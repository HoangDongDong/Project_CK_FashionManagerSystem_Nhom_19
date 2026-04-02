package com.example.salesmis.repository.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import com.example.salesmis.model.entity.Account;
import com.example.salesmis.repository.AbstractJpaRepository;
import com.example.salesmis.repository.AccountRepository;

import jakarta.persistence.EntityManager;

public class AccountRepositoryImpl extends AbstractJpaRepository<Account, Integer> implements AccountRepository {
    public AccountRepositoryImpl() {
        super(Account.class);
    }

    @Override
    public Optional<Account> findByUsername(EntityManager entityManager, String username) {
        return entityManager.createQuery("SELECT a FROM Account a WHERE a.username = :username", Account.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

    @Override
    public void updateLastLogin(EntityManager entityManager, Integer accountId, LocalDateTime lastLogin) {
        entityManager.createQuery("UPDATE Account a SET a.lastLogin = :lastLogin WHERE a.accountId = :accountId")
                .setParameter("lastLogin", lastLogin)
                .setParameter("accountId", accountId)
                .executeUpdate();
    }
}
