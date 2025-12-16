package com.application.useCases.consecutivos;

import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.ConsecutivoRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class ActualizarConsecutivoUseCase implements BaseUseCases<Boolean> {

    private final EntityManagerFactory entityManagerFactory;
    private final long id;
    private final long consecutivo;

    public ActualizarConsecutivoUseCase(long id, long consecutivo) {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
        this.id = id;
        this.consecutivo = consecutivo;
    }

    @Override
    public Boolean execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            ConsecutivoRepository repository = new ConsecutivoRepository(em);
            return repository.actualizarConsecutivo(id, consecutivo);
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}

