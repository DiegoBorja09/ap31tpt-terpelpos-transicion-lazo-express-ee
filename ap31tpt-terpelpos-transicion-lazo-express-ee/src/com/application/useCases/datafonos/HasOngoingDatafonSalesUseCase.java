package com.application.useCases.datafonos;

import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.DatafonosRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class HasOngoingDatafonSalesUseCase implements BaseUseCases<Boolean> {

    private final EntityManagerFactory entityManagerFactory;
    private final String codigoTerminal;

    public HasOngoingDatafonSalesUseCase(String codigoTerminal) {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
        this.codigoTerminal = codigoTerminal;
    }

    @Override
    public Boolean execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            DatafonosRepository repository = new DatafonosRepository(em);
            return repository.HasOngoingDatafonSalesUseCase(codigoTerminal);
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}


