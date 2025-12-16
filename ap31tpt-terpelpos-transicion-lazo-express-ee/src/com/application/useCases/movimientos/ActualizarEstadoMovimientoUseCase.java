package com.application.useCases.movimientos;

import com.application.core.BaseUseCases;
import com.controllers.NovusUtils;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.CtMovimientoRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Use Case para actualizar el estado de un movimiento a anulado (017002).
 * Migración JPA del método actualizarEstadoMovimiento() de RumboDao.
 */
public class ActualizarEstadoMovimientoUseCase implements BaseUseCases<Boolean> {

    private final EntityManagerFactory entityManagerFactory;
    private final long movimientoId;

    public ActualizarEstadoMovimientoUseCase(long movimientoId) {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE.getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
        this.movimientoId = movimientoId;
    }

    @Override
    public Boolean execute() {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            
            CtMovimientoRepository ctMovimientoRepository = new CtMovimientoRepository(entityManager);
            
            boolean actualizado = ctMovimientoRepository.actualizarEstadoMovimiento(this.movimientoId);
            
            entityManager.getTransaction().commit();
            
            if (actualizado) {
                NovusUtils.printLn("✅ Estado del movimiento " + this.movimientoId + " actualizado a anulado (017002)");
            } else {
                NovusUtils.printLn("⚠️ No se pudo actualizar el estado del movimiento " + this.movimientoId);
            }
            
            return actualizado;
            
        } catch (Exception e) {
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            NovusUtils.printLn("❌ Error actualizando estado del movimiento " + this.movimientoId + ": " + e.getMessage());
            return false;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
} 