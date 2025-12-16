package com.application.useCases.bodegas;

import com.application.core.BaseUseCases;
import com.controllers.NovusUtils;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.CtBodegaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para obtener el ID de la bodega UREA con tipo 'V' (Venta)
 * 
 * Reemplaza: RumboDao.idBodegaUREA()
 * Tabla: ct_bodegas
 * Filtro: atributos::json->>'tipo' = 'V'
 */
public class ObtenerIdBodegaUreaUseCase implements BaseUseCases<Long> {

    private final EntityManagerFactory entityManagerFactory;

    public ObtenerIdBodegaUreaUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    /**
     * Ejecuta el caso de uso para obtener el ID de la bodega UREA
     * 
     * @return ID de la bodega UREA con tipo 'V', o 0L si no se encuentra
     */
    @Override
    public Long execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            CtBodegaRepository ctBodegaRepository = new CtBodegaRepository(em);
            return ctBodegaRepository.obtenerIdBodegaUrea();
        } catch (Exception e) {
            NovusUtils.printLn("❌ Error en ObtenerIdBodegaUreaUseCase: " + e.getMessage());
            return 0L;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
} 