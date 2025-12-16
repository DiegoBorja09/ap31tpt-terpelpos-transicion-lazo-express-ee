package com.application.useCases.wacherparametros;

import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.CtWacherParametroRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class ObtenerCodigoEstacionUseCase implements BaseUseCases<String> {

    private final EntityManagerFactory entityManagerFactory;

    public ObtenerCodigoEstacionUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public String execute() {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CtWacherParametroRepository ctWacherParametroRepository = new CtWacherParametroRepository(entityManager);
            return ctWacherParametroRepository.obtenerCodigoEstacion();
        } catch (Exception e) {
            System.err.println("❌ Error al obtener código de estación: " + e.getMessage());
            return null;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
} 