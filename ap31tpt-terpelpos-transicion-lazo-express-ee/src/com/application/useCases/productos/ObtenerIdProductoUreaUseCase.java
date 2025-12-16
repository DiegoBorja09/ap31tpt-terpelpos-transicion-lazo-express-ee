package com.application.useCases.productos;

import com.application.core.BaseUseCases;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.ProductoRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Use Case para obtener el ID del producto UREA
 * Migración JPA del método idProductoUREA() de RumboDao
 */
public class ObtenerIdProductoUreaUseCase implements BaseUseCases<Long> {

    private final EntityManagerFactory entityManagerFactory;

    public ObtenerIdProductoUreaUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Long execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            ProductoRepository productoRepository = new ProductoRepository(em);
            return productoRepository.obtenerIdProductoUrea(NovusConstante.NOMBRE_PRODUCTO_UREA);
        } catch (Exception e) {
            NovusUtils.printLn("Error al obtener ID del producto UREA: " + e.getMessage());
            return 0L;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
} 