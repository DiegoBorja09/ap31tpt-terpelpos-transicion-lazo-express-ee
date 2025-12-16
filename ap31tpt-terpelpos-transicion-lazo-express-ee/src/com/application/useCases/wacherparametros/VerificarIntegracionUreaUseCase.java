package com.application.useCases.wacherparametros;

import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.CtWacherParametroRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class VerificarIntegracionUreaUseCase implements BaseUseCases<Boolean> {

    private final EntityManagerFactory entityManagerFactory;

    public VerificarIntegracionUreaUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Boolean execute() {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CtWacherParametroRepository ctWacherParametroRepository = new CtWacherParametroRepository(entityManager);
            return ctWacherParametroRepository.verificarIntegracionUrea();
        } catch (Exception e) {
            System.err.println("❌ Error al verificar integración UREA: " + e.getMessage());
            return false;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
} 