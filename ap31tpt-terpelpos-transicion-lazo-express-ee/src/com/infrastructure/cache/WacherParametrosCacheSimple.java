package com.infrastructure.cache;

import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.domain.entities.CtWacherParametroEntity;
import com.infrastructure.repositories.CtWacherParametroRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Cache OPTIMIZADO para wacher_parametros con REFRESH PROGRAMADO A LAS 3:00 AM
 * 
 * ENFOQUE OPTIMIZADO:
 * - Refresh DIARIO automático a las 3:00 AM (horario de menor carga)
 * - Cache válido por 24 horas desde última actualización  
 * - Carga PRIORITARIA basada en frecuencia de uso real
 * - Fallback a BD solo cuando cache está vacío o hay error
 * - Configuración simple y mantenimiento predictible
 * 
 * Problema resuelto: 300,000+ consultas diarias hacia wacher_parametros
 * TOP 5 más críticos: REMISION, OBLIGATORIO_FE, MONTO_MINIMO_FE, POS_ID, MENSAJES_FE
 * Solución: Cache con refresh nocturno + fallback inteligente
 */
public class WacherParametrosCacheSimple {
    
    private static final Logger logger = Logger.getLogger(WacherParametrosCacheSimple.class.getName());
    
    // Singleton instance
    private static volatile WacherParametrosCacheSimple instance;
    
    // Configuration manager
    private final WacherParametrosCacheConfig config;
    
    // Cache storage SIMPLE - Solo String values con TTL global
    private final ConcurrentHashMap<String, String> parametersCache;
    
    // Timestamp de carga del cache (para TTL global)
    private volatile LocalDateTime cacheLoadTime;
    
    // Scheduler para refresh cada 24h
    private final ScheduledExecutorService scheduler;
    
    // Entity Manager Factory
    private final EntityManagerFactory entityManagerFactory;
    
    // Control de inicialización
    private volatile boolean cacheInitialized = false;
    
    // Métricas básicas
    private volatile long hitCount = 0;
    private volatile long missCount = 0;
    
    private WacherParametrosCacheSimple() {
        // Cargar configuración
        this.config = WacherParametrosCacheConfig.getInstance();
        
        // Verificar si cache está habilitado
        if (!config.isCacheEnabled()) {
            throw new RuntimeException("Cache wacher_parametros deshabilitado en configuración");
        }
        
        // Inicializar cache simple
        this.parametersCache = new ConcurrentHashMap<>(
            config.getInitialCapacity(), 
            0.75f, 
            config.getConcurrencyLevel()
        );
        
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
            .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
        
        // Inicializar cache al arrancar
        if (config.isRefreshAtStartup()) {
            loadAllParameters();
        }
        
        // Programar refresh diario en horario configurado
        if (config.isRefreshEnabled() && config.isDailyRefreshEnabled()) {
            scheduleDailyRefreshAtConfiguredTime();
        } else if (config.isRefreshEnabled()) {
            // Fallback a refresh periódico
            schedulePeriodicRefresh();
        }
        
        // Programar logs de estado cada 30 minutos (configurable)
        if (config.getBooleanProperty("cache.wacher.status.log.enabled", true)) {
            schedulePeriodicStatusLogs();
        }
        
        logger.info(" ============ CACHE WACHER_PARAMETROS INICIADO ============");
        logger.info("    Configuración: cache-config.properties");
        logger.info("   ️  Parámetros configurados: " + getParametersListFromConfig().length);
        logger.info("    TTL global: " + getGlobalTTLHours() + " horas");
        String refreshInfo = "NO";
        if (config.isRefreshEnabled()) {
            if (config.isDailyRefreshEnabled()) {
                refreshInfo = "DIARIO a las " + config.getDailyRefreshTime() + " (" + config.getRefreshTimezone() + ")";
            } else {
                refreshInfo = "PERIÓDICO cada " + config.getRefreshIntervalHours() + "h";
            }
        }
        logger.info("    Refresh automático: " + refreshInfo);
        logger.info("============================================================");
    }
    
    public static WacherParametrosCacheSimple getInstance() {
        if (instance == null) {
            synchronized (WacherParametrosCacheSimple.class) {
                if (instance == null) {
                    instance = new WacherParametrosCacheSimple();
                }
            }
        }
        return instance;
    }
    
    /**
     * Obtiene un parámetro del cache
     * Si el cache está expirado (>24h), refresca TODO el cache
     */
    public String getParameter(String codigo) {
        String caller = getCaller();
        
        if (codigo == null || codigo.trim().isEmpty()) {
            WacherParametrosCacheLogger.logDebug("VALIDATION_ERROR", "Código de parámetro vacío desde " + caller);
            return null;
        }
        
        // Verificar si el cache está expirado (TTL global)
        if (isCacheExpired()) {
            missCount++; // Cache miss por expiración
            WacherParametrosCacheLogger.logCacheMiss(codigo, caller, "TTL_EXPIRED");
            WacherParametrosCacheLogger.logRefreshStart("TTL_EXPIRED", getParametersListFromConfig().length);
            
            long refreshStart = System.currentTimeMillis();
            loadAllParameters(); // Recargar TODO
            long refreshEnd = System.currentTimeMillis();
            
            // Ahora obtener el valor del cache fresco
            String value = parametersCache.get(codigo);
            if (value != null) {
                hitCount++; // Aunque fue miss inicial, ahora es hit
                WacherParametrosCacheLogger.logCacheHit(codigo, value, caller);
                WacherParametrosCacheLogger.logDebug("REFRESH_RECOVERY", "Parámetro recuperado después de refresh TTL");
            } else {
                WacherParametrosCacheLogger.logDebug("REFRESH_ERROR", "Parámetro no encontrado después de refresh completo");
            }
            return value;
        }
        
        // Cache vigente - obtener valor directamente
        String value = parametersCache.get(codigo);
        
        if (value != null) {
            hitCount++;
            WacherParametrosCacheLogger.logCacheHit(codigo, value, caller);
            return value;
        }
        
        // Cache miss - parámetro no está en cache pero cache no expiró
        missCount++;
        WacherParametrosCacheLogger.logCacheMiss(codigo, caller, "NOT_IN_CACHE");
        
        // Consultar solo este parámetro y agregarlo al cache
        long startTime = System.currentTimeMillis();
        String dbValue = queryParameterFromDatabase(codigo);
        long endTime = System.currentTimeMillis();
        long duracionMs = endTime - startTime;
        
        if (dbValue != null) {
            parametersCache.put(codigo, dbValue);
            WacherParametrosCacheLogger.logDatabaseQuery(codigo, dbValue, duracionMs, caller);
            WacherParametrosCacheLogger.logCacheUpdate(codigo, dbValue, caller);
        } else {
            WacherParametrosCacheLogger.logDatabaseQuery(codigo, "NOT_FOUND", duracionMs, caller);
            WacherParametrosCacheLogger.logDebug("BD_EMPTY", "Parámetro no existe en tabla wacher_parametros: " + codigo);
        }
        
        return dbValue;
    }
    
    /**
     * Métodos de conveniencia para TOP 5 parámetros
     */
    public boolean isRemisionActiva() {
        String valor = getParameter("REMISION");
        return "S".equals(valor);
    }
    
    public boolean isObligatorioFE() {
        String valor = getParameter("OBLIGATORIO_FE");
        return "S".equals(valor);
    }
    
    public float getMontoMinimoFE() {
        String valor = getParameter("MONTO_MINIMO_FE");
        try {
            return valor != null ? Float.parseFloat(valor) : -1.0f;
        } catch (NumberFormatException e) {
            logger.warning("⚠️ Error parsing MONTO_MINIMO_FE: " + valor);
            return -1.0f;
        }
    }
    
    public int getPosId() {
        String valor = getParameter("POS_ID");
        try {
            return valor != null ? Integer.parseInt(valor) : -1;
        } catch (NumberFormatException e) {
            logger.warning("⚠️ Error parsing POS_ID: " + valor);
            return -1;
        }
    }
    
    public String getMensajesFE() {
        return getParameter("MENSAJES_FE");
    }
    
    /**
     * Verifica si el cache está expirado según TTL global
     */
    private boolean isCacheExpired() {
        if (cacheLoadTime == null) {
            return true; // Cache nunca cargado
        }
        
        long hoursAgo = java.time.Duration.between(cacheLoadTime, LocalDateTime.now()).toHours();
        long globalTTLHours = getGlobalTTLHours();
        
        return hoursAgo >= globalTTLHours;
    }
    
    /**
     * Obtiene TTL global desde configuración
     */
    private long getGlobalTTLHours() {
        // Usar configuración global, fallback a 24h
        return config.getTTLHours("global"); // Desde cache.wacher.ttl.global=24h
    }
    
    /**
     * Carga TODOS los parámetros configurados desde BD
     */
    private void loadAllParameters() {
        logger.info("🚀 Cargando TODOS los parámetros wacher_parametros...");
        
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CtWacherParametroRepository repository = new CtWacherParametroRepository(entityManager);
            
            // EJECUTAR DEBUG ANTES DE CARGAR PARÁMETROS
            logger.info("🔍 DEBUG: Verificando estado de tabla wacher_parametros...");
            try {
                javax.persistence.Query countQuery = entityManager.createNativeQuery(
                    "SELECT COUNT(*) FROM wacher_parametros"
                );
                Object countResult = countQuery.getSingleResult();
                logger.info("🔍 DEBUG: Número total de registros en wacher_parametros: " + countResult);
                
                // Si hay registros, mostrar algunos ejemplos
                if (countResult != null && Integer.parseInt(countResult.toString()) > 0) {
                    javax.persistence.Query debugQuery = entityManager.createNativeQuery(
                        "SELECT codigo, valor FROM wacher_parametros LIMIT 10"
                    );
                    java.util.List<Object[]> allData = debugQuery.getResultList();
                    logger.info("🔍 DEBUG: Primeros 10 registros encontrados:");
                    for (Object[] row : allData) {
                        logger.info("🔍 DEBUG:   código: '" + row[0] + "' = valor: '" + row[1] + "'");
                    }
                } else {
                    logger.warning("⚠️ DEBUG: La tabla wacher_parametros está COMPLETAMENTE VACÍA");
                    logger.warning("⚠️ DEBUG: Necesita ejecutar scripts de inicialización de datos");
                }
            } catch (Exception debugEx) {
                logger.severe("❌ DEBUG: Error verificando tabla wacher_parametros: " + debugEx.getMessage());
                
                // Intentar verificar si la tabla existe
                try {
                    javax.persistence.Query existQuery = entityManager.createNativeQuery(
                        "SELECT table_name FROM information_schema.tables WHERE table_name = 'wacher_parametros'"
                    );
                    java.util.List<Object> tableExists = existQuery.getResultList();
                    if (tableExists.isEmpty()) {
                        logger.severe("❌ DEBUG: La tabla 'wacher_parametros' NO EXISTE en la base de datos");
                    } else {
                        logger.info("🔍 DEBUG: La tabla 'wacher_parametros' SÍ existe pero puede estar vacía");
                    }
                } catch (Exception tableEx) {
                    logger.severe("❌ DEBUG: Error verificando existencia de tabla: " + tableEx.getMessage());
                }
            }
            
            // Obtener lista de parámetros a cargar desde configuración
            String[] parametersToLoad = getParametersListFromConfig();
            
            // Limpiar cache actual
            parametersCache.clear();
            
            int loadedCount = 0;
            int errorCount = 0;
            
            // Cargar cada parámetro configurado
            for (String codigo : parametersToLoad) {
                try {
                    String valor = queryParameterFromRepository(repository, codigo);
                    if (valor != null) {
                        parametersCache.put(codigo, valor);
                        loadedCount++;
                        logger.info("----> CACHE CARGADO: " + codigo + " = " + valor);
                    } else {
                        errorCount++;
                        logger.warning("⚠️ CACHE ERROR: Parámetro no encontrado en BD: " + codigo);
                    }
                } catch (Exception e) {
                    errorCount++;
                    logger.log(Level.WARNING, "❌ CACHE ERROR: Error cargando parámetro: " + codigo, e);
                }
            }
            
            // Actualizar timestamp de carga
            cacheLoadTime = LocalDateTime.now();
            cacheInitialized = true;
            
            // Log detallado de carga completa
            logger.info("✅ CACHE INICIALIZADO COMPLETO:");
            logger.info("   📊 Parámetros cargados: " + loadedCount + "/" + parametersToLoad.length);
            logger.info("   ❌ Errores: " + errorCount);
            logger.info("   🗄️ Tamaño cache actual: " + parametersCache.size() + " parámetros");
            logger.info("   ⏰ Cargado: " + cacheLoadTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            logger.info("   📅 Próximo refresh: " + cacheLoadTime.plusHours(getGlobalTTLHours()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            logger.info("   🚀 TTL global: " + getGlobalTTLHours() + " horas");
            
            // Log lista de parámetros cargados exitosamente
            if (loadedCount > 0) {
                StringBuilder loadedParams = new StringBuilder();
                loadedParams.append("----> CACHE CONTENIDO: ");
                parametersCache.keySet().forEach(key -> loadedParams.append(key).append(", "));
                String contenido = loadedParams.toString();
                if (contenido.endsWith(", ")) {
                    contenido = contenido.substring(0, contenido.length() - 2);
                }
                logger.info(contenido);
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error cargando cache completo", e);
            cacheInitialized = false;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
    
    /**
     * Obtiene lista de parámetros desde configuración (formato array)
     */
    private String[] getParametersListFromConfig() {
        // Leer parámetros en formato array: cache.wacher.parameters[0]=PARAM1, cache.wacher.parameters[1]=PARAM2, etc.
        java.util.List<String> parametersList = new java.util.ArrayList<>();
        
        int index = 0;
        String parameterKey = "cache.wacher.parameters[" + index + "]";
        String parameterValue = config.getProperty(parameterKey, null);
        
        // Leer todos los parámetros del array hasta que no haya más
        while (parameterValue != null) {
            // Limpiar valor: trim() y remover comentarios que empiecen con #
            String cleanValue = parameterValue.trim();
            int commentIndex = cleanValue.indexOf('#');
            if (commentIndex >= 0) {
                cleanValue = cleanValue.substring(0, commentIndex).trim();
            }
            
            if (!cleanValue.isEmpty()) {
                parametersList.add(cleanValue);
            }
            
            index++;
            parameterKey = "cache.wacher.parameters[" + index + "]";
            parameterValue = config.getProperty(parameterKey, null);
        }
        
        // Si no se encontraron parámetros en formato array, intentar formato lista como fallback
        if (parametersList.isEmpty()) {
            String legacyList = config.getProperty("cache.wacher.parameters.list", null);
            if (legacyList != null) {
                logger.warning("⚠️ Usando formato legacy 'cache.wacher.parameters.list' - considerar migrar a formato array");
                return legacyList.split(",");
            } else {
                // Fallback a TOP 5 por defecto
                logger.warning("⚠️ No se encontraron parámetros configurados - usando TOP 5 por defecto");
                return new String[]{"REMISION", "OBLIGATORIO_FE", "MONTO_MINIMO_FE", "POS_ID", "MENSAJES_FE"};
            }
        }
        
        logger.info("----> Cargados " + parametersList.size() + " parámetros desde configuración array");
        
        // DEBUG: Mostrar exactamente qué parámetros se parsearon
        for (int i = 0; i < parametersList.size(); i++) {
            logger.info("🔍 PARAM[" + i + "]: '" + parametersList.get(i) + "'");
        }
        
        return parametersList.toArray(new String[0]);
    }
    
    /**
     * Programa refresh automático DIARIO en horario configurado
     * Lee la hora desde cache-config.properties
     */
    private void scheduleDailyRefreshAtConfiguredTime() {
        try {
            // Obtener configuración
            int[] timeConfig = config.parseDailyRefreshTime();
            int targetHour = timeConfig[0];
            int targetMinute = timeConfig[1];
            String timezone = config.getRefreshTimezone();
            
            // Configurar zona horaria
            ZoneId zoneId;
            try {
                zoneId = ZoneId.of(timezone);
            } catch (Exception e) {
                logger.warning("⚠️ Zona horaria inválida: " + timezone + ", usando zona del sistema");
                zoneId = ZoneId.systemDefault();
            }
            
            // Calcular el delay hasta el próximo horario configurado
            LocalTime targetTime = LocalTime.of(targetHour, targetMinute, 0);
            ZonedDateTime now = ZonedDateTime.now(zoneId);
            ZonedDateTime nextRun = now.with(targetTime);
            
            // Si ya pasó la hora hoy, programar para mañana
            if (now.compareTo(nextRun) > 0) {
                nextRun = nextRun.plusDays(1);
            }
            
            long initialDelayMinutes = ChronoUnit.MINUTES.between(now, nextRun);
            long intervalMinutes = 24 * 60; // 24 horas en minutos
            
            // Programar ejecución
            scheduler.scheduleAtFixedRate(
                this::executeDailyRefresh,
                initialDelayMinutes,   // Delay hasta próximo horario configurado
                intervalMinutes,       // Repetir cada 24 horas
                TimeUnit.MINUTES
            );
            
            logger.info("⏰ REFRESH DIARIO PROGRAMADO DESDE CONFIGURACIÓN:");
            logger.info("   🕒 Horario configurado: " + String.format("%02d:%02d", targetHour, targetMinute));
            logger.info("   🌍 Zona horaria: " + zoneId.getId());
            logger.info("   ⏳ Próxima ejecución: " + nextRun.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")));
            logger.info("   ⏱️ Delay inicial: " + initialDelayMinutes + " minutos");
            logger.info("   🔄 Intervalo: 24 horas");
            logger.info("   📄 Configuración: cache.wacher.refresh.daily.time=" + config.getDailyRefreshTime());
            
        } catch (Exception e) {
            logger.severe("❌ Error programando refresh diario desde configuración: " + e.getMessage());
            
            // Fallback a método periódico
            logger.warning("⚠️ Fallback: usando refresh periódico en lugar de diario");
            schedulePeriodicRefresh();
        }
    }
    
    /**
     * Método de fallback: refresh periódico (modo legacy)
     */
    private void schedulePeriodicRefresh() {
        int intervalHours = config.getRefreshIntervalHours();
        
        scheduler.scheduleAtFixedRate(
            this::executeDailyRefresh,
            intervalHours, // Delay inicial
            intervalHours, // Período
            TimeUnit.HOURS
        );
        
        logger.info("⏰ Programado refresh periódico (fallback): cada " + intervalHours + " horas");
    }
    
    /**
     * Programa logs de estado del cache cada 30 minutos
     */
    private void schedulePeriodicStatusLogs() {
        // Leer intervalo desde configuración (default 30 minutos)
        String intervalConfig = config.getProperty("cache.wacher.status.log.interval", "30m");
        int intervalMinutes = parseIntervalToMinutes(intervalConfig);
        
        scheduler.scheduleAtFixedRate(
            this::logPeriodicStatus,
            intervalMinutes, // Delay inicial de 30 minutos
            intervalMinutes, // Cada 30 minutos
            TimeUnit.MINUTES
        );
        
        logger.info("📊 Programado log de estado cada " + intervalMinutes + " minutos");
    }
    
    /**
     * Parsea intervalos de tiempo (30m, 1h, etc.) a minutos
     */
    private int parseIntervalToMinutes(String intervalConfig) {
        try {
            intervalConfig = intervalConfig.trim().toLowerCase();
            
            if (intervalConfig.endsWith("m")) {
                return Integer.parseInt(intervalConfig.substring(0, intervalConfig.length() - 1));
            } else if (intervalConfig.endsWith("h")) {
                int hours = Integer.parseInt(intervalConfig.substring(0, intervalConfig.length() - 1));
                return hours * 60; // Convertir a minutos
            } else {
                // Asumir minutos si no tiene sufijo
                return Integer.parseInt(intervalConfig);
            }
        } catch (Exception e) {
            logger.warning("⚠️ Error parseando intervalo de log: " + intervalConfig + ", usando 30 minutos por defecto");
            return 30; // Default 30 minutos
        }
    }
    
    /**
     * Ejecuta log periódico del estado del cache
     */
    private void logPeriodicStatus() {
        try {
            logger.info("========== ESTADO PERIÓDICO DEL CACHE WACHER_PARAMETROS ==========");
            logger.info("Timestamp: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            // Estadísticas básicas
            logger.info("Total parámetros en cache: " + parametersCache.size());
            logger.info("Capacidad máxima: " + config.getMaxCapacity());
            logger.info("TTL global: " + getGlobalTTLHours() + " horas");
            
            // Métricas de performance
            long totalQueries = hitCount + missCount;
            if (totalQueries > 0) {
                double hitRatePercent = (double) hitCount / totalQueries * 100;
                logger.info("Cache hits: " + hitCount);
                logger.info("Cache misses: " + missCount);
                logger.info("Hit rate: " + String.format("%.2f", hitRatePercent) + "%");
            }
            
            // Tiempo desde última actualización
            if (cacheLoadTime != null) {
                long minutosDesdeActualizacion = java.time.Duration.between(cacheLoadTime, LocalDateTime.now()).toMinutes();
                long horasParaActualizacion = getGlobalTTLHours() - (minutosDesdeActualizacion / 60);
                
                logger.info("Última actualización: hace " + minutosDesdeActualizacion + " minutos");
                logger.info("Próxima actualización: en " + Math.max(0, horasParaActualizacion) + " horas");
            }
            
            // Parámetros almacenados con valores detallados
            if (!parametersCache.isEmpty()) {
                logger.info("Parámetros almacenados con valores:");
                parametersCache.entrySet().stream()
                    .sorted(java.util.Map.Entry.comparingByKey()) // Ordenar alfabéticamente
                    .forEach(entry -> {
                        String codigo = entry.getKey();
                        String valor = entry.getValue();
                        // Mostrar valor truncado si es muy largo
                        String valorMostrar = valor != null && valor.length() > 50 
                            ? valor.substring(0, 47) + "..." 
                            : valor;
                        logger.info("   ----> " + codigo + " = " + valorMostrar);
                    });
            }
            
            logger.info("====================================================================");
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "❌ Error en log periódico de estado: " + e.getMessage(), e);
        }
    }
    
    /**
     * Ejecuta el refresh diario con logging completo
     */
    private void executeDailyRefresh() {
        String sessionId = "DAILY-REFRESH-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm"));
        
        logger.info("🌙 ============ REFRESH DIARIO PROGRAMADO INICIADO ============");
        logger.info("   📅 Sesión: " + sessionId);
        logger.info("   🕒 Hora actual: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        logger.info("   ⚙️ Horario configurado: " + config.getDailyRefreshTime());
        logger.info("   🎯 Objetivo: Actualizar cache para el día siguiente");
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Ejecutar refresh completo
            loadAllParameters();
            
            long duration = System.currentTimeMillis() - startTime;
            
            // Log de éxito
            logger.info("✅ REFRESH DIARIO COMPLETADO EXITOSAMENTE:");
            logger.info("   ⏱️ Duración: " + duration + "ms");
            logger.info("   📊 Parámetros actualizados: " + parametersCache.size());
            logger.info("   📅 Válido hasta: " + cacheLoadTime.plusHours(24).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            logger.info("   🔄 Próximo refresh: Mañana a las " + config.getDailyRefreshTime());
            
            // Log métricas actuales
            logCacheStatistics();
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.severe("❌ ERROR EN REFRESH DIARIO 3:00 AM:");
            logger.severe("   ⏱️ Duración hasta error: " + duration + "ms");
            logger.severe("   📝 Error: " + e.getMessage());
            logger.severe("   🔄 Reintentará mañana a las " + config.getDailyRefreshTime());
            
            // Log del error para análisis posterior  
            try {
                WacherParametrosCacheLogger.logRefreshErrorAlert(sessionId, 1, parametersCache.size());
            } catch (Exception logError) {
                logger.severe("❌ Error adicional en logging: " + logError.getMessage());
            }
        }
        
        logger.info("============================================================");
    }
    
    /**
     * Consulta un parámetro desde BD
     */
    private String queryParameterFromDatabase(String codigo) {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CtWacherParametroRepository repository = new CtWacherParametroRepository(entityManager);
            return queryParameterFromRepository(repository, codigo);
        } catch (Exception e) {
            logger.log(Level.WARNING, "❌ Error consultando BD: " + codigo, e);
            return null;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
    
    /**
     * Consulta usando EntityManager directamente
     */
    private String queryParameterFromRepository(CtWacherParametroRepository repository, String codigo) {
        try {
            // CORRECCIÓN: usar consulta nativa directa en lugar del método mal usado
            EntityManager em = entityManagerFactory.createEntityManager();
            try {
                javax.persistence.Query query = em.createNativeQuery(
                    "SELECT valor FROM wacher_parametros WHERE codigo = ?1"
                );
                query.setParameter(1, codigo);
                Object result = query.getSingleResult();
                return result != null ? result.toString() : null;
            } finally {
                if (em != null && em.isOpen()) {
                    em.close();
                }
            }
        } catch (javax.persistence.NoResultException e) {
            // No hay resultado - parámetro no existe
            return null;
        } catch (Exception e) {
            logger.log(Level.WARNING, "❌ Error consultando parámetro: " + codigo, e);
            return null;
        }
    }
    
    /**
     * Fuerza refresh manual del cache
     */
    public void forceRefresh() {
        logger.info("🔄 REFRESH MANUAL: Iniciando carga completa por solicitud del usuario...");
        long startTime = System.currentTimeMillis();
        loadAllParameters();
        long endTime = System.currentTimeMillis();
        logger.info("✅ REFRESH MANUAL COMPLETO: Cache refrescado en " + (endTime - startTime) + "ms");
    }
    
    /**
     * Muestra el contenido actual del cache wacher_parametros (para debugging)
     */
    public void mostrarContenidoCacheParametros() {
        System.out.println("\n ========== CONTENIDO ACTUAL DEL CACHE WACHER_PARAMETROS ==========");
        System.out.println("Total parámetros en cache: " + parametersCache.size());
        System.out.println("Capacidad máxima: " + config.getMaxCapacity());
        System.out.println("TTL global: " + getGlobalTTLHours() + " horas");
        
        if (parametersCache.isEmpty()) {
            System.out.println(" Cache wacher_parametros vacío - No hay parámetros almacenados");
        } else {
            System.out.println("Parámetros almacenados:");
            parametersCache.forEach((codigo, valor) -> {
                System.out.println("  ----> " + codigo + " = " + valor);
            });
            
            // Mostrar tiempo desde última carga
            if (cacheLoadTime != null) {
                long minutosDesdeActualizacion = java.time.Duration.between(cacheLoadTime, LocalDateTime.now()).toMinutes();
                System.out.println("\nÚltima actualización: hace " + minutosDesdeActualizacion + " minutos");
                
                // Calcular próxima actualización
                LocalDateTime proximaActualizacion = cacheLoadTime.plusHours(getGlobalTTLHours());
                long horasParaActualizacion = java.time.Duration.between(LocalDateTime.now(), proximaActualizacion).toHours();
                System.out.println("Próxima actualización: en " + Math.max(0, horasParaActualizacion) + " horas");
            }
        }
        System.out.println("====================================================================");
    }
    
    /**
     * Obtiene estadísticas detalladas del cache para logging
     */
    public void logCacheStatistics() {
        CacheMetrics metrics = getMetrics();
        logger.info("📊 ============ ESTADÍSTICAS CACHE ============");
        logger.info("   ✅ Cache hits: " + metrics.getHitCount());
        logger.info("   ❌ Cache misses: " + metrics.getMissCount());
        logger.info("   📈 Hit rate: " + String.format("%.2f", metrics.getHitRatePercent()) + "%");
        logger.info("   🗄️ Parámetros en cache: " + metrics.getCacheSize());
        logger.info("   ⏰ Última carga: " + metrics.getLastLoad());
        logger.info("   📅 Próximo refresh: en " + metrics.getHoursUntilRefresh() + " horas");
        logger.info("   🎯 TTL global: " + metrics.getGlobalTTLHours() + " horas");
        logger.info("===============================================");
    }
    
    /**
     * Obtiene métricas del cache
     */
    public CacheMetrics getMetrics() {
        long total = hitCount + missCount;
        double hitRate = total > 0 ? (double) hitCount / total * 100 : 0;
        
        // Calcular tiempo restante hasta próximo refresh
        long hoursUntilRefresh = 0;
        if (cacheLoadTime != null) {
            long hoursAgo = java.time.Duration.between(cacheLoadTime, LocalDateTime.now()).toHours();
            hoursUntilRefresh = Math.max(0, getGlobalTTLHours() - hoursAgo);
        }
        
        return new CacheMetrics(
            hitCount,
            missCount,
            hitRate,
            parametersCache.size(),
            cacheLoadTime,
            cacheInitialized,
            hoursUntilRefresh,
            getGlobalTTLHours()
        );
    }
    
    /**
     * Shutdown del servicio
     */
    public void shutdown() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        parametersCache.clear();
        logger.info("🛑 WacherParametrosCacheSimple detenido");
    }
    
    /**
     * OBTIENE EL MÉTODO QUE LLAMÓ AL CACHE PARA TRAZABILIDAD
     */
    private String getCaller() {
        try {
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            
            // Buscar el primer método que NO sea de esta clase
            for (int i = 3; i < Math.min(stack.length, 8); i++) {
                String className = stack[i].getClassName();
                if (!className.contains("WacherParametrosCacheSimple") && 
                    !className.contains("WacherParametrosCacheLogger")) {
                    return className + "." + stack[i].getMethodName() + ":" + stack[i].getLineNumber();
                }
            }
            return "UNKNOWN_CALLER";
        } catch (Exception e) {
            return "ERROR_GETTING_CALLER";
        }
    }
    
    /**
     * Clase para métricas del cache simple
     */
    public static class CacheMetrics {
        private final long hitCount;
        private final long missCount;
        private final double hitRatePercent;
        private final int cacheSize;
        private final LocalDateTime lastLoad;
        private final boolean initialized;
        private final long hoursUntilRefresh;
        private final long globalTTLHours;
        
        public CacheMetrics(long hitCount, long missCount, double hitRatePercent, 
                          int cacheSize, LocalDateTime lastLoad, boolean initialized,
                          long hoursUntilRefresh, long globalTTLHours) {
            this.hitCount = hitCount;
            this.missCount = missCount;
            this.hitRatePercent = hitRatePercent;
            this.cacheSize = cacheSize;
            this.lastLoad = lastLoad;
            this.initialized = initialized;
            this.hoursUntilRefresh = hoursUntilRefresh;
            this.globalTTLHours = globalTTLHours;
        }
        
        @Override
        public String toString() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return String.format(
                "CacheMetrics{hits=%d, misses=%d, hitRate=%.2f%%, size=%d, lastLoad=%s, nextRefresh=%dh, globalTTL=%dh}",
                hitCount, missCount, hitRatePercent, cacheSize,
                lastLoad != null ? lastLoad.format(formatter) : "null",
                hoursUntilRefresh, globalTTLHours
            );
        }
        
        // Getters
        public long getHitCount() { return hitCount; }
        public long getMissCount() { return missCount; }
        public double getHitRatePercent() { return hitRatePercent; }
        public int getCacheSize() { return cacheSize; }
        public LocalDateTime getLastLoad() { return lastLoad; }
        public boolean isInitialized() { return initialized; }
        public long getHoursUntilRefresh() { return hoursUntilRefresh; }
        public long getGlobalTTLHours() { return globalTTLHours; }
    }
}
