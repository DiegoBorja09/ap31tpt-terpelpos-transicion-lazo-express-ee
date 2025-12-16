package com.application.useCases.sutidores;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.application.core.BaseUseCasesWithParams;
import com.controllers.NovusUtils;
import com.google.gson.JsonObject;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.SurtidorRepository;

public class ActualizarNovedadSaltoLecturaUseCase implements BaseUseCasesWithParams<JsonObject, Void> {
    
    private final EntityManagerFactory entityManagerFactory;

    public ActualizarNovedadSaltoLecturaUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }
    
    @Override
    public Void execute(JsonObject detailHose) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            SurtidorRepository surtidorRepository = new SurtidorRepository(entityManager);
            surtidorRepository.actualizarNovedadSaltoLectura(detailHose);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            NovusUtils.printLn("Error en ActualizarNovedadSaltoLecturaUseCase: " + e.getMessage());
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return null;
    }
} 