package com.application.useCases.sutidores;

import com.application.core.BaseUseCasesWithParams;
import com.bean.CrearPosVentaFEParams;
import com.infrastructure.repositories.SurtidorRepository;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;

public class CrearPosVentaFEUseCase implements BaseUseCasesWithParams<CrearPosVentaFEParams, Void> {
    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;
    private final SurtidorRepository surtidorRepository;

    public CrearPosVentaFEUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
            .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
        this.entityManager = entityManagerFactory.createEntityManager();
        this.surtidorRepository = new SurtidorRepository(entityManager);
    }

    @Override
    public Void execute(CrearPosVentaFEParams params) {
        try {
            entityManager.getTransaction().begin();
            surtidorRepository.crearPosVentaFE(params.getCara(), params.getManguera(), params.getClienteJson());
            entityManager.getTransaction().commit();
            return null;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al crear la venta de factura electrónica: " + e.getMessage(), e);
        } finally {
            close();
        }
    }

    // Método para limpiar recursos cuando ya no se necesite el caso de uso
    public void close() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }
}