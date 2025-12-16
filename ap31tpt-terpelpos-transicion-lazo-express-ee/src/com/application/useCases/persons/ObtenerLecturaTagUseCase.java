package com.application.useCases.persons;

import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.PersonaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para obtener la lectura de un tag.
 * Reemplaza: PersonasDao.obtenerLecturaTag()
 * Usado en: [Especificar dónde se usa, si se conoce]
 */
public class ObtenerLecturaTagUseCase {

    private final EntityManagerFactory entityManagerFactory;

    public ObtenerLecturaTagUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    /**
     * Ejecuta el caso de uso para obtener la lectura de un tag.
     * @return String con el valor de la lectura del tag, o null si no se encuentra.
     */
    public String execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            PersonaRepository repository = new PersonaRepository(em);
            return repository.obtenerLecturaTagRepository();
        } catch (Exception ex) {
            throw new RuntimeException("Error al obtener la lectura del tag", ex);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}