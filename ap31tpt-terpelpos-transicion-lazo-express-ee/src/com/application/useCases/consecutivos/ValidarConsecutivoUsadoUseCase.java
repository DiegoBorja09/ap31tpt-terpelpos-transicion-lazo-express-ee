package com.application.useCases.consecutivos;

import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.CtMovimientoRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class ValidarConsecutivoUsadoUseCase implements BaseUseCases<Boolean> {

    private final String prefijo;
    private final long consecutivo;
    private final EntityManagerFactory entityManagerFactory;

    public ValidarConsecutivoUsadoUseCase(String prefijo, long consecutivo) {
        this.prefijo = prefijo;
        this.consecutivo = consecutivo;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Boolean execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            CtMovimientoRepository repository = new CtMovimientoRepository(em);
            return repository.consecutivoUsado(prefijo, consecutivo);
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}

