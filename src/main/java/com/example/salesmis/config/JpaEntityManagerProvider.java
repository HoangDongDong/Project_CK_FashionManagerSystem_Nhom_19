package com.example.salesmis.config;

import jakarta.persistence.EntityManager;

public class JpaEntityManagerProvider implements EntityManagerProvider {
    @Override
    public EntityManager createEntityManager() {
        return JpaUtil.createEntityManager();
    }
}
