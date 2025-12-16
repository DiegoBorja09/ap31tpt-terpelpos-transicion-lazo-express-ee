package com.application.useCases.persons;

import com.google.gson.JsonArray;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.PersonaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para buscar todas las personas.
 * Reemplaza: PersonasDao.searchAllPersons(boolean searchInactives)
 * Usado en: [Especificar dónde se usa, si se conoce]
 */
public class SearchAllPersonsUseCase {

    private final EntityManagerFactory entityManagerFactory;

    public SearchAllPersonsUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    /**
     * Ejecuta el caso de uso para buscar todas las personas.
     * @param searchInactives Indica si se deben incluir personas inactivas.
     * @return JsonArray con los datos de las personas, o null si no se encuentran.
     */
    public JsonArray execute(boolean searchInactives) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            PersonaRepository repository = new PersonaRepository(em);
            return repository.searchAllPersonsRepository(searchInactives);
        } catch (Exception ex) {
            throw new RuntimeException("Error al buscar todas las personas", ex);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}