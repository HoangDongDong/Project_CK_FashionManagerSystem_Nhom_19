package com.example.salesmis.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JpaUtil {
    private static final String PERSISTENCE_UNIT_NAME = "salesmisPU";
    private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);

    private JpaUtil() {}

    public static EntityManagerFactory getEntityManagerFactory() {
        return EMF;
    }

    public static EntityManager createEntityManager() {
        return EMF.createEntityManager();
    }

    public static void shutdown() {
        if (EMF.isOpen()) {
            EMF.close();
        }
    }
}

