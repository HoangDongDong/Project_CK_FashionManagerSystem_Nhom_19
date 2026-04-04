package com.example.salesmis.service.impl;

import java.time.LocalDateTime;

import com.example.salesmis.config.EntityManagerProvider;
import com.example.salesmis.model.entity.Account;
import com.example.salesmis.repository.AccountRepository;
import com.example.salesmis.service.AuthService;
import com.example.salesmis.service.exception.AuthenticationException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class AuthServiceImpl implements AuthService {
    private final AccountRepository accountRepository;
    private final EntityManagerProvider entityManagerProvider;

    public AuthServiceImpl(AccountRepository accountRepository, EntityManagerProvider entityManagerProvider) {
        this.accountRepository = accountRepository;
        this.entityManagerProvider = entityManagerProvider;
    }

    @Override
    public Account authenticate(String username, String password) {
        EntityManager entityManager = entityManagerProvider.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            Account account = accountRepository.findByUsername(entityManager, username)
                    .orElseThrow(() -> new AuthenticationException("Sai ten dang nhap hoac mat khau"));

            if (account.getIsActive() != null && !account.getIsActive()) {
                throw new AuthenticationException("Tai khoan da bi khoa");
            }

            String storedHash = account.getPasswordHash();
            if (storedHash == null || !storedHash.equals(password)) {
                throw new AuthenticationException("Sai ten dang nhap hoac mat khau");
            }

            // Cap nhat last_login tren entity dang managed (tranh loi JPQL UPDATE / cot thieu).
            account.setLastLogin(LocalDateTime.now());
            transaction.commit();
            return account;
        } catch (RuntimeException ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw ex;
        } finally {
            entityManager.close();
        }
    }
}
