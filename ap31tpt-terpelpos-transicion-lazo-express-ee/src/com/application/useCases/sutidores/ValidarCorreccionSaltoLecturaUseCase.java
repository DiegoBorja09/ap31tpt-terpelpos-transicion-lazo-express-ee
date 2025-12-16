package com.application.useCases.sutidores;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.application.core.BaseUseCasesWithParams;
import com.controllers.NovusUtils;
import com.google.gson.JsonObject;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.SurtidorRepository;

public class ValidarCorreccionSaltoLecturaUseCase implements BaseUseCasesWithParams<JsonObject, Boolean> {
    
    private final EntityManagerFactory entityManagerFactory;

    public ValidarCorreccionSaltoLecturaUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }
    
    @Override
    public Boolean execute(JsonObject detailHose) {
        try {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            SurtidorRepository surtidorRepository = new SurtidorRepository(entityManager);
            return surtidorRepository.validarCorreccionSaltoLectura(detailHose);
            
        } catch (Exception e) {
            NovusUtils.printLn("Error en ValidarCorreccionSaltoLecturaUseCase: " + e.getMessage());
            return true; // Por defecto asumimos que está corregido en caso de error
        }
    }
} 