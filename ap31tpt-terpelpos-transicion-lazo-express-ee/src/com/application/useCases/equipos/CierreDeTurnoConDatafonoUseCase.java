package com.application.useCases.equipos;
import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.EquipoRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class CierreDeTurnoConDatafonoUseCase implements BaseUseCases<Boolean> {

    private final EntityManagerFactory entityManagerFactory;

    public CierreDeTurnoConDatafonoUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Boolean execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            EquipoRepository repository = new EquipoRepository(em);
            return repository.cierreDeTurnoConDatafono();
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}