package com.application.useCases.ventas;

import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.VentaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para validar si existe turno medio de pago para un movimiento.
 * Reemplaza: MovimientosDao.validarTurnoMedioPago()
 */
public class ValidarTurnoMedioPagoUseCase implements BaseUseCases<Boolean> {

    private final long idMovimiento;
    private final EntityManagerFactory entityManagerFactory;

    public ValidarTurnoMedioPagoUseCase(long idMovimiento) {
        this.idMovimiento = idMovimiento;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Boolean execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            VentaRepository repo = new VentaRepository(em);
            return repo.validarTurnoMedioPago(idMovimiento);
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}

