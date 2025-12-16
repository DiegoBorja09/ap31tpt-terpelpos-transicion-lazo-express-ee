package com.application.useCases.remision;

import com.application.core.BaseUseCases;
import com.google.gson.JsonObject;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.RemisionRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class GetMontoMinimoRemisionUseCase implements BaseUseCases<JsonObject> {

    private final EntityManagerFactory entityManagerFactory;

    public GetMontoMinimoRemisionUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public JsonObject execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            RemisionRepository repo = new RemisionRepository(em);
            return repo.obtenerObligatoriedadRemision();
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}

