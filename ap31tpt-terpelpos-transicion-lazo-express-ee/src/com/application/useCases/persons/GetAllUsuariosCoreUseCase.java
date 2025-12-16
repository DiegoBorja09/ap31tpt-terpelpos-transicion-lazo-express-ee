package com.application.useCases.persons;

import com.bean.PersonaBean;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.JornadaRepository;
import com.infrastructure.repositories.PersonaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;

/**
 * Caso de uso para obtener todos los usuarios del core.
 * Reemplaza: PersonasDao.getAllUsuariosCore()
 * Usado en: [Especificar dónde se usa, si se conoce]
 */
public class GetAllUsuariosCoreUseCase {

    private final EntityManagerFactory entityManagerFactory;

    public GetAllUsuariosCoreUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    /**
     * Ejecuta el caso de uso para obtener todos los usuarios del core.
     * @return ArrayList de PersonaBean con los datos de los usuarios.
     */
    public ArrayList<PersonaBean> execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            PersonaRepository repository = new PersonaRepository(em);
            return repository.getAllUsuariosCoreRepository();
        } catch (Exception ex) {
            throw new RuntimeException("Error al obtener todos los usuarios del core", ex);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}