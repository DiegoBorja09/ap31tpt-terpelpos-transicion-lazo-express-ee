package com.application.useCases.jornadas;

import com.application.core.BaseUseCases;
import com.controllers.NovusUtils;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.JornadaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Use Case para obtener el ID de jornada (grupo_jornada)
 * Migración JPA del método jornadaId() de RumboDao
 */
public class ObtenerJornadaIdUseCase implements BaseUseCases<Long> {

    private final EntityManagerFactory entityManagerFactory;

    public ObtenerJornadaIdUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Long execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            JornadaRepository jornadaRepository = new JornadaRepository(em);
            return jornadaRepository.obtenerJornadaId();
        } catch (Exception e) {
            NovusUtils.printLn("Error al obtener jornada ID: " + e.getMessage());
            return 0L;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
} 