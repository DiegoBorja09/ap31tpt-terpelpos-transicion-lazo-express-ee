package com.infrastructure.database.strategies;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class JpaPostgresStrategy {
 
    private static final String PERSISTENCE_UNIT_NAME = "dynamicPU";
    private EntityManagerFactory emf;
    private String dbName;

    public JpaPostgresStrategy(String dbName) {
        this.dbName = dbName;
    }
 
    public synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            Map<String, String> props = new HashMap<>();
            props.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
            props.put("javax.persistence.jdbc.url", "jdbc:postgresql://" + NovusConstante.HOST_CENTRAL_POINT + ":" + NovusConstante.POSGRES_PORT + "/" + dbName);
            props.put("javax.persistence.jdbc.user", NovusConstante.POSGRES_USER);
            props.put("javax.persistence.jdbc.password", NovusConstante.POSGRES_CONTRASENA);
            props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            props.put("hibernate.hbm2ddl.auto", "validate");
            props.put("hibernate.show_sql", "true");
            
            try {
                NovusUtils.printLn("Intentando crear EntityManagerFactory con URL: jdbc:postgresql://" + NovusConstante.HOST_CENTRAL_POINT + ":" + NovusConstante.POSGRES_PORT + "/" + dbName);
                emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, props);
                NovusUtils.printLn("EntityManagerFactory creado exitosamente");
            } catch (Exception e) {
                NovusUtils.printLn("Error creating EntityManagerFactory: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        }
        return emf;
    }
 
    public synchronized void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
 