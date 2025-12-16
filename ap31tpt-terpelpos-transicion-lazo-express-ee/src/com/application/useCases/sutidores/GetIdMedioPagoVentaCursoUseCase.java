package com.application.useCases.sutidores;

import com.application.core.BaseUseCasesWithParams;
import com.controllers.NovusUtils;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.SurtidorRepository;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class GetIdMedioPagoVentaCursoUseCase implements BaseUseCasesWithParams<Integer, Long> {

    private final EntityManagerFactory entityManagerFactory;

    public GetIdMedioPagoVentaCursoUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
            .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Long execute(Integer cara) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            SurtidorRepository surtidorRepository = new SurtidorRepository(entityManager);
            return surtidorRepository.getIdMedioPagoVentaCurso(cara);
        } catch (Exception e) {
            NovusUtils.printLn("Error al obtener ID del medio de pago de la venta en curso: " + e.getMessage());
            return 1L;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
} 