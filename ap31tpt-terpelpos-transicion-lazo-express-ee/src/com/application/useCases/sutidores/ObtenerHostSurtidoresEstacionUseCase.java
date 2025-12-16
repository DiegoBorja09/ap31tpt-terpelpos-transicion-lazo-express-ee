package com.application.useCases.sutidores;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.application.core.BaseUseCases;
import com.controllers.NovusUtils;
import com.google.gson.JsonArray;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.SurtidorRepository;

public class ObtenerHostSurtidoresEstacionUseCase implements BaseUseCases<JsonArray> {
    
    private final EntityManagerFactory entityManagerFactory;

    public ObtenerHostSurtidoresEstacionUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }
    
    @Override
    public JsonArray execute() {
        try {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            SurtidorRepository surtidorRepository = new SurtidorRepository(entityManager);
            return surtidorRepository.obtenerHostSurtidoresEstacion();
            
        } catch (Exception e) {
            NovusUtils.printLn("Error en ObtenerHostSurtidoresEstacionUseCase: " + e.getMessage());
            return new JsonArray();
        }
    }
} 