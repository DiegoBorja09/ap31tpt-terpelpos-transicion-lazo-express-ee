package com.application.useCases.ventas;

import com.application.core.BaseUseCases;
import com.google.gson.JsonArray;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.VentaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para consultar las ventas cliente desde la función fnc_conseguir_ventas_cliente().
 * Reemplaza: MovimientosDao.buscarVentaCliente()
 * Usado en: asignarCliente, AsignacionclienteRemision.
 */
public class BuscarVentasClienteUseCase implements BaseUseCases<JsonArray> {

    private final EntityManagerFactory entityManagerFactory;

    public BuscarVentasClienteUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public JsonArray execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            VentaRepository repository = new VentaRepository(em);
            return repository.buscarVentaCliente();
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}

