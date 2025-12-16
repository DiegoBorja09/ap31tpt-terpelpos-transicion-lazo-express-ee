package com.application.useCases.persons;

import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.PersonaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para registrar un tag para una persona.
 * Reemplaza: PersonasDao.registrarTag(String identificacion, String tag)
 * Usado en: [Especificar dónde se usa, si se conoce]
 */
public class RegistrarTagUseCase {

    private final EntityManagerFactory entityManagerFactory;

    public RegistrarTagUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    /**
     * Ejecuta el caso de uso para registrar un tag para una persona.
     * @param identificacion La identificación de la persona.
     * @param tag El tag a registrar.
     * @return long con el ID de la persona actualizada, o 0 si no se encuentra.
     */
    public long execute(String identificacion, String tag) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            PersonaRepository repository = new PersonaRepository(em);
            return repository.registrarTagRepository(identificacion, tag);
        } catch (Exception ex) {
            throw new RuntimeException("Error al registrar el tag", ex);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}