package com.application.useCases.movimientos;
import com.application.core.BaseUseCases;
import com.google.gson.JsonObject;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.VentaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para obtener los datos de una venta pendiente de datafono.
 * Usado en: Validación de transacciones de datafono pendientes.
 */
public class ObtenerDatosVentaPendienteDatafonoUseCase implements BaseUseCases<JsonObject> {

    private final long idTransaccionDatafono;
    private final EntityManagerFactory entityManagerFactory;

    public ObtenerDatosVentaPendienteDatafonoUseCase(long idTransaccionDatafono) {
        this.idTransaccionDatafono = idTransaccionDatafono;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public JsonObject execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            VentaRepository repo = new VentaRepository(em);
            return repo.obtenerDatosVentaPendienteDatafono(idTransaccionDatafono);
        } finally {
            if (em.isOpen()) em.close();
        }
    }

}