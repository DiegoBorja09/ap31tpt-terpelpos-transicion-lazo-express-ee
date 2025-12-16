package com.application.useCases.sutidores;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.application.core.BaseUseCasesWithParams;
import com.controllers.NovusUtils;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.SurtidorRepository;

public class BorrarRecepcionUseCase implements BaseUseCasesWithParams<Long, Void> {
    
    private final EntityManagerFactory entityManagerFactory;

    public BorrarRecepcionUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }
    
    @Override
    public Void execute(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        SurtidorRepository surtidorRepository = new SurtidorRepository(entityManager);
        
        try {
            if (id == null || id <= 0) {
                NovusUtils.printLn("Error: El ID de la recepción no puede ser nulo o menor/igual a cero");
                return null;
            }

            entityManager.getTransaction().begin();
            surtidorRepository.borrarRecepcion(id);
            entityManager.getTransaction().commit();
            
        } catch (Exception e) {
            NovusUtils.printLn("Error en BorrarRecepcionUseCase: " + e.getMessage());
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