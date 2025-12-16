package com.application.useCases.movimientos;

import com.google.gson.JsonObject;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.repositories.CtMovimientoRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Consulta cliente transmisión a través de función SQL
 * Reemplaza: MovimientosDao.consultaClienteMovimientoTransmision()
 */

public class ConsultarClienteMovimientoTransmisionUseCase {

    private final EntityManagerFactory emf;

    public ConsultarClienteMovimientoTransmisionUseCase() {
        this.emf = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
    }

    public JsonObject execute(Long idMovimiento) {
        EntityManager em = emf.createEntityManager();
        try {
            CtMovimientoRepository repo = new CtMovimientoRepository(em);
            return repo.findClienteTransmisionById(idMovimiento);
        } finally {
            em.close();
        }
    }
}

