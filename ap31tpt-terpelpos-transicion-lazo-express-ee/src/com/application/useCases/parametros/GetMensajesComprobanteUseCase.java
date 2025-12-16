package com.application.useCases.parametros;

import com.application.core.BaseUseCases;
import com.google.gson.JsonObject;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.ParametrosRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para obtener los mensajes de comprobante electrónico.
 * Reemplaza: MovimientosDao.mensajesComprobante()
 * Usado en: ConfiguracionFE.
 */
public class GetMensajesComprobanteUseCase implements BaseUseCases<JsonObject> {

    private final EntityManagerFactory entityManagerFactory;

    public GetMensajesComprobanteUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public JsonObject execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            ParametrosRepository repository = new ParametrosRepository(em);
            return repository.obtenerMensajesComprobante();
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}

