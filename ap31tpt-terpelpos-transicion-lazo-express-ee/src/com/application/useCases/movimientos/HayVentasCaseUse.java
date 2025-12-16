package com.application.useCases.movimientos;

import com.application.core.BaseUseCases;
import com.google.gson.JsonObject;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.VentaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para verificar si hay ventas para un turno y responsable específicos.
 * Usado en: Validación de ventas por turno.
 */
public class HayVentasCaseUse implements BaseUseCases<JsonObject> {

    private final long turno;
    private final long idResponsable;
    private final EntityManagerFactory entityManagerFactory;

    public HayVentasCaseUse(long turno, long idResponsable) {
        this.turno = turno;
        this.idResponsable = idResponsable;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public JsonObject execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            VentaRepository repo = new VentaRepository(em);
            return repo.hayVentas(turno, idResponsable);
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}

