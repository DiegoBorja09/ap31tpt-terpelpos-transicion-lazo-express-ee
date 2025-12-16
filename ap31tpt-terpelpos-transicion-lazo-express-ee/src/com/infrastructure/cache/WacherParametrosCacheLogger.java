package com.infrastructure.cache;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * SISTEMA DE LOGGING AVANZADO PARA CACHE WACHER_PARAMETROS
 * 
 * Proporciona trazabilidad completa de todas las operaciones del cache
 * con métricas detalladas, contexto de negocio y facilidad de debugging.
 * 
 * CARACTERÍSTICAS:
 * - Logs estructurados con contexto de operación
 * - Métricas en tiempo real de performance
 * - Trazabilidad de request ID para debugging
 * - Alertas automáticas por thresholds
 * - Logs de auditoría para compliance
 * - Performance tracking detallado
 */
public class WacherParametrosCacheLogger {
    
    private static final Logger logger = Logger.getLogger(WacherParametrosCacheLogger.class.getName());
    
    // Formatter para timestamps consistentes
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    // Contadores para métricas
    private static final AtomicLong sessionId = new AtomicLong(0);
    private static final AtomicLong requestCounter = new AtomicLong(0);
    
    // Thresholds para alertas
    private static final long SLOW_QUERY_THRESHOLD_MS = 100;    // BD query > 100ms
    private static final long CACHE_MISS_ALERT_COUNT = 10;      // > 10 misses consecutivos
    private static final double LOW_HIT_RATE_THRESHOLD = 85.0;  // Hit rate < 85%
    
    /**
     * ========== LOGS DE OPERACIONES DE CACHE ==========
     */
    
    /**
     * Log de Cache Hit - Parámetro encontrado en memoria
     */
    public static void logCacheHit(String codigo, String valor, String caller) {
        String requestId = generateRequestId();
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        
        String mensaje = String.format(
            "[CACHE-HIT] [%s] [%s] parametro='%s' valor='%s' origen=MEMORIA tiempo=<1ms caller=%s",
            requestId, timestamp, codigo, 
            truncateValue(valor), caller != null ? caller : "unknown"
        );
        
        logger.info(mensaje);
    }
    
    /**
     * Log de Cache Miss - Parámetro NO encontrado en cache, va a BD
     */
    public static void logCacheMiss(String codigo, String caller, String reason) {
        String requestId = generateRequestId();
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        
        String mensaje = String.format(
            "[CACHE-MISS] [%s] [%s] parametro='%s' razon='%s' accion=CONSULTA_BD caller=%s",
            requestId, timestamp, codigo, reason, caller != null ? caller : "unknown"
        );
        
        logger.warning(mensaje);
    }
    
    /**
     * Log de consulta exitosa a Base de Datos
     */
    public static void logDatabaseQuery(String codigo, String valor, long duracionMs, String caller) {
        String requestId = getCurrentRequestId();
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        
        // Determinar nivel según performance
        Level level = duracionMs > SLOW_QUERY_THRESHOLD_MS ? Level.WARNING : Level.INFO;
        String performance = duracionMs > SLOW_QUERY_THRESHOLD_MS ? "SLOW" : "OK";
        
        String mensaje = String.format(
            "[BD-QUERY] [%s] [%s] parametro='%s' valor='%s' duracion=%dms performance=%s caller=%s",
            requestId, timestamp, codigo, truncateValue(valor), duracionMs, performance, caller != null ? caller : "unknown"
        );
        
        logger.log(level, mensaje);
        
        // Alerta si es consulta lenta
        if (duracionMs > SLOW_QUERY_THRESHOLD_MS) {
            logSlowQueryAlert(codigo, duracionMs, caller);
        }
    }
    
    /**
     * Log de parámetro agregado al cache desde BD
     */
    public static void logCacheUpdate(String codigo, String valor, String caller) {
        String requestId = getCurrentRequestId();
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        
        String mensaje = String.format(
            "[CACHE-UPDATE] [%s] [%s] parametro='%s' valor='%s' accion=AGREGADO_CACHE caller=%s",
            requestId, timestamp, codigo, truncateValue(valor), caller != null ? caller : "unknown"
        );
        
        logger.info(mensaje);
    }
    
    /**
     * ========== LOGS DE REFRESH Y MANTENIMIENTO ==========
     */
    
    /**
     * Log de inicio de refresh automático (TTL expirado)
     */
    public static void logRefreshStart(String trigger, int parametrosACarga) {
        String sessionIdStr = generateSessionId();
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        
        String mensaje = String.format(
            "[REFRESH-START] [%s] [%s] trigger='%s' parametros_a_cargar=%d estado=INICIANDO",
            sessionIdStr, timestamp, trigger, parametrosACarga
        );
        
        logger.info(mensaje);
    }
    
    /**
     * Log de finalización de refresh
     */
    public static void logRefreshComplete(String sessionIdStr, int cargados, int errores, long duracionMs) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        
        String mensaje = String.format(
            "[REFRESH-COMPLETE] [%s] [%s] cargados=%d errores=%d duracion=%dms estado=COMPLETADO",
            sessionIdStr, timestamp, cargados, errores, duracionMs
        );
        
        logger.info(mensaje);
        
        // Alerta si hay demasiados errores
        if (errores > 0) {
            logRefreshErrorAlert(sessionIdStr, errores, cargados);
        }
    }
    
    /**
     * ========== LOGS DE MÉTRICAS Y PERFORMANCE ==========
     */
    
    /**
     * Log de métricas de hit rate
     */
    public static void logHitRateMetrics(long hits, long misses, double hitRatePercent) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        
        String mensaje = String.format(
            "[METRICS] [%s] hits=%d misses=%d hit_rate=%.2f%% total_requests=%d",
            timestamp, hits, misses, hitRatePercent, hits + misses
        );
        
        Level level = hitRatePercent < LOW_HIT_RATE_THRESHOLD ? Level.WARNING : Level.INFO;
        logger.log(level, mensaje);
        
        // Alerta si hit rate es bajo
        if (hitRatePercent < LOW_HIT_RATE_THRESHOLD) {
            logLowHitRateAlert(hitRatePercent);
        }
    }
    
    /**
     * Log de estado actual del cache
     */
    public static void logCacheStatus(int tamanoCache, long ultimaCarga, long proximoRefresh) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        
        String mensaje = String.format(
            "[CACHE-STATUS] [%s] size=%d ultima_carga=%d proximo_refresh_en=%d_horas",
            timestamp, tamanoCache, ultimaCarga, proximoRefresh
        );
        
        logger.info(mensaje);
    }
    
    /**
     * ========== LOGS DE ALERTAS Y ERRORES ==========
     */
    
    /**
     * Alerta de consulta BD lenta
     */
    private static void logSlowQueryAlert(String codigo, long duracionMs, String caller) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        
        String mensaje = String.format(
            "[ALERT-SLOW-QUERY] [%s] parametro='%s' duracion=%dms threshold=%dms caller=%s ACCION_REQUERIDA=OPTIMIZAR",
            timestamp, codigo, duracionMs, SLOW_QUERY_THRESHOLD_MS, caller != null ? caller : "unknown"
        );
        
        logger.warning(mensaje);
    }
    
    /**
     * Alerta de hit rate bajo
     */
    private static void logLowHitRateAlert(double hitRatePercent) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        
        String mensaje = String.format(
            "[ALERT-LOW-HIT-RATE] [%s] hit_rate=%.2f%% threshold=%.2f%% ACCION_REQUERIDA=REVISAR_CONFIGURACION",
            timestamp, hitRatePercent, LOW_HIT_RATE_THRESHOLD
        );
        
        logger.warning(mensaje);
    }
    
    /**
     * Alerta de errores en refresh
     */
    public static void logRefreshErrorAlert(String sessionId, int errores, int cargados) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        
        String mensaje = String.format(
            "[ALERT-REFRESH-ERRORS] [%s] [%s] errores=%d cargados=%d ACCION_REQUERIDA=REVISAR_BD_CONEXION",
            sessionId, timestamp, errores, cargados
        );
        
        logger.warning(mensaje);
    }
    
    /**
     * ========== LOGS DE DEBUGGING Y AUDITORÍA ==========
     */
    
    /**
     * Log de debugging para desarrollo
     */
    public static void logDebug(String operacion, String detalle) {
        if (logger.isLoggable(Level.FINE)) {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            
            String mensaje = String.format(
                "[DEBUG] [%s] operacion='%s' detalle='%s'",
                timestamp, operacion, detalle
            );
            
            logger.fine(mensaje);
        }
    }
    
    /**
     * Log de auditoría para compliance
     */
    public static void logAudit(String usuario, String accion, String parametro, String valorAnterior, String valorNuevo) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        
        String mensaje = String.format(
            "[AUDIT] [%s] usuario='%s' accion='%s' parametro='%s' valor_anterior='%s' valor_nuevo='%s'",
            timestamp, usuario != null ? usuario : "SYSTEM", accion, parametro, 
            truncateValue(valorAnterior), truncateValue(valorNuevo)
        );
        
        logger.info(mensaje);
    }
    
    /**
     * ========== MÉTODOS UTILITARIOS ==========
     */
    
    /**
     * Genera ID único para request
     */
    private static String generateRequestId() {
        return "REQ-" + String.format("%08d", requestCounter.incrementAndGet());
    }
    
    /**
     * Obtiene el request ID actual del thread
     */
    private static String getCurrentRequestId() {
        return "REQ-" + String.format("%08d", requestCounter.get());
    }
    
    /**
     * Genera ID único para sesión de refresh
     */
    private static String generateSessionId() {
        return "SES-" + String.format("%06d", sessionId.incrementAndGet());
    }
    
    /**
     * Trunca valores largos para logs legibles
     */
    private static String truncateValue(String valor) {
        if (valor == null) return "null";
        if (valor.length() > 50) {
            return valor.substring(0, 47) + "...";
        }
        return valor;
    }
    
    /**
     * ========== CONFIGURACIÓN DE LOGGING ==========
     */
    
    /**
     * Configura el nivel de logging dinámicamente
     */
    public static void setLogLevel(Level level) {
        logger.setLevel(level);
        logDebug("CONFIG", "Nivel de logging cambiado a: " + level.getName());
    }
    
    /**
     * Verifica si el debugging está habilitado
     */
    public static boolean isDebugEnabled() {
        return logger.isLoggable(Level.FINE);
    }
}
