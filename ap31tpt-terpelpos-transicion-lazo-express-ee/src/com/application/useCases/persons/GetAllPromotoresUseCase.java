package com.application.useCases.persons;

import com.bean.PersonaBean;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.PersonaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;

/**
 * Caso de uso para obtener todos los promotores.
 * Reemplaza: PersonasDao.getAllPromotores()
 * Usado en: [Especificar dónde se usa, si se conoce]
 */
public class GetAllPromotoresUseCase {

    private final EntityManagerFactory entityManagerFactory;

    public GetAllPromotoresUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
    }

    /**
     * Ejecuta el caso de uso para obtener todos los promotores.
     * @return ArrayList de PersonaBean con los datos de los promotores.
     */
    public ArrayList<PersonaBean> execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            PersonaRepository repository = new PersonaRepository(em);
            return repository.getAllPromotoresRepository();
        } catch (Exception ex) {
            throw new RuntimeException("Error al obtener todos los promotores", ex);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}