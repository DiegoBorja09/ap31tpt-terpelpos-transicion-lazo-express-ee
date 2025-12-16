package com.application.useCases.sutidores;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.application.core.BaseUseCasesWithParams;
import com.controllers.NovusUtils;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.SurtidorRepository;

public class FueFidelizadaUseCase implements BaseUseCasesWithParams<Long, Boolean> {
    
    private final EntityManagerFactory entityManagerFactory;

    public FueFidelizadaUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }
    
    @Override
    public Boolean execute(Long idMovimiento) {
        try {
            if (idMovimiento == null || idMovimiento <= 0) {
                NovusUtils.printLn("Error: El ID del movimiento debe ser mayor a cero");
                return false;
            }

            EntityManager entityManager = entityManagerFactory.createEntityManager();
            SurtidorRepository surtidorRepository = new SurtidorRepository(entityManager);
            return surtidorRepository.fueFidelizada(idMovimiento);
            
        } catch (Exception e) {
            NovusUtils.printLn("Error en FueFidelizadaUseCase: " + e.getMessage());
            return false;
        }
    }
} 