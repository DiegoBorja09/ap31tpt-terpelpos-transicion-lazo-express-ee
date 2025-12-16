package com.application.useCases.categorias;

import com.application.core.BaseUseCases;
import com.bean.CategoriaBean;
import com.infrastructure.cache.KioscoCacheServiceLiviano;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.BodegaRepository;
import com.controllers.NovusConstante;
import com.firefuel.Main;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class FindAllCategoriasKIOSCOUseCase implements BaseUseCases<List<CategoriaBean>> {
    
    private final EntityManagerFactory entityManagerFactory;
    private KioscoCacheServiceLiviano cacheService;
    
    public FindAllCategoriasKIOSCOUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
        
        // Obtener cache service liviano (Singleton)
        try {
            this.cacheService = KioscoCacheServiceLiviano.getInstance();
        } catch (Exception e) {
            System.out.println(" Cache liviano no disponible para categorías, usando BD directamente: " + e.getMessage());
            this.cacheService = null;
        }
    }
    
    @Override
    public List<CategoriaBean> execute() {
      
        if (cacheService != null) {
            try {
                List<CategoriaBean> categoriasCache = cacheService.obtenerCategoriasConCache();
                if (categoriasCache != null && !categoriasCache.isEmpty()) {
                    System.out.println("Cache HIT - Categorías KIOSCO (" + categoriasCache.size() + " categorías)");
                    return categoriasCache;
                }
            } catch (Exception e) {
                System.err.println("Error en cache para categorías, fallback a BD: " + e.getMessage());
            }
        }
        
        // Fallback a BD si cache no funciona
        System.out.println("Cache MISS para categorías - Consultando BD");
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            BodegaRepository repository = new BodegaRepository(em);
            boolean isCDL = Main.TIPO_NEGOCIO.equals(NovusConstante.PARAMETER_CDL);
            return repository.findAllCategoriasKIOSCO(isCDL);
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}
