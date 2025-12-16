package com.application.useCases.movimientos;

import com.application.core.BaseUseCases;
import com.controllers.NovusUtils;
import com.google.gson.JsonObject;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.CtMovimientoRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Use Case para actualizar movimientos CT usando función PostgreSQL
 * Migración JPA del método actualizarMovimientos() de RumboDao
 */
public class ActualizarCtMovimientosUseCase implements BaseUseCases<Boolean> {

    private final EntityManagerFactory entityManagerFactory;
    private final JsonObject data;

    public ActualizarCtMovimientosUseCase(JsonObject data) {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
        this.data = data;
    }

    @Override
    public Boolean execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            CtMovimientoRepository ctMovimientoRepository = new CtMovimientoRepository(em);
            return ctMovimientoRepository.actualizarCtMovimientos(data);
        } catch (Exception e) {
            NovusUtils.printLn("Error actualizando CT movimientos: " + e.getMessage());
            return false;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
} 