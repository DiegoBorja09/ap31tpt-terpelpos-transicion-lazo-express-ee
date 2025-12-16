package com.application.useCases.sutidores;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.application.core.BaseUseCasesWithParams;
import com.controllers.NovusUtils;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.SurtidorRepository;

public class SetEstadoMovimientoUseCase implements BaseUseCasesWithParams<Long, Void> {
    
    private final EntityManagerFactory entityManagerFactory;

    public SetEstadoMovimientoUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }
    
    @Override
    public Void execute(Long id) {
        try {
            if (id == null || id <= 0) {
                NovusUtils.printLn("Error: El ID del movimiento no puede ser nulo o menor/igual a cero");
                return null;
            }

            EntityManager entityManager = entityManagerFactory.createEntityManager();
            SurtidorRepository surtidorRepository = new SurtidorRepository(entityManager);
            surtidorRepository.setEstadoMovimiento(id);
            
        } catch (Exception e) {
            NovusUtils.printLn("Error en SetEstadoMovimientoUseCase: " + e.getMessage());
        }
        return null;
    }
} 