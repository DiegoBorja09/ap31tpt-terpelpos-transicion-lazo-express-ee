package com.application.useCases.sutidores;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.application.core.BaseUseCasesWithParams;
import com.controllers.NovusUtils;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.SurtidorRepository;

public class HabilitarBotonesFamiliaAutorizacionUseCase implements BaseUseCasesWithParams<String, Boolean> {
    
    private final EntityManagerFactory entityManagerFactory;

    public HabilitarBotonesFamiliaAutorizacionUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }
    
    @Override
    public Boolean execute(String familia) {
        try {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            SurtidorRepository surtidorRepository = new SurtidorRepository(entityManager);
            return surtidorRepository.habilitarBotonesFamiliaAutorizacion(familia);
            
        } catch (Exception e) {
            NovusUtils.printLn("Error en HabilitarBotonesFamiliaAutorizacionUseCase: " + e.getMessage());
            return false;
        }
    }
} 