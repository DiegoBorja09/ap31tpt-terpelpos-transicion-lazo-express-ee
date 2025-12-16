package com.application.useCases.transmisiones;

import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.TransmisionRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

public class InsertarTransmisionUseCase implements BaseUseCases<Boolean> {

    private final String autorizacion;
    private final EntityManagerFactory entityManagerFactory;

    public InsertarTransmisionUseCase(String autorizacion) {
        this.autorizacion = autorizacion;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Boolean execute() {
        EntityManager entityManager = null;
        EntityTransaction transaction = null;
        
        try {
            entityManager = entityManagerFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            
            TransmisionRepository transmisionRepository = new TransmisionRepository(entityManager);
            boolean resultado = transmisionRepository.insertarAutorizacion(autorizacion);
            
            transaction.commit();
            return resultado;
            
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("❌ Error al insertar transmisión/autorización: " + e.getMessage());
            return false;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
} 