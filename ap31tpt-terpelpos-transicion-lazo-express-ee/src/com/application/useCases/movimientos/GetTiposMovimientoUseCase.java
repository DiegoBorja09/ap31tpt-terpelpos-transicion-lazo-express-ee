package com.application.useCases.movimientos;

import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.TiposMovimientoRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.TreeMap;

/**
 * Caso de uso para consultar los tipos de movimiento disponibles.
 * Reemplaza: MovimientosDao.getTipoMovimiento()
 * Usado en: ReporteInventarioKardex
 */
public class GetTiposMovimientoUseCase implements BaseUseCases<TreeMap<Integer, String>> {

    private final EntityManagerFactory entityManagerFactory;

    public GetTiposMovimientoUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public TreeMap<Integer, String> execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            TiposMovimientoRepository repo = new TiposMovimientoRepository(em);
            return repo.obtenerTiposMovimiento();
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}
