package com.application.useCases.persons;

import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.PersonaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para verificar si un usuario es administrador.
 * Reemplaza: PersonasDao.isAdmin(long id)
 * Usado en: [Especificar dónde se usa, si se conoce]
 */
public class IsAdminUseCase {

    private final EntityManagerFactory entityManagerFactory;

    public IsAdminUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
    }

    /**
     * Ejecuta el caso de uso para verificar si un usuario es administrador.
     * @param id El ID del usuario.
     * @return true si el usuario es administrador (perfil tipo 5), false en caso contrario.
     */
    public boolean execute(long id) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            PersonaRepository repository = new PersonaRepository(em);
            return repository.isAdminRepository(id);
        } catch (Exception ex) {
            throw new RuntimeException("Error al verificar si el usuario es administrador", ex);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}