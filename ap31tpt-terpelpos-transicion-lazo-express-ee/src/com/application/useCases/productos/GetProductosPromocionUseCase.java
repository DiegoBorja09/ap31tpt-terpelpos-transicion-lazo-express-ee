package com.application.useCases.productos;

import com.application.core.BaseUseCases;
import com.bean.ProductoBean;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.ProductoRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

/**
 * Caso de uso para obtener productos que están en promoción.
 * Retorna productos activos que tienen promoción definida y pueden venderse.
 * Incluye información de saldo desde bodegas_productos.
 */
public class GetProductosPromocionUseCase implements BaseUseCases<List<ProductoBean>> {

    private final EntityManagerFactory entityManagerFactory;

    public GetProductosPromocionUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
    }

    @Override
    public List<ProductoBean> execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            ProductoRepository repository = new ProductoRepository(em);
            return repository.obtenerProductosPromocion();
        } catch (Exception ex) {
            throw new RuntimeException("Error al obtener productos en promoción", ex);
        } finally {
            if (em.isOpen()) em.close();
        }
    }
} 