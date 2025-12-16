package com.application.useCases.jornada;

import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.repositories.JornadaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
/**
 * Caso de uso para obtener el ID del grupo de jornada asociado a una persona.
 * Reemplaza: MovimientosDao.getMyJornadaIdCore(long id)
 * Usado en: KCOSExpressServices
 */


public class GetJornadaIdByPersonaUseCase {

    private final EntityManagerFactory entityManagerFactory;

    public GetJornadaIdByPersonaUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    public long execute(Long personaId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            JornadaRepository repository = new JornadaRepository(em);
            return repository.findGrupoJornadaByPersonaId(personaId);
        } catch (Exception ex) {
            throw new RuntimeException("Error al obtener grupo_jornada", ex);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}

