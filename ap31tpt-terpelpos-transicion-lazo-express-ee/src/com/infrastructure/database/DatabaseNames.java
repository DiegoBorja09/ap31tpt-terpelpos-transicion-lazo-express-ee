package com.infrastructure.database;

public enum DatabaseNames {
    LAZOEXPRESSREGISTRY("lazoexpressregistry"),
    LAZOEXPRESSCORE("lazoexpresscore"),
    PRUEBA("Prueba");
    private final String dbName;
    DatabaseNames(String dbName) {
        this.dbName = dbName;
    }
    public String getDbName() {
        return dbName;
    }
} 