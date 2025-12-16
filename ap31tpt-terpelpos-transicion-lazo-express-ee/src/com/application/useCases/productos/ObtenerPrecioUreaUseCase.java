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
 * Use Case para obtener el precio del producto UREA
 * Migración JPA del método precioUREA() de RumboDao
 */
public class ObtenerPrecioUreaUseCase implements BaseUseCases<Float> {

    private final EntityManagerFactory entityManagerFactory;

    public ObtenerPrecioUreaUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Float execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            ProductoRepository productoRepository = new ProductoRepository(em);
            return productoRepository.obtenerPrecioUrea(NovusConstante.NOMBRE_PRODUCTO_UREA);
        } catch (Exception e) {
            NovusUtils.printLn("Error al obtener precio UREA: " + e.getMessage());
            return 0f;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
} 