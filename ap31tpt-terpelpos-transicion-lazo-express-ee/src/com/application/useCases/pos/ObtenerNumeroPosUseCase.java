package com.application.useCases.pos;

import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.PosRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class ObtenerNumeroPosUseCase implements BaseUseCases<Integer> {

    private final EntityManagerFactory entityManagerFactory;

    public ObtenerNumeroPosUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Integer execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            PosRepository repository = new PosRepository(em);
            return repository.obtenerNumeroPos();
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}
