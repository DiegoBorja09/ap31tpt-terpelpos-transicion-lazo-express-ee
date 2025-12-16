package com.application.useCases.wacherparametros;

import com.application.core.BaseUseCases;
import com.infrastructure.cache.WacherParametrosCacheSimple;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Use Case para obtener parametros de wacher_parametros usando cache
 * 
 * Este Use Case reemplaza las consultas directas a la BD con un cache inteligente
 * que reduce drasticamente el numero de consultas a wacher_parametros.
 * 
 * Problema resuelto:
 * - 300,000+ consultas diarias a wacher_parametros
 * - Latencia alta por consultas repetitivas
 * - Carga innecesaria en PostgreSQL
 * 
 * Solucion implementada:
 * - Cache en memoria con los TOP 5 parametros mas consultados
 * - Refresh automatico cada 24 horas
 * - Fallback a BD en caso de cache miss
 * - Metricas y logging completo
 */
public class GetParameterCachedUseCase implements BaseUseCases<String> {
    
    private static final Logger logger = Logger.getLogger(GetParameterCachedUseCase.class.getName());
    
    private final String parameterCode;
    private final WacherParametrosCacheSimple cacheService;
    
    /**
     * Constructor
     * @param parameterCode Codigo del parametro a consultar (ej: "REMISION", "POS_ID")
     */
    public GetParameterCachedUseCase(String parameterCode) {
        this.parameterCode = parameterCode;
        this.cacheService = WacherParametrosCacheSimple.getInstance();
    }
    
    /**
     * Ejecuta la consulta del parametro usando cache
     * 
     * Flujo:
     * 1. Consulta el cache primero (esperado: 95%+ hit rate)
     * 2. Si cache miss, consulta BD y actualiza cache
     * 3. Retorna el valor o null si no existe
     * 
     * @return Valor del parametro o null si no existe
     */
    @Override
    public String execute() {
        if (parameterCode == null || parameterCode.trim().isEmpty()) {
            logger.warning("❌ [GetParameterCachedUseCase] Codigo de parametro vacio");
            return null;
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Obtener del cache (incluye fallback automatico a BD si es necesario)
            String value = cacheService.getParameter(parameterCode);
            
            long duration = System.currentTimeMillis() - startTime;
            
            if (value != null) {
                logger.fine("✅ [GetParameterCachedUseCase] " + parameterCode + " = " + value + " (" + duration + "ms)");
            } else {
                logger.warning("⚠️ [GetParameterCachedUseCase] Parametro no encontrado: " + parameterCode + " (" + duration + "ms)");
            }
            
            return value;
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.log(Level.SEVERE, "❌ [GetParameterCachedUseCase] Error consultando: " + parameterCode + " (" + duration + "ms)", e);
            return null;
        }
    }
    
    /**
     * Metodo de conveniencia para obtener parametros boolean
     * Util para parametros como REMISION, OBLIGATORIO_FE, etc.
     */
    public Boolean executeAsBoolean() {
        String value = execute();
        if (value == null) {
            return null;
        }
        
        // Normalizar valores boolean
        return "S".equalsIgnoreCase(value) || 
               "true".equalsIgnoreCase(value) || 
               "1".equals(value);
    }
    
    /**
     * Metodo de conveniencia para obtener parametros numericos
     * Util para parametros como MONTO_MINIMO_FE, POS_ID, etc.
     */
    public Float executeAsFloat() {
        String value = execute();
        if (value == null) {
            return null;
        }
        
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            logger.warning("⚠️ [GetParameterCachedUseCase] Error parsing float: " + value + " for parameter: " + parameterCode);
            return null;
        }
    }
    
    /**
     * Metodo de conveniencia para obtener parametros enteros
     * Util para parametros como POS_ID, TIMEOUT_ESPERA_LECTURAS, etc.
     */
    public Integer executeAsInteger() {
        String value = execute();
        if (value == null) {
            return null;
        }
        
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warning("⚠️ [GetParameterCachedUseCase] Error parsing integer: " + value + " for parameter: " + parameterCode);
            return null;
        }
    }
}
