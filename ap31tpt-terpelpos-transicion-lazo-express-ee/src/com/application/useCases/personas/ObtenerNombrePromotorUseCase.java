package com.application.useCases.personas;

import com.application.core.BaseUseCases;
import com.controllers.NovusUtils;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.PersonaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Use Case para obtener el nombre de un promotor por su ID.
 * Migración JPA del método nombrePromotor() de RumboDao.
 */
public class ObtenerNombrePromotorUseCase implements BaseUseCases<String> {

    private final EntityManagerFactory entityManagerFactory;
    private final long promotorId;

    public ObtenerNombrePromotorUseCase(long promotorId) {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE.getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
        this.promotorId = promotorId;
    }

    @Override
    public String execute() {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            PersonaRepository personaRepository = new PersonaRepository(entityManager);
            
            String nombrePromotor = personaRepository.obtenerNombrePromotor(this.promotorId);
            
            NovusUtils.printLn("✅ Nombre promotor obtenido para ID " + this.promotorId + ": " + nombrePromotor);
            return nombrePromotor;
            
        } catch (Exception e) {
            NovusUtils.printLn("❌ Error obteniendo nombre del promotor con ID " + this.promotorId + ": " + e.getMessage());
            return "";
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
} 