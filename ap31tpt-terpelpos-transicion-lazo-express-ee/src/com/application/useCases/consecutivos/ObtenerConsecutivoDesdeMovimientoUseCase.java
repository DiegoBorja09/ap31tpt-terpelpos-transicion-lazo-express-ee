package com.application.useCases.consecutivos;
import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.CtMovimientoRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class ObtenerConsecutivoDesdeMovimientoUseCase implements BaseUseCases<Long> {

    private final long numeroMovimiento;
    private final EntityManagerFactory entityManagerFactory;

    public ObtenerConsecutivoDesdeMovimientoUseCase(long numeroMovimiento) {
        this.numeroMovimiento = numeroMovimiento;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Long execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            CtMovimientoRepository repository = new CtMovimientoRepository(em);
            return repository.obtenerConsecutivoDesdeMovimiento(numeroMovimiento);
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}
