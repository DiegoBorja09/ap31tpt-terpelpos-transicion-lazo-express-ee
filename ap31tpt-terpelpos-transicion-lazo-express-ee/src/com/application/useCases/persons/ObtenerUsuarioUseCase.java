package com.application.useCases.persons;

import com.bean.PersonaBean;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.PersonaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para obtener un usuario por lectura de identificador.
 * Reemplaza: PersonasDao.obtenerUsuario(String lectura)
 * Usado en: [Especificar dónde se usa, si se conoce]
 */
public class ObtenerUsuarioUseCase {

    private final EntityManagerFactory entityManagerFactory;

    public ObtenerUsuarioUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
    }

    /**
     * Ejecuta el caso de uso para obtener un usuario por lectura de identificador.
     * @param lectura El identificador del usuario.
     * @return PersonaBean con los datos del usuario, o null si no se encuentra.
     */
    public PersonaBean execute(String lectura) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            PersonaRepository repository = new PersonaRepository(em);
            return repository.obtenerUsuarioRepository(lectura);
        } catch (Exception ex) {
            throw new RuntimeException("Error al obtener el usuario", ex);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}