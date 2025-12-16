package com.application.useCases.productos;

import com.application.core.BaseUseCases;
import com.bean.MovimientosDetallesBean;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.ProductoKioskoRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Caso de uso para buscar productos de tipo kiosko por texto.
 * Reemplaza: MovimientosDao.busquedaProductoTipoKIOSCO(String busqueda)
 * Usado en: PanelResultadoBusqueda.consultarProductos()
 */
public class BuscarProductosTipoKioskoUseCase implements BaseUseCases<List<MovimientosDetallesBean>> {

    private final String busqueda;
    private final EntityManagerFactory entityManagerFactory;

    public BuscarProductosTipoKioskoUseCase(String busqueda) {
        this.busqueda = busqueda;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
    }

    @Override
    public ArrayList<MovimientosDetallesBean> execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            ProductoKioskoRepository repository = new ProductoKioskoRepository(em);
            return repository.buscarProductos(busqueda);
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}

