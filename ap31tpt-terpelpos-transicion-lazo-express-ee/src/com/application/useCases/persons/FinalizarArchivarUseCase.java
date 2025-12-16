package com.application.useCases.persons;

import com.bean.JornadaBean;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.PersonaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para finalizar y archivar una jornada.
 * Reemplaza: PersonasDao.finalizarArchivar(JornadaBean jornada)
 * Usado en: [Especificar dónde se usa, si se conoce]
 */
public class FinalizarArchivarUseCase {

    private final EntityManagerFactory entityManagerFactory;

    public FinalizarArchivarUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
    }

    /**
     * Ejecuta el caso de uso para finalizar y archivar una jornada.
     * @param jornada La jornada a finalizar y archivar.
     */
    public void execute(JornadaBean jornada) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            PersonaRepository repository = new PersonaRepository(em);
            repository.finalizarArchivarRepository(jornada);
        } catch (Exception ex) {
            throw new RuntimeException("Error al finalizar y archivar la jornada", ex);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}