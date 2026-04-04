package com.example.salesmis.config;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JpaUtil {
    private static final String PERSISTENCE_UNIT_NAME = "salesmisPU";
    private static final EntityManagerFactory EMF = createEntityManagerFactory();

    private JpaUtil() {}

    /**
     * Ghi de JDBC tu bien moi truong JVM (khong sua persistence.xml tren moi may).
     * Vi du: -Djakarta.persistence.jdbc.password=123456 -Djakarta.persistence.jdbc.user=root
     */
    private static EntityManagerFactory createEntityManagerFactory() {
        Map<String, String> overrides = new HashMap<>();
        putIfSystemProperty(overrides, "jakarta.persistence.jdbc.url");
        putIfSystemProperty(overrides, "jakarta.persistence.jdbc.user");
        putIfSystemProperty(overrides, "jakarta.persistence.jdbc.password");
        putIfSystemProperty(overrides, "jakarta.persistence.jdbc.driver");
        if (overrides.isEmpty()) {
            return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, overrides);
    }

    private static void putIfSystemProperty(Map<String, String> map, String key) {
        String value = System.getProperty(key);
        if (value != null && !value.isBlank()) {
            map.put(key, value);
        }
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

