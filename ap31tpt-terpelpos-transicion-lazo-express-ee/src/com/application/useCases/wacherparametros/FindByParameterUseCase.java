package com.application.useCases.wacherparametros;

import com.application.core.BaseUseCases;
import com.infrastructure.cache.WacherParametrosCacheSimple;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.CtWacherParametroRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Use Case para consultar parámetros wacher_parametros con cache optimizado
 * 
 * MIGRADO A CACHE: Este Use Case ahora utiliza WacherParametrosCacheSimple
 * para reducir drásticamente las consultas a la base de datos.
 * 
 * Objetivo: Reducir 300,000+ consultas diarias
 * Resultado esperado: 99%+ consultas desde cache
 */
public class FindByParameterUseCase implements BaseUseCases<Boolean> {

    private static final Logger logger = Logger.getLogger(FindByParameterUseCase.class.getName());
    
    private final String parameterColumn;
    private final String value;
    private final WacherParametrosCacheSimple cacheService;
    private final EntityManagerFactory entityManagerFactory;
    
    // Flag para habilitar fallback legacy (por defecto: cache activo)
    private final boolean useLegacyMode;

    public FindByParameterUseCase(String parameterColumn, String value) {
        this.parameterColumn = parameterColumn;
        this.value = value;
        this.cacheService = WacherParametrosCacheSimple.getInstance();
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
        this.useLegacyMode = false; // Cache activo por defecto
    }
    
    /**
     * Constructor para modo legacy (solo para casos especiales)
     */
    public FindByParameterUseCase(String parameterColumn, String value, boolean useLegacyMode) {
        this.parameterColumn = parameterColumn;
        this.value = value;
        this.cacheService = WacherParametrosCacheSimple.getInstance();
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
        this.useLegacyMode = useLegacyMode;
    }

    @Override
    public Boolean execute() {
        // Validación de entrada
        if ("codigo".equals(parameterColumn) && value != null) {
            
            // MODO CACHE (por defecto) - Optimizado para alto rendimiento
            if (!useLegacyMode) {
                try {
                    long startTime = System.currentTimeMillis();
                    String cachedValue = cacheService.getParameter(value);
                    long duration = System.currentTimeMillis() - startTime;
                    
                    boolean result = "S".equals(cachedValue);
                    
                    logger.fine(String.format(
                        "[CACHE-MODE] FindByParameterUseCase: %s = %s (%dms) [Cache]",
                        value, result, duration
                    ));
                    
                    return result;
                    
                } catch (Exception e) {
                    logger.log(Level.WARNING, 
                        "❌ Error en cache para parámetro: " + value + ", fallback a BD", e);
                    // Continúa con fallback legacy
                }
            }
        }
        
        // MODO LEGACY - Fallback a consulta directa BD
        return executeLegacyMode();
    }
    
    /**
     * Modo legacy para fallback cuando cache falla
     */
    private Boolean executeLegacyMode() {
        EntityManager entityManager = null;
        try {
            long startTime = System.currentTimeMillis();
            entityManager = entityManagerFactory.createEntityManager();
            CtWacherParametroRepository ctWacherParametroRepository = new CtWacherParametroRepository(entityManager);
            Boolean result = ctWacherParametroRepository.findByParameter(parameterColumn, value)
                    .map(parametro -> "S".equals(parametro.getValor()))
                    .orElse(false);
            
            long duration = System.currentTimeMillis() - startTime;
            
            logger.info(String.format(
                "[LEGACY-MODE] FindByParameterUseCase: %s.%s = %s (%dms) [BD]",
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
