package com.application.useCases.ventas;

import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.VentaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para verificar si una transacción de movimiento está pendiente.
 * Reemplaza: MovimientosDao.isPendienteTransaccionMovimiento()
 * Usado en: VentasHistorialView.
 */
public class IsPendienteTransaccionUseCase implements BaseUseCases<Boolean> {

    private final long idMovimiento;
    private final EntityManagerFactory entityManagerFactory;

    public IsPendienteTransaccionUseCase(long idMovimiento) {
        this.idMovimiento = idMovimiento;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Boolean execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            VentaRepository repo = new VentaRepository(em);
            return repo.isPendienteTransaccionMovimiento(idMovimiento);
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}
