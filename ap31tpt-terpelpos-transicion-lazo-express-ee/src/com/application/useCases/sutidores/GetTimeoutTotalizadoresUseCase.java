package com.application.useCases.sutidores;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.application.core.BaseUseCases;
import com.controllers.NovusUtils;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.SurtidorRepository;

public class GetTimeoutTotalizadoresUseCase implements BaseUseCases<Integer> {
    
    private final EntityManagerFactory entityManagerFactory;

    public GetTimeoutTotalizadoresUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }
    
    @Override
    public Integer execute() {
        try {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            SurtidorRepository surtidorRepository = new SurtidorRepository(entityManager);
            return surtidorRepository.getTimeoutTotalizadores();
            
        } catch (Exception e) {
            NovusUtils.printLn("Error en GetTimeoutTotalizadoresUseCase: " + e.getMessage());
            return 30000;
        }
    }
} 