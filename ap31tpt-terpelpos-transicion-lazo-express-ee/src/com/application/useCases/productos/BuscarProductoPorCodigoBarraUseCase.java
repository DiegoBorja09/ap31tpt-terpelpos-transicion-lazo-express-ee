package com.application.useCases.productos;

import com.application.core.BaseUseCases;
import com.bean.MovimientosDetallesBean;
import com.infrastructure.cache.KioscoCacheServiceLiviano;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.ProductoKioskoRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

/**
 * Caso de uso para buscar un producto de tipo kiosko por código de barras.
 * Reemplaza: MovimientosDao.findByBarCodeKIOSCO(String code)
 * Usado en: lógica de búsqueda rápida por código de barras
 * 
 *  ACTUALIZADO: Ahora usa cache inteligente primero
 */
public class BuscarProductoPorCodigoBarraUseCase implements BaseUseCases<MovimientosDetallesBean> {

    private final String codigoBarra;
    private final EntityManagerFactory entityManagerFactory;
    private KioscoCacheServiceLiviano cacheService;

    public BuscarProductoPorCodigoBarraUseCase(String codigoBarra) {
        this.codigoBarra = codigoBarra;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
        
        // Obtener cache service liviano (Singleton)
        try {
            this.cacheService = KioscoCacheServiceLiviano.getInstance();
        } catch (Exception e) {
            System.out.println(" Cache liviano no disponible para código de barras, usando BD directamente: " + e.getMessage());
            this.cacheService = null;
        }
    }

    @Override
    public MovimientosDetallesBean execute() {
        // OPTIMIZADO: Cache service ahora maneja todo (cache + BD si es necesario)
        if (cacheService != null) {
            try {
                // Cache service maneja internamente: cache hit → retorna, cache miss → BD + cache store
                return cacheService.buscarProductoPorCodigoBarraConCache(codigoBarra);
            } catch (Exception e) {
                System.err.println(" Error en cache service para código de barras: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // Solo como último recurso si cache service falló completamente
        System.out.println(" Fallback final a BD directo - Código: " + codigoBarra);
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            ProductoKioskoRepository repository = new ProductoKioskoRepository(em);
            return repository.findByCodigoBarraKiosco(codigoBarra);
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}