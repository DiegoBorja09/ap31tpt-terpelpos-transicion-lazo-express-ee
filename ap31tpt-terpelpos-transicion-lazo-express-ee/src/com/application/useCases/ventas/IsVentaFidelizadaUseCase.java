package com.application.useCases.ventas;


import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.VentaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;


/**
 * Caso de uso para verificar si una venta está fidelizada.
 * Reemplaza: MovimientosDao.isVentaFidelizada()
 * Usado en: StoreConfirmarViewController
 */
public class IsVentaFidelizadaUseCase implements BaseUseCases<Boolean> {

    private final Long idVenta;
    private final EntityManagerFactory entityManagerFactory;

    public IsVentaFidelizadaUseCase(Long idVenta) {
        this.idVenta = idVenta;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Boolean execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            VentaRepository repository = new VentaRepository(em);
            return repository.isVentaFidelizada(idVenta);
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}
