package com.application.useCases.wacherparametros;

import com.application.core.BaseUseCases;
import com.domain.entities.CtWacherParametroEntity;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.CtWacherParametroRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class FindAllWacherParametrosUseCase implements BaseUseCases<List<CtWacherParametroEntity>> {

    private final EntityManagerFactory entityManagerFactory;

    public FindAllWacherParametrosUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public List<CtWacherParametroEntity> execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            CtWacherParametroRepository ctWacherParametroRepository = new CtWacherParametroRepository(entityManager);
            return ctWacherParametroRepository.findAll();
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}
