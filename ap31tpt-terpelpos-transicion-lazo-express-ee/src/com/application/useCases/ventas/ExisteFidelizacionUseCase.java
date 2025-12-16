package com.application.useCases.ventas;

import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.VentaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para validar si existe una fidelización en estado específico.
 * Reemplaza: MovimientosDao.existeFidelizacion()
 * Usado en: Lógica de validación de fidelización de venta
 */
public class ExisteFidelizacionUseCase implements BaseUseCases<Boolean> {

    private final Long idVenta;
    private final EntityManagerFactory entityManagerFactory;

    public ExisteFidelizacionUseCase(Long idVenta) {
        this.idVenta = idVenta;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Boolean execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            VentaRepository repo = new VentaRepository(em);
            return repo.existeFidelizacion(idVenta);
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}

