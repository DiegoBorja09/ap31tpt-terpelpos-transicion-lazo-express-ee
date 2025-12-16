package com.application.useCases.sutidores;

import com.application.core.BaseUseCasesWithParams;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.SurtidorRepository;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class ActualizarFidelizacionUseCase implements BaseUseCasesWithParams<Long, Void> {
    private final EntityManagerFactory entityManagerFactory;

    public ActualizarFidelizacionUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Void execute(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID del movimiento no puede ser nulo o menor o igual a cero");
        }

        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            SurtidorRepository surtidorRepository = new SurtidorRepository(em);
            surtidorRepository.actualizarFidelizacion(id);
            em.getTransaction().commit();
            return null;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al actualizar la fidelización: " + e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
} 