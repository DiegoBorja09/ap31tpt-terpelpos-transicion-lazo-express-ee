package com.application.useCases.sutidores;

import com.application.core.BaseUseCases;
import com.application.core.BaseUseCasesWithParams;
import com.controllers.NovusUtils;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.Enums.SqlQueryEnum;
import com.infrastructure.repositories.SurtidorRepository;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class GetMovimientoIdDesdeCaraUseCase implements BaseUseCasesWithParams<Integer, Long> {

    private final EntityManagerFactory entityManagerFactory;

    public GetMovimientoIdDesdeCaraUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
            .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Long execute(Integer cara) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            SurtidorRepository surtidorRepository = new SurtidorRepository(entityManager);
            return surtidorRepository.getMovimientoIdDesdeVentaCurso(cara);
        } catch (Exception e) {
            NovusUtils.printLn("Error al obtener producto desde venta curso: " + e.getMessage());
            return 0L;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}
