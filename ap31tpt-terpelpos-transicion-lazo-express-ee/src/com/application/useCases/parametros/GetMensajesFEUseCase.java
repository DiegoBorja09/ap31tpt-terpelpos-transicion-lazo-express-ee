package com.application.useCases.parametros;

import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.ParametrosRepository;
import com.google.gson.JsonObject;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para obtener los mensajes FE desde la tabla wacher_parametros.
 * Reemplaza: mensajesFE()
 * Usado en: Controladores o servicios que requieren mostrar los mensajes de comprobantes fiscales electrónicos.
 */
public class GetMensajesFEUseCase implements BaseUseCases<JsonObject> {

    private final EntityManagerFactory entityManagerFactory;

    public GetMensajesFEUseCase() {
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

