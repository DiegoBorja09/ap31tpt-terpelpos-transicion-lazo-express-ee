package com.application.useCases.wacherparametros;

import com.application.core.BaseUseCases;
import com.application.commons.CtWacherParametrosEnum;
import com.infrastructure.cache.WacherParametrosCacheSimple;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.CtWacherParametroRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Use Case para obtener valores de parámetros wacher_parametros con cache
 * 
 * MIGRADO A CACHE: Ahora utiliza WacherParametrosCacheSimple para
 * optimizar las consultas más frecuentes del sistema.
 * 
 * Impacto esperado:
 * - Response time: 50-200ms → <1ms
 * - Consultas BD: Reducción 99%+
 */
public class FindParameterValueUseCase implements BaseUseCases<String> {

    private static final Logger logger = Logger.getLogger(FindParameterValueUseCase.class.getName());
    
    private final String parameterColumn;
    private final String value;
    private final WacherParametrosCacheSimple cacheService;
    private final EntityManagerFactory entityManagerFactory;
    private final boolean useLegacyMode;

    public FindParameterValueUseCase(String parameterColumn, String value) {
        this.parameterColumn = parameterColumn;
        this.value = value;
        this.cacheService = WacherParametrosCacheSimple.getInstance();
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
        this.useLegacyMode = false; // Cache activo por defecto
    }
    
    /**
     * Constructor con control de modo legacy
     */
    public FindParameterValueUseCase(String parameterColumn, String value, boolean useLegacyMode) {
        this.parameterColumn = parameterColumn;
        this.value = value;
        this.cacheService = WacherParametrosCacheSimple.getInstance();
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
        this.useLegacyMode = useLegacyMode;
    }

    @Override
    public String execute() {
        // Usar cache si el parámetro es por código (caso más frecuente)
        if ("codigo".equals(parameterColumn) && value != null && !useLegacyMode) {
            try {
                long startTime = System.currentTimeMillis();
                String cachedValue = cacheService.getParameter(value);
                long duration = System.currentTimeMillis() - startTime;
                
                logger.fine(String.format(
                    "[CACHE-MODE] FindParameterValueUseCase: %s = %s (%dms) [Cache]",
                    value, cachedValue, duration
                ));
                
                return cachedValue;
                
            } catch (Exception e) {
                logger.log(Level.WARNING, 
                    "❌ Error en cache para parámetro: " + value + ", fallback a BD", e);
                // Continúa con fallback legacy
            }
        }
        
        // Fallback legacy o consultas no estándar
        return executeLegacyMode();
    }
    
    /**
     * Modo legacy - consulta directa a BD
     */
    private String executeLegacyMode() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            long startTime = System.currentTimeMillis();
            CtWacherParametroRepository ctWacherParametroRepository = new CtWacherParametroRepository(entityManager);
            String result = ctWacherParametroRepository.findByParameter(parameterColumn, value)
                    .map(parametro -> parametro.getValor())
                    .orElse(null);
            
            long duration = System.currentTimeMillis() - startTime;
            
            logger.info(String.format(
                "[LEGACY-MODE] FindParameterValueUseCase: %s.%s = %s (%dms) [BD]",
                parameterColumn, value, result, duration
            ));
            
            return result;
            
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
    
    /**
     * Obtiene el valor del parámetro como entero con manejo de errores
     * @param defaultValue Valor por defecto si no se encuentra o hay error de conversión
     * @return El valor como entero o el valor por defecto
     */
    public int executeAsInt(int defaultValue) {
        String valor = execute();
        
        if (valor == null || valor.trim().isEmpty()) {
            Logger.getLogger(FindParameterValueUseCase.class.getName())
                  .log(Level.WARNING, "⚠️ Parámetro no encontrado o valor vacío para: " + value + ", usando valor por defecto: " + defaultValue);
            return defaultValue;
        }
        
        try {
            int resultado = Integer.parseInt(valor.trim());
            
            if (resultado <= 0) {
                Logger.getLogger(FindParameterValueUseCase.class.getName())
                      .log(Level.WARNING, "⚠️ Valor inválido para parámetro " + value + ": " + resultado + ", usando valor por defecto: " + defaultValue);
                return defaultValue;
            }
            
            return resultado;
            
        } catch (NumberFormatException e) {
            Logger.getLogger(FindParameterValueUseCase.class.getName())
                  .log(Level.WARNING, "⚠️ Error al convertir valor del parámetro " + value + ": '" + valor + "', usando valor por defecto: " + defaultValue, e);
            return defaultValue;
        }
    }
    
    /**
     * Método estático de conveniencia para consultar el tiempo máximo de datos del cliente
     * Reemplaza: MovimientosDao.buscarTiempoMaximoDatosCliente()
     * @return Tiempo máximo en minutos (por defecto 5)
     */
    public static int consultarTiempoMaximoDatosCliente() {
        FindParameterValueUseCase useCase = new FindParameterValueUseCase(
            CtWacherParametrosEnum.CODIGO.getColumnName(),
            "TIEMPO_MAXIMO_DATOS_CLIENTE_FE"
        );
        return useCase.executeAsInt(5); // 5 minutos por defecto
    }
}
