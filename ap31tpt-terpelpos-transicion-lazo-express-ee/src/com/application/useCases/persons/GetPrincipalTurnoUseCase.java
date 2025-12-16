package com.application.useCases.persons;

import com.bean.PersonaBean;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.PersonaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para obtener el turno principal de una persona.
 * Reemplaza: PersonasDao.getPrincipalTurno()
 * Usado en: [Especificar dónde se usa, si se conoce]
 */
public class GetPrincipalTurnoUseCase {

    private final EntityManagerFactory entityManagerFactory;

    public GetPrincipalTurnoUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    /**
     * Ejecuta el caso de uso para obtener el turno principal de una persona.
     * @return PersonaBean con los datos del turno principal, o null si no se encuentra.
     */
    public PersonaBean execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            PersonaRepository repository = new PersonaRepository(em);
            return repository.getPrincipalTurnoRepository();
        } catch (Exception ex) {
            throw new RuntimeException("Error al obtener el turno principal", ex);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}