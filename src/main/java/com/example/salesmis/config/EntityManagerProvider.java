package com.example.salesmis.config;

import jakarta.persistence.EntityManager;

public interface EntityManagerProvider {
    EntityManager createEntityManager();
}
