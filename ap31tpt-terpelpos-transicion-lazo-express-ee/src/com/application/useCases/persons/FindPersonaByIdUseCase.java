package com.application.useCases.persons;

import com.bean.PersonaBean;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.PersonaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para buscar una persona por su ID.
 * Reemplaza: PersonasDao.findPersonaById(long idPersona)
 * Usado en: [Especificar dónde se usa, si se conoce]
 */
public class FindPersonaByIdUseCase {

    private final EntityManagerFactory entityManagerFactory;

    public FindPersonaByIdUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
    }

    /**
     * Ejecuta el caso de uso para buscar una persona por su ID.
     * @param idPersona El ID de la persona.
     * @return PersonaBean con los datos de la persona, o null si no se encuentra.
     */
    public PersonaBean execute(long idPersona) {
        System.out.println("🔍 idPersona: " + idPersona);
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            PersonaRepository repository = new PersonaRepository(em);
            return repository.findPersonaByIdRepository(idPersona);
        } catch (Exception ex) {
            throw new RuntimeException("Error al buscar la persona por ID", ex);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}