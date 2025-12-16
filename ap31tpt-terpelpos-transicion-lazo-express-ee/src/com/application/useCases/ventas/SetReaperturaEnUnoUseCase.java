package com.application.useCases.ventas;

import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.VentaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para actualizar el campo `reapertura` a 1 en la tabla procesos.tbl_transaccion_proceso.
 * Reemplaza: MovimientosDao.setReaperturaInOne(Long idVenta)
 * Usado en: FidelizacionCliente, StoreConfirmarViewController, FidelizarVentaCliente
 */
public class SetReaperturaEnUnoUseCase implements BaseUseCases<Void> {

    private final Long idVenta;
    private final EntityManagerFactory entityManagerFactory;

    public SetReaperturaEnUnoUseCase(Long idVenta) {
        this.idVenta = idVenta;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Void execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            VentaRepository repo = new VentaRepository(em);
            repo.setReaperturaInOne(idVenta);
        } finally {
            if (em.isOpen()) em.close();
        }
        return null;
    }
}
