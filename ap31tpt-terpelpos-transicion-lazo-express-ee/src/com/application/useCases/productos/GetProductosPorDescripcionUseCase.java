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
 * Caso de uso para buscar productos activos por palabra clave en la descripción.
 * Reemplaza: MovimientosDao.buscarListaBasicaProductor(String keyword)
 * Usado en: ServerComandoWS.
 */
public class GetProductosPorDescripcionUseCase implements BaseUseCases<List<ProductoBean>> {

    private final String keyword;
    private final EntityManagerFactory entityManagerFactory;

    public GetProductosPorDescripcionUseCase(String keyword) {
        this.keyword = keyword;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
    }

    @Override
    public List<ProductoBean> execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            ProductoRepository repository = new ProductoRepository(em);
            return repository.buscarPorDescripcion(keyword);
        } catch (Exception ex) {
            throw new RuntimeException("Error al buscar productos por descripción: " + keyword, ex);
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}
