package com.infrastructure.cache;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuracion centralizada para WacherParametrosCacheSimple
 * 
 * Lee configuracion desde cache-config.properties y proporciona
 * acceso type-safe a todos los parametros de configuracion.
 * 
 * Caracteristicas:
 * - TTL diferenciado por parametro
 * - Configuracion por ambiente (dev/test/prod)
 * - Hit ratio targets configurables
 * - Timeouts y retry policies
 * - Logging y monitoring settings
 * - Fallback configurations
 */
public class WacherParametrosCacheConfig {
    
    private static final Logger logger = Logger.getLogger(WacherParametrosCacheConfig.class.getName());
    
    // Singleton instance
    private static volatile WacherParametrosCacheConfig instance;
    
    // Archivo de configuracion por defecto
    private static final String DEFAULT_CONFIG_FILE = "cache-config.properties";
    
    // Properties cargadas
    private final Properties properties;
    
    // Cache de TTL por parametro (en milisegundos)
    private final Map<String, Long> ttlCache;
    
    private WacherParametrosCacheConfig(String configFile) {
        this.properties = new Properties();
        this.ttlCache = new HashMap<>();
        loadConfiguration(configFile);
    }
    
    /**
     * Obtiene la instancia singleton con configuracion por defecto
     */
    public static WacherParametrosCacheConfig getInstance() {
        return getInstance(DEFAULT_CONFIG_FILE);
    }
    
    /**
     * Obtiene la instancia singleton con archivo de configuracion especifico
     */
    public static WacherParametrosCacheConfig getInstance(String configFile) {
        if (instance == null) {
            synchronized (WacherParametrosCacheConfig.class) {
                if (instance == null) {
                    instance = new WacherParametrosCacheConfig(configFile);
                }
            }
        }
        return instance;
    }
    
    /**
     * Carga la configuracion desde archivo
     */
    private void loadConfiguration(String configFile) {
        InputStream inputStream = null;
        try {
            // Intentar cargar desde file system primero
            try {
                inputStream = new FileInputStream(configFile);
                logger.info("📁 Cargando configuracion desde: " + configFile);
            } catch (Exception e) {
                // Fallback a classpath
                inputStream = getClass().getClassLoader().getResourceAsStream(configFile);
                if (inputStream != null) {
                    logger.info("📁 Cargando configuracion desde classpath: " + configFile);
                } else {
                    throw new RuntimeException("No se encontro archivo de configuracion: " + configFile);
                }
            }
            
            properties.load(inputStream);
            
            // Aplicar multiplicador por ambiente
            applyEnvironmentMultiplier();
            
            // Pre-calcular TTLs para performance
            precalculateTTLs();
            
            logger.info("✅ Configuracion cache cargada exitosamente");
            logConfigurationSummary();
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error cargando configuracion cache", e);
            loadDefaultConfiguration();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error cerrando stream de configuracion", e);
                }
            }
        }
    }
    
    /**
     * Aplica multiplicador de TTL segun ambiente
     */
    private void applyEnvironmentMultiplier() {
        String currentEnv = getProperty("cache.wacher.environment.current", "prod");
        double multiplier = getDoubleProperty("cache.wacher.environment." + currentEnv + ".ttl_multiplier", 1.0);
        
        if (multiplier != 1.0) {
            logger.info("🔧 Aplicando multiplicador TTL para ambiente " + currentEnv + ": " + multiplier);
        }
    }
    
    /**
     * Pre-calcula TTLs para mejorar performance en runtime
     */
    private void precalculateTTLs() {
        String[] criticalParameters = {
            "REMISION", "OBLIGATORIO_FE", "MONTO_MINIMO_FE", "POS_ID", "MENSAJES_FE",
            "OBLIGATORIEDAD_REMISION", "MONTO_MINIMO_REMISION", "codigoBackoffice",
            "TIMEOUT_ESPERA_LECTURAS", "HOST_LAZO_PRINCIPAL", "HOST_SERVER"
        };
        
        for (String parameter : criticalParameters) {
            long ttlMs = getTTLMilliseconds(parameter);
            ttlCache.put(parameter, ttlMs);
        }
        
        logger.info("🚀 Pre-calculados TTLs para " + ttlCache.size() + " parametros criticos");
    }
    
    /**
     * Carga configuracion por defecto en caso de error
     */
    private void loadDefaultConfiguration() {
        logger.warning("⚠️ Cargando configuracion por defecto");
        
        // Configuracion minima funcional
        properties.setProperty("cache.wacher.enabled", "true");
        properties.setProperty("cache.wacher.initial_capacity", "15");
        properties.setProperty("cache.wacher.max_capacity", "25");
        properties.setProperty("cache.wacher.ttl.default", "24h");
        properties.setProperty("cache.wacher.monitoring.min_hit_rate", "90.0");
        properties.setProperty("cache.wacher.refresh.interval_hours", "24");
    }
    
    /**
     * Log resumen de configuracion cargada
     */
    private void logConfigurationSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("\n=== CONFIGURACION CACHE WACHER_PARAMETROS ===\n");
        summary.append("Cache habilitado: ").append(isCacheEnabled()).append("\n");
        summary.append("Capacidad inicial: ").append(getInitialCapacity()).append("\n");
        summary.append("Capacidad maxima: ").append(getMaxCapacity()).append("\n");
        summary.append("Hit rate minimo: ").append(getMinHitRate()).append("%\n");
        summary.append("Intervalo refresh: ").append(getRefreshIntervalHours()).append("h\n");
        summary.append("TTL REMISION: ").append(getTTLHours("REMISION")).append("h\n");
        summary.append("TTL POS_ID: ").append(getTTLHours("POS_ID")).append("h\n");
        summary.append("Ambiente: ").append(getCurrentEnvironment()).append("\n");
        summary.append("===========================================");
        
        logger.info(summary.toString());
    }
    
    // =================================================================
    // METODOS DE ACCESO A CONFIGURACION GENERAL
    // =================================================================
    
    public boolean isCacheEnabled() {
        return getBooleanProperty("cache.wacher.enabled", true);
    }
    
    public int getInitialCapacity() {
        return getIntProperty("cache.wacher.initial_capacity", 15);
    }
    
    public int getMaxCapacity() {
        return getIntProperty("cache.wacher.max_capacity", 25);
    }
    
    public int getConcurrencyLevel() {
        return getIntProperty("cache.wacher.concurrency_level", 4);
    }
    
    public long getOperationTimeoutMs() {
        return getLongProperty("cache.wacher.operation_timeout_ms", 5000L);
    }
    
    // =================================================================
    // METODOS DE TTL
    // =================================================================
    
    /**
     * Obtiene TTL en milisegundos para un parametro especifico
     * MODO SIMPLE: Si hay TTL global, usar ese para todos
     */
    public long getTTLMilliseconds(String parameterCode) {
        // MODO SIMPLE: Verificar si hay TTL global configurado
        String globalTTL = getProperty("cache.wacher.ttl.global", null);
        if (globalTTL != null) {
            // Usar TTL global para TODOS los parámetros (modo simple)
            long ttlMs = parseTTLToMilliseconds(globalTTL);
            
            // Aplicar multiplicador por ambiente
            String currentEnv = getCurrentEnvironment();
            double multiplier = getDoubleProperty("cache.wacher.environment." + currentEnv + ".ttl_multiplier", 1.0);
            ttlMs = (long) (ttlMs * multiplier);
            
            return ttlMs;
        }
        
        // MODO INDIVIDUAL: Verificar cache primero
        Long cachedTTL = ttlCache.get(parameterCode);
        if (cachedTTL != null) {
            return cachedTTL;
        }
        
        // Calcular TTL individual
        String ttlKey = "cache.wacher.ttl." + parameterCode;
        String ttlValue = getProperty(ttlKey, null);
        
        if (ttlValue == null) {
            // Usar TTL por defecto
            ttlValue = getProperty("cache.wacher.ttl.default", "24h");
        }
        
        long ttlMs = parseTTLToMilliseconds(ttlValue);
        
        // Aplicar multiplicador por ambiente
        String currentEnv = getCurrentEnvironment();
        double multiplier = getDoubleProperty("cache.wacher.environment." + currentEnv + ".ttl_multiplier", 1.0);
        ttlMs = (long) (ttlMs * multiplier);
        
        // Cachear para futuras consultas
        ttlCache.put(parameterCode, ttlMs);
        
        return ttlMs;
    }
    
    /**
     * Obtiene TTL en horas para un parametro
     */
    public long getTTLHours(String parameterCode) {
        return getTTLMilliseconds(parameterCode) / (1000 * 60 * 60);
    }
    
    /**
     * Parsea string de TTL a milisegundos
     * Formatos soportados: "24h", "30m", "1800s"
     */
    private long parseTTLToMilliseconds(String ttlValue) {
        if (ttlValue == null || ttlValue.trim().isEmpty()) {
            return TimeUnit.HOURS.toMillis(24); // Default 24 horas
        }
        
        ttlValue = ttlValue.trim().toLowerCase();
        
        try {
            if (ttlValue.endsWith("h")) {
                // Horas
                long hours = Long.parseLong(ttlValue.substring(0, ttlValue.length() - 1));
                return TimeUnit.HOURS.toMillis(hours);
            } else if (ttlValue.endsWith("m")) {
                // Minutos
                long minutes = Long.parseLong(ttlValue.substring(0, ttlValue.length() - 1));
                return TimeUnit.MINUTES.toMillis(minutes);
            } else if (ttlValue.endsWith("s")) {
                // Segundos
                long seconds = Long.parseLong(ttlValue.substring(0, ttlValue.length() - 1));
                return TimeUnit.SECONDS.toMillis(seconds);
            } else {
                // Asumir milisegundos
                return Long.parseLong(ttlValue);
            }
        } catch (NumberFormatException e) {
            logger.warning("⚠️ Error parseando TTL: " + ttlValue + ", usando default 24h");
            return TimeUnit.HOURS.toMillis(24);
        }
    }
    
    // =================================================================
    // CONFIGURACION DE REFRESH
    // =================================================================
    
    public boolean isRefreshEnabled() {
        return getBooleanProperty("cache.wacher.refresh.enabled", true);
    }
    
    public int getRefreshIntervalHours() {
        return getIntProperty("cache.wacher.refresh.interval_hours", 24);
    }
    
    public boolean isRefreshAtStartup() {
        return getBooleanProperty("cache.wacher.refresh.at_startup", true);
    }
    
    public int getRefreshBatchSize() {
        return getIntProperty("cache.wacher.refresh.batch_size", 20);
    }
    
    public long getRefreshTimeoutMs() {
        return getLongProperty("cache.wacher.refresh.timeout_ms", 30000L);
    }
    
    public int getRefreshMaxRetries() {
        return getIntProperty("cache.wacher.refresh.max_retries", 3);
    }
    
    public long getRefreshRetryDelayMs() {
        return getLongProperty("cache.wacher.refresh.retry_delay_ms", 5000L);
    }
    
    // =================================================================
    // CONFIGURACION DE REFRESH DIARIO
    // =================================================================
    
    /**
     * Verifica si el refresh diario está habilitado
     */
    public boolean isDailyRefreshEnabled() {
        return getBooleanProperty("cache.wacher.refresh.daily.enabled", true);
    }
    
    /**
     * Obtiene la hora configurada para el refresh diario (formato HH:mm)
     * Por defecto: "03:00" (3:00 AM)
     */
    public String getDailyRefreshTime() {
        return getProperty("cache.wacher.refresh.daily.time", "03:00");
    }
    
    /**
     * Obtiene la zona horaria configurada para el refresh
     * Por defecto: "America/Bogota"
     */
    public String getRefreshTimezone() {
        return getProperty("cache.wacher.refresh.timezone", "America/Bogota");
    }
    
    /**
     * Parsea la hora de refresh diario y retorna las horas y minutos
     * @return array [horas, minutos] o [3, 0] si hay error
     */
    public int[] parseDailyRefreshTime() {
        try {
            String timeString = getDailyRefreshTime();
            String[] parts = timeString.split(":");
            
            if (parts.length == 2) {
                int hours = Integer.parseInt(parts[0].trim());
                int minutes = Integer.parseInt(parts[1].trim());
                
                // Validar rangos
                if (hours >= 0 && hours <= 23 && minutes >= 0 && minutes <= 59) {
                    return new int[]{hours, minutes};
                }
            }
            
            logger.warning("⚠️ Formato inválido para hora de refresh: " + timeString + ", usando 03:00");
            return new int[]{3, 0}; // Default 3:00 AM
            
        } catch (Exception e) {
            logger.warning("⚠️ Error parseando hora de refresh: " + e.getMessage() + ", usando 03:00");
            return new int[]{3, 0}; // Default 3:00 AM
        }
    }
    
    // =================================================================
    // CONFIGURACION DE MONITOREO
    // =================================================================
    
    public double getMinHitRate() {
        return getDoubleProperty("cache.wacher.monitoring.min_hit_rate", 90.0);
    }
    
    public double getTargetHitRate() {
        return getDoubleProperty("cache.wacher.monitoring.target_hit_rate", 95.0);
    }
    
    public double getExcellentHitRate() {
        return getDoubleProperty("cache.wacher.monitoring.excellent_hit_rate", 98.0);
    }
    
    public int getHealthCheckIntervalMinutes() {
        return getIntProperty("cache.wacher.monitoring.health_check_interval_minutes", 15);
    }
    
    public int getReportIntervalHours() {
        return getIntProperty("cache.wacher.monitoring.report_interval_hours", 4);
    }
    
    public long getMinQueriesForEvaluation() {
        return getLongProperty("cache.wacher.monitoring.min_queries_for_evaluation", 100L);
    }
    
    public int getAlertCooldownMinutes() {
        return getIntProperty("cache.wacher.monitoring.alert_cooldown_minutes", 30);
    }
    
    public int getMaxConsecutiveAlerts() {
        return getIntProperty("cache.wacher.monitoring.max_consecutive_alerts", 5);
    }
    
    // =================================================================
    // CONFIGURACION DE LOGGING
    // =================================================================
    
    public boolean isLoggingEnabled() {
        return getBooleanProperty("cache.wacher.logging.enabled", true);
    }
    
    public String getLoggingLevel() {
        return getProperty("cache.wacher.logging.level", "INFO");
    }
    
    public String getLogFile() {
        return getProperty("cache.wacher.logging.file", "logs/cache_wacher_parametros.log");
    }
    
    public int getMaxLogFileSizeMB() {
        return getIntProperty("cache.wacher.logging.max_file_size_mb", 50);
    }
    
    public int getMaxLogFiles() {
        return getIntProperty("cache.wacher.logging.max_files", 5);
    }
    
    public boolean isMetricsLoggingEnabled() {
        return getBooleanProperty("cache.wacher.logging.metrics_enabled", true);
    }
    
    public boolean isOperationsLoggingEnabled() {
        return getBooleanProperty("cache.wacher.logging.operations_enabled", false);
    }
    
    // =================================================================
    // CONFIGURACION DE FALLBACK
    // =================================================================
    
    public boolean isFallbackEnabled() {
        return getBooleanProperty("cache.wacher.fallback.enabled", true);
    }
    
    public long getFallbackTimeoutMs() {
        return getLongProperty("cache.wacher.fallback.timeout_ms", 10000L);
    }
    
    public int getFallbackMaxRetries() {
        return getIntProperty("cache.wacher.fallback.max_retries", 2);
    }
    
    public long getFallbackRetryDelayMs() {
        return getLongProperty("cache.wacher.fallback.retry_delay_ms", 1000L);
    }
    
    // =================================================================
    // CONFIGURACION DE AMBIENTE
    // =================================================================
    
    public String getCurrentEnvironment() {
        return getProperty("cache.wacher.environment.current", "prod");
    }
    
    public boolean isDevelopmentEnvironment() {
        return "dev".equals(getCurrentEnvironment());
    }
    
    public boolean isTestEnvironment() {
        return "test".equals(getCurrentEnvironment());
    }
    
    public boolean isProductionEnvironment() {
        return "prod".equals(getCurrentEnvironment());
    }
    
    // =================================================================
    // CONFIGURACION AVANZADA
    // =================================================================
    
    public boolean isWarmupEnabled() {
        return getBooleanProperty("cache.wacher.warmup.enabled", true);
    }
    
    public long getWarmupTimeoutMs() {
        return getLongProperty("cache.wacher.warmup.timeout_ms", 60000L);
    }
    
    public boolean isCompressionEnabled() {
        return getBooleanProperty("cache.wacher.compression.enabled", false);
    }
    
    public boolean isEncryptionEnabled() {
        return getBooleanProperty("cache.wacher.encryption.enabled", false);
    }
    
    public boolean isStatisticsEnabled() {
        return getBooleanProperty("cache.wacher.statistics.enabled", true);
    }
    
    // =================================================================
    // CONFIGURACION DE VALIDACION
    // =================================================================
    
    public boolean isValidationEnabled() {
        return getBooleanProperty("cache.wacher.validation.enabled", true);
    }
    
    public String[] getBlacklistedParameters() {
        String blacklist = getProperty("cache.wacher.security.blacklist", "");
        return blacklist.trim().isEmpty() ? new String[0] : blacklist.split(",");
    }
    
    public String[] getValidationRequiredParameters() {
        String validationRequired = getProperty("cache.wacher.security.validation_required", "MONTO_MINIMO_FE,OBLIGATORIO_FE");
        return validationRequired.trim().isEmpty() ? new String[0] : validationRequired.split(",");
    }
    
    public long getMaxAgeHours() {
        return getLongProperty("cache.wacher.security.max_age_hours", 168L); // 7 dias
    }
    
    // =================================================================
    // METRICAS DE NEGOCIO
    // =================================================================
    
    public long getEstimatedDailyQueries() {
        return getLongProperty("cache.wacher.business.estimated_daily_queries", 300000L);
    }
    
    public long getEstimatedDbQueryCostMs() {
        return getLongProperty("cache.wacher.business.estimated_db_query_cost_ms", 100L);
    }
    
    public long getEstimatedCacheQueryCostMs() {
        return getLongProperty("cache.wacher.business.estimated_cache_query_cost_ms", 1L);
    }
    
    public double getTargetDbReductionPercent() {
        return getDoubleProperty("cache.wacher.business.target_db_reduction_percent", 99.0);
    }
    
    // =================================================================
    // METODOS UTILITARIOS PARA PROPERTIES
    // =================================================================
    
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(value.trim());
    }
    
    private int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            logger.warning("⚠️ Error parseando property entero: " + key + "=" + value + ", usando default: " + defaultValue);
            return defaultValue;
        }
    }
    
    private long getLongProperty(String key, long defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            logger.warning("⚠️ Error parseando property long: " + key + "=" + value + ", usando default: " + defaultValue);
            return defaultValue;
        }
    }
    
    private double getDoubleProperty(String key, double defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            logger.warning("⚠️ Error parseando property double: " + key + "=" + value + ", usando default: " + defaultValue);
            return defaultValue;
        }
    }
    
    /**
     * Recarga la configuracion desde archivo
     */
    public void reload() {
        logger.info("🔄 Recargando configuracion cache...");
        ttlCache.clear();
        loadConfiguration(DEFAULT_CONFIG_FILE);
    }
    
    /**
     * Obtiene todas las properties como string para debugging
     */
    public String getAllProperties() {
        StringBuilder sb = new StringBuilder();
        properties.forEach((key, value) -> {
            sb.append(key).append("=").append(value).append("\n");
        });
        return sb.toString();
    }
}
