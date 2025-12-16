package com.infrastructure.database;
import com.infrastructure.database.strategies.JpaPostgresStrategy;
import javax.persistence.EntityManagerFactory;
import java.util.EnumMap;
import java.util.Map;


public enum JpaEntityManagerFactory {

    INSTANCE;

    private final Map<DatabaseNames, JpaPostgresStrategy> strategies;

    JpaEntityManagerFactory() {
        strategies = new EnumMap<>(DatabaseNames.class);
        for (DatabaseNames db : DatabaseNames.values()) {
            strategies.put(db, new JpaPostgresStrategy(db.getDbName()));
        }
    }

    // Método para mantener compatibilidad con código existente
    public static JpaEntityManagerFactory getInstance() {
        return INSTANCE;
    }

    // Get the EntityManagerFactory for a specific database
    public EntityManagerFactory getEntityManagerFactory(DatabaseNames dbName) {
        JpaPostgresStrategy strategy = strategies.get(dbName);
        if (strategy == null) {
            throw new IllegalArgumentException("No JPA strategy found for database: " + dbName);
        }
        return strategy.getEntityManagerFactory();
    }

    // Método para cerrar todas las conexiones cuando sea necesario
    public void closeAllConnections() {
        for (JpaPostgresStrategy strategy : strategies.values()) {
            if (strategy != null) {
                strategy.close();
            }
        }
    }

//    // Close the EntityManagerFactory for a specific database
//    public void closeEntityManagerFactory(DatabaseNames dbName) {
//        JpaPostgresStrategy strategy = strategies.get(dbName);
//        if (strategy != null) {
//            strategy.close();
//        }
//    }
}