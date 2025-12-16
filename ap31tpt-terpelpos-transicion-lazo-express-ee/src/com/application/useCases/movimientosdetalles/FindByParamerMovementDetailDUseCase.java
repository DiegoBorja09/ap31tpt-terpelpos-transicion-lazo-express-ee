package com.application.useCases.movimientosdetalles;

import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.CtMovimeintoDetallesRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Optional;

public class FindByParamerMovementDetailDUseCase<T> implements BaseUseCases<Optional<T>> {
    
    private final Long idMovimiento;
    private final String columnName;
    private final Class<T> returnType;
    private final EntityManagerFactory entityManagerFactory;

    public FindByParamerMovementDetailDUseCase(Long idMovimiento, String columnName, Class<T> returnType) {
        this.idMovimiento = idMovimiento;
        this.columnName = columnName;
        this.returnType = returnType;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Optional<T> execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            CtMovimeintoDetallesRepository repository = new CtMovimeintoDetallesRepository(entityManager);
            return repository.finByMovimientoId(idMovimiento, columnName, returnType);
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}
