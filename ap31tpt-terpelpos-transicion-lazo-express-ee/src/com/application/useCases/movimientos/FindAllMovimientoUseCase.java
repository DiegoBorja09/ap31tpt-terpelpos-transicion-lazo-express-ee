package com.application.useCases.movimientos;

import com.application.core.BaseUseCases;
import com.domain.entities.CtMovimientoEntity;
import com.infrastructure.repositories.CtMovimientoRepository;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.database.DatabaseNames;
import java.util.List;

public class FindAllMovimientoUseCase implements BaseUseCases<List<CtMovimientoEntity>> {

    private final EntityManagerFactory entityManagerFactory;

    public FindAllMovimientoUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public List<CtMovimientoEntity> execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        
        try {
            CtMovimientoRepository ctMovimientoRepository = new CtMovimientoRepository(entityManager);
            return ctMovimientoRepository.findAll();
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}
