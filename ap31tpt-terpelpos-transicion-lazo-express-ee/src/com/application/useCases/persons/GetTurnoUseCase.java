package com.application.useCases.persons;

import com.bean.PersonaBean;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.PersonaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para obtener el turno de una persona.
 * Reemplaza: PersonasDao.getTurno(long idPersona)
 * Usado en: [Especificar dónde se usa, si se conoce]
 */
public class GetTurnoUseCase {

    private final EntityManagerFactory entityManagerFactory;

    public GetTurnoUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    /**
     * Ejecuta el caso de uso para obtener el turno de una persona.
     * @param idPersona El ID de la persona.
     * @return PersonaBean con los datos del turno, o null si no se encuentra.
     */
    public PersonaBean execute(long idPersona) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            PersonaRepository repository = new PersonaRepository(em);
            return repository.getTurnoRepository(idPersona);
        } catch (Exception ex) {
            throw new RuntimeException("Error al obtener el turno de la persona", ex);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
