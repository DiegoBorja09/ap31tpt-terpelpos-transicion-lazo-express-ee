package com.application.useCases.wacherparametros;
import com.application.core.BaseUseCases;
import com.application.core.BaseUseCasesWithParams;
import com.infrastructure.cache.WacherParametrosCacheSimple;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.CtWacherParametroRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Use Case parametrizable para consultas wacher_parametros con cache
 * 
 * MIGRADO A CACHE: Optimizado para usar WacherParametrosCacheSimple
 * en lugar de consultas directas a PostgreSQL.
 */
public class GetParameterWacherUseCase implements BaseUseCasesWithParams<String,String> {

    private static final Logger logger = Logger.getLogger(GetParameterWacherUseCase.class.getName());
    
    private final String parameterColumn;
    private final WacherParametrosCacheSimple cacheService;
    private final EntityManagerFactory entityManagerFactory;
    private final boolean useLegacyMode;

    public GetParameterWacherUseCase(String parameterColumn) {
        this.parameterColumn = parameterColumn;
        this.cacheService = WacherParametrosCacheSimple.getInstance();
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
        this.useLegacyMode = false;
    }
    
    public GetParameterWacherUseCase(String parameterColumn, boolean useLegacyMode) {
        this.parameterColumn = parameterColumn;
        this.cacheService = WacherParametrosCacheSimple.getInstance();
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
        this.useLegacyMode = useLegacyMode;
    }

    @Override
    public String execute(String value) {
        // Usar cache para consultas estándar por código
        if ("codigo".equals(parameterColumn) && value != null && !useLegacyMode) {
            try {
                long startTime = System.currentTimeMillis();
                String cachedValue = cacheService.getParameter(value);
                long duration = System.currentTimeMillis() - startTime;
                
                // Convertir null a string vacío para mantener compatibilidad
                String result = cachedValue != null ? cachedValue : "";
                
                logger.fine(String.format(
                    "[CACHE-MODE] GetParameterWacherUseCase: %s = %s (%dms) [Cache]",
                    value, result, duration
                ));
                
                return result;
                
            } catch (Exception e) {
                logger.log(Level.WARNING, 
                    "❌ Error en cache para parámetro: " + value + ", fallback a BD", e);
                // Continúa con fallback legacy
            }
        }
        
        // Fallback legacy
        return executeLegacyMode(value);
    }
    
    private String executeLegacyMode(String value) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            long startTime = System.currentTimeMillis();
            CtWacherParametroRepository ctWacherParametroRepository = new CtWacherParametroRepository(entityManager);
            String result = ctWacherParametroRepository.findByParameter(parameterColumn, value)
                    .map(parametro -> parametro.getValor())
                    .orElse("");
            
            long duration = System.currentTimeMillis() - startTime;
            
            logger.info(String.format(
                "[LEGACY-MODE] GetParameterWacherUseCase: %s.%s = %s (%dms) [BD]",
                parameterColumn, value, result, duration
            ));
            
            return result;
            
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}
