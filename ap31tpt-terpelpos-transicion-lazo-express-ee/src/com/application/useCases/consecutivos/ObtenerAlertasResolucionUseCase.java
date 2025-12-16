package com.application.useCases.consecutivos;

import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.ConsecutivoRepository;
import com.google.gson.JsonObject;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class ObtenerAlertasResolucionUseCase implements BaseUseCases<JsonObject> {

    private final int tipoDocumento;
    private final String destino;
    private final EntityManagerFactory entityManagerFactory;

    public ObtenerAlertasResolucionUseCase(int tipoDocumento, String destino) {
        this.tipoDocumento = tipoDocumento;
        this.destino = destino;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
    }

    @Override
    public JsonObject execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            ConsecutivoRepository repository = new ConsecutivoRepository(em);
            return repository.obtenerAlertasResolucion(tipoDocumento, destino);
        } finally {
            if (em.isOpen()) em.close();
        }
    }
} 