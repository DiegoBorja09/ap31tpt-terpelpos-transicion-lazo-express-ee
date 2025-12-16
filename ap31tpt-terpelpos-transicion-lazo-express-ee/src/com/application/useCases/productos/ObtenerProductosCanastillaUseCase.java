package com.application.useCases.productos;

import com.application.core.BaseUseCases;
import com.bean.MovimientosDetallesBean;
import com.bean.ResultadoProductosCanastila;
import com.dao.DAOException;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.ProductoCanastillaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Caso de uso para obtener productos de canastilla.
 * Reemplaza: MovimientosDao.findAllProductoTipo()
 * Usado en: StoreViewController para mostrar productos en la vista de canastilla.
 */
public class ObtenerProductosCanastillaUseCase implements BaseUseCases<ResultadoProductosCanastila> {

    private final EntityManagerFactory entityManagerFactory;
    private final int currentPage;
    private final int pageSize;
    public ObtenerProductosCanastillaUseCase(int currentPage, int pageSize) {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    @Override
    public ResultadoProductosCanastila execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        long startTime = System.currentTimeMillis();
        try {
            ProductoCanastillaRepository repository = new ProductoCanastillaRepository(em);
            ResultadoProductosCanastila result = repository.obtenerProductosCanastilla(currentPage, pageSize);
            long endTime = System.currentTimeMillis();
            System.out.println("⏱️ Tiempo de ejecución ObtenerProductosCanastillaUseCase: " + (endTime - startTime) + "ms");
            return result;
        } catch (DAOException e) {
            System.err.println("⚠️ Error en ObtenerProductosCanastillaUseCase: " + e.getMessage());
            e.printStackTrace();
            return new ResultadoProductosCanastila(new ArrayList<>(), 0); // Retornamos lista vacía en caso de error
        } finally {
            if (em.isOpen()) em.close();
        }
    }
} 