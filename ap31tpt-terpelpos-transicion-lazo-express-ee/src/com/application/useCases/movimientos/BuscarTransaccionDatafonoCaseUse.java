package com.application.useCases.movimientos;
import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.VentaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para buscar una transacción de datafono por ID de movimiento.
 * Usado en: Validación de transacciones de datafono.
 */
public class BuscarTransaccionDatafonoCaseUse implements BaseUseCases<Boolean> {

    private final long idMovimiento;
    private final EntityManagerFactory entityManagerFactory;

    public BuscarTransaccionDatafonoCaseUse(long idMovimiento) {
        this.idMovimiento = idMovimiento;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Boolean execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            VentaRepository repo = new VentaRepository(em);
            return repo.buscarTransaccionDatafono(idMovimiento);
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}