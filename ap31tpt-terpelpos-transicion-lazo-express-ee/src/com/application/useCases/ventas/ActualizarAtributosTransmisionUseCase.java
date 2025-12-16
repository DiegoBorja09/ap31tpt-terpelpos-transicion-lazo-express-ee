package com.application.useCases.ventas;

import com.application.core.BaseUseCases;
import com.google.gson.JsonObject;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.VentaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para actualizar los atributos (campo request) de una transmisión de venta.
 * Reemplaza: MovimientosDao.actualizarTransmisionAtributosVenta()
 * Usado en: ReenviodeFE.
 */
public class ActualizarAtributosTransmisionUseCase {

    private final Long idTransmision;
    private final JsonObject json;

    public ActualizarAtributosTransmisionUseCase(Long idTransmision, JsonObject json) {
        this.idTransmision = idTransmision;
        this.json = json;
    }

    public void execute() {
        EntityManagerFactory emf = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
        EntityManager em = emf.createEntityManager();

        try {
            // ✅ Iniciar transacción
            em.getTransaction().begin();
            
            VentaRepository repository = new VentaRepository(em);
            repository.actualizarTransmisionAtributosVenta(idTransmision, json);
            
            // ✅ Confirmar transacción
            em.getTransaction().commit();
        } catch (Exception e) {
            // ✅ Rollback en caso de error
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al actualizar atributos de transmisión", e);
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}

