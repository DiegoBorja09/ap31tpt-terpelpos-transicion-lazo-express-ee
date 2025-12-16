package com.application.useCases.productos;

import com.application.core.BaseUseCases;
import com.bean.MovimientosDetallesBean;
import com.infrastructure.cache.KioscoCacheServiceLiviano;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.ProductoKioskoRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para buscar un producto de tipo kiosko por PLU.
 * Reemplaza: MovimientosDao.findByPluKIOSCO(String plu)
 * Usado en: búsquedas rápidas por PLU en el KIOSCO
 * 
 *  ACTUALIZADO: Ahora usa cache inteligente primero
 */
public class BuscarProductoPorPluKioskoUseCase implements BaseUseCases<MovimientosDetallesBean> {

    private final String plu;
    private final EntityManagerFactory entityManagerFactory;
    private KioscoCacheServiceLiviano cacheService;

    public BuscarProductoPorPluKioskoUseCase(String plu) {
        this.plu = plu;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
        
        // Obtener cache service liviano (Singleton)
        try {
            this.cacheService = KioscoCacheServiceLiviano.getInstance();
        } catch (Exception e) {
            System.out.println(" Cache liviano no disponible para PLU, usando BD directamente: " + e.getMessage());
            this.cacheService = null;
        }
    }

    @Override
    public MovimientosDetallesBean execute() {
        // OPTIMIZADO: Cache service ahora maneja todo (cache + BD si es necesario)
        if (cacheService != null) {
            try {
                // Cache service maneja internamente: cache hit → retorna, cache miss → BD + cache store
                return cacheService.buscarProductoPorPluConCache(plu);
            } catch (Exception e) {
                System.err.println(" Error en cache service para PLU: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // Solo como último recurso si cache service falló completamente
        System.out.println(" Fallback final a BD directo - PLU: " + plu);
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            ProductoKioskoRepository repository = new ProductoKioskoRepository(em);
            return repository.findByPluKiosco(plu);
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}
