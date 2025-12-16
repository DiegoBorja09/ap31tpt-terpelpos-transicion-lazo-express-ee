package com.application.useCases.persons;

import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.PersonaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para eliminar todas las lecturas de tags.
 * Reemplaza: PersonasDao.eliminarLectura()
 * Usado en: [ConsultarUsuario, TurnosIniciarViewController]
 */
public class EliminarLecturaUseCase {

    private final EntityManagerFactory entityManagerFactory;

    public EliminarLecturaUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    /**
     * Ejecuta el caso de uso para eliminar todas las lecturas de tags.
     */
    public void execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin(); // Iniciar transacción
            PersonaRepository repository = new PersonaRepository(em);
            repository.eliminarLecturaRepository();
            em.getTransaction().commit(); // Confirmar transacción
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Revertir en caso de error
            }
            throw new RuntimeException("Error al eliminar las lecturas de tags", ex);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}