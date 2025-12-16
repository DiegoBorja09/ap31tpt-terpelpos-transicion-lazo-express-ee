package com.application.useCases.persons;

import com.bean.PersonaBean;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.PersonaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;

/**
 * Caso de uso para obtener todas las jornadas.
 * Reemplaza: PersonasDao.getAllJornadas()
 * Usado en: [Especificar dónde se usa, si se conoce]
 */
public class GetAllJornadasUseCase {

    private final EntityManagerFactory entityManagerFactory;

    public GetAllJornadasUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
    }

    /**
     * Ejecuta el caso de uso para obtener todas las jornadas.
     * @return ArrayList de PersonaBean con los datos de las jornadas.
     */
    public ArrayList<PersonaBean> execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            PersonaRepository repository = new PersonaRepository(em);
            return repository.getAllJornadasRepository();
        } catch (Exception ex) {
            throw new RuntimeException("Error al obtener todas las jornadas", ex);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}