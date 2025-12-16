package com.application.useCases.productos;

// package com.application.useCases.productos;

import com.application.core.BaseUseCases;
import com.bean.ProductoBean;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.repositories.ProductoRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

/**
 * Caso de uso para obtener los productos activos pertenecientes a una categoría específica.
 * Reemplaza: MovimientosDao.buscarListaBasicaProductosPorCategoria(int)
 * Usado en: ServerComandoWS.
 */
public class GetProductosPorCategoriaUseCase implements BaseUseCases<List<ProductoBean>> {

    private final EntityManagerFactory entityManagerFactory;
    private final int categoriaId;

    public GetProductosPorCategoriaUseCase(int categoriaId) {
        this.categoriaId = categoriaId;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
    }

    @Override
    public List<ProductoBean> execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            ProductoRepository repository = new ProductoRepository(em);
            return repository.buscarPorCategoria(categoriaId);
        }catch (Exception ex) {
            throw new RuntimeException("Error al buscar productos por categoría: " + categoriaId, ex);
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}
