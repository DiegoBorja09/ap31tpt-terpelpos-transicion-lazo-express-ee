package com.application.useCases.movimientos;

import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.CtMovimientoRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class UpadteByEstateMovimientoUseCase implements BaseUseCases<Boolean> {
    
    private final Long idVenta;
    private final String nuevoEstado;
    private final EntityManagerFactory entityManagerFactory;

    public UpadteByEstateMovimientoUseCase(Long idVenta, String nuevoEstado) {
        this.idVenta = idVenta;
        this.nuevoEstado = nuevoEstado;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Boolean execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        
        try {
            CtMovimientoRepository ctMovimientoRepository = new CtMovimientoRepository(entityManager);
            return ctMovimientoRepository.updateEstadoMovimiento(idVenta, nuevoEstado);
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}
