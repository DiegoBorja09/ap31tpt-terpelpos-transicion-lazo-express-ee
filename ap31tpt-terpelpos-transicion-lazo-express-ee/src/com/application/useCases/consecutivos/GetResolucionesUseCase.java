package com.application.useCases.consecutivos;

import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.ConsecutivoRepository;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class GetResolucionesUseCase implements BaseUseCases<String> {

    private final Long consecutivoId;
    private final EntityManagerFactory entityManagerFactory;

    public GetResolucionesUseCase(Long consecutivoId) {
        this.consecutivoId = consecutivoId;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
    }

    @Override
    public String execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            ConsecutivoRepository repository = new ConsecutivoRepository(em);
            return repository.obtenerObservaciones(consecutivoId);
        } finally {
            if (em.isOpen())
                em.close();
        }
    }
}