package com.application.useCases.sutidores;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.application.core.BaseUseCasesWithParams;
import com.controllers.NovusUtils;
import com.google.gson.JsonObject;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.SurtidorRepository;

public class ObtenerCapacidadMaximaUseCase implements BaseUseCasesWithParams<Long, JsonObject> {
    
    private final EntityManagerFactory entityManagerFactory;

    public ObtenerCapacidadMaximaUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }
    
    @Override
    public JsonObject execute(Long tanque) {
        try {
            if (tanque == null || tanque <= 0) {
                NovusUtils.printLn("Error: El ID del tanque no puede ser nulo o menor/igual a cero");
                return new JsonObject();
            }

            EntityManager entityManager = entityManagerFactory.createEntityManager();
            SurtidorRepository surtidorRepository = new SurtidorRepository(entityManager);
            return surtidorRepository.obtenerCapacidadMaxima(tanque);
            
        } catch (Exception e) {
            NovusUtils.printLn("Error en ObtenerCapacidadMaximaUseCase: " + e.getMessage());
            return new JsonObject();
        }
    }
} 