package com.application.useCases.transmisiones;

import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.TransmisionRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

/**
 * Use Case para insertar confirmación de venta App
 * NOTA: Actualmente no se usa en el código, pero se mantiene preparado para futuro uso
 * Reemplaza el método insertarConfirmacionVentaApp() de RumboDao
 */
public class InsertarConfirmacionVentaAppUseCase implements BaseUseCases<Boolean> {

    private final String autorizacion;
    private final EntityManagerFactory entityManagerFactory;

    public InsertarConfirmacionVentaAppUseCase(String autorizacion) {
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
            boolean resultado = transmisionRepository.insertarConfirmacionVentaApp(autorizacion);
            
            transaction.commit();
            return resultado;
            
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("❌ Error al insertar confirmación de venta App: " + e.getMessage());
            return false;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
} 