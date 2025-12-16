package com.application.useCases.transmisiones;

import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.TransmisionRemisionRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para obtener el ID de remisión desde el ID de movimiento.
 * Reemplaza: MovimientosDao.obtenerIdRemisionDesdeMovimiento()
 * Usado en: FacturaElectronicaVentaEnVivo
 */
public class ObtenerIdRemisionDesdeMovimientoUseCase implements BaseUseCases<Long> {

    private final long idMovimiento;
    private final EntityManagerFactory entityManagerFactory;

    public ObtenerIdRemisionDesdeMovimientoUseCase(long idMovimiento) {
        this.idMovimiento = idMovimiento;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Long execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            TransmisionRemisionRepository repo = new TransmisionRemisionRepository(em);
            return repo.obtenerIdRemisionDesdeMovimiento(idMovimiento);
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}
