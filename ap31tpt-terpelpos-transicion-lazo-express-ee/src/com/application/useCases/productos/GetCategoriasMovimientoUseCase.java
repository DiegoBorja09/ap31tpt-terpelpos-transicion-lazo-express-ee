package com.application.useCases.productos;

import com.application.core.BaseUseCases;
import com.bean.MovimientosDetallesBean;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.ProductoRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Set;

/**
 * Caso de uso para obtener las categorías asociadas a un conjunto de productos.
 * Reemplaza: MovimientosDao.getCategoriasMovimiento(Set<Long>)
 * Usado en: servicios que requieren mostrar o procesar las categorías de los productos
 * seleccionados en procesos de venta o transmisión.
 */
public class GetCategoriasMovimientoUseCase implements BaseUseCases<List<MovimientosDetallesBean>> {

    private final Set<Long> productosIds;
    private final EntityManagerFactory entityManagerFactory;

    public GetCategoriasMovimientoUseCase(Set<Long> productosIds) {
        this.productosIds = productosIds;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
    }

    @Override
    public List<MovimientosDetallesBean> execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            ProductoRepository repo = new ProductoRepository(em);
            return repo.getCategoriasMovimiento(productosIds);
        } catch (Exception ex) {
            throw new RuntimeException("Error al obtener categorías por producto", ex);
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}

