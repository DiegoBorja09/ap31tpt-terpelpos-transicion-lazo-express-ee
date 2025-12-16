package com.application.useCases.printService;

import com.application.core.BaseUseCasesWithParams;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.google.gson.JsonObject;
import java.util.TreeMap;

/**
 * Use Case: Verificar el estado de salud del servicio de impresión Python
 * 
 * Arquitectura Limpia: Use Case que encapsula la lógica de negocio
 * para verificar el estado del servicio de impresión con cache integrado.
 * 
 * El cache evita múltiples llamadas simultáneas al servicio y mejora
 * el rendimiento cuando se hacen múltiples verificaciones en poco tiempo.
 * 
 * @author Equipo Desarrollo
 * @version 1.0
 */
public class CheckPrintServiceHealthUseCase implements BaseUseCasesWithParams<Void, CheckPrintServiceHealthUseCase.HealthCheckResult> {
    
    // Cache estático compartido entre todas las instancias
    private static JsonObject cachedHealthCheck = null;
    private static long healthCheckTimestamp = 0;
    private static final long HEALTH_CHECK_CACHE_MS = 10000; // 10 segundos
    private static final Object healthCheckLock = new Object();
    
    // Timeout para el health check (en milisegundos)
    private final int timeout;
    
    /**
     * Constructor con timeout por defecto de 3 segundos
     */
    public CheckPrintServiceHealthUseCase() {
        this.timeout = 3000;
    }
    
    /**
     * Constructor con timeout personalizado
     * @param timeout Timeout en milisegundos
     */
    public CheckPrintServiceHealthUseCase(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public HealthCheckResult execute(Void input) {
        JsonObject healthResponse = obtenerHealthCheckCacheado();
        return new HealthCheckResult(healthResponse);
    }
    
    /**
     * Obtiene el resultado del health check usando cache para evitar múltiples llamadas simultáneas
     * El cache es válido por 10 segundos (configurable)
     * @return JsonObject con el resultado del health check, null si no hay respuesta
     */
    private JsonObject obtenerHealthCheckCacheado() {
        synchronized (healthCheckLock) {
            long ahora = System.currentTimeMillis();
            
            // Si el cache es válido, retornarlo
            if (cachedHealthCheck != null && (ahora - healthCheckTimestamp) < HEALTH_CHECK_CACHE_MS) {
                long tiempoRestante = (HEALTH_CHECK_CACHE_MS - (ahora - healthCheckTimestamp)) / 1000;
                NovusUtils.printLn("[CheckPrintServiceHealthUseCase] Usando health check cacheado (válido por " + 
                    tiempoRestante + " segundos más)");
                return cachedHealthCheck;
            }
            
            // Cache expirado o no existe - hacer nuevo health check
            NovusUtils.printLn("[CheckPrintServiceHealthUseCase] Ejecutando nuevo health check (cache expirado o no existe)");
            JsonObject response = verificarServicioImpresion();
            
            // Actualizar cache
            cachedHealthCheck = response;
            healthCheckTimestamp = ahora;
            
            return response;
        }
    }
    
    /**
     * Verifica el estado del servicio de impresión Python
     * @return JsonObject con el resultado del health check, null si no hay respuesta
     * 
     * Estructura esperada de respuesta:
     * {
     *     "status": "healthy",
     *     "service": "print-ticket-service",
     *     "printer": {
     *         "connected": true,
     *         "host": "localhost",
     *         "port": 9100,
     *         "message": "Impresora conectada - Puerto 9100 activo"
     *     },
     *     "timestamp": "2025-12-01T12:30:45.123456"
     * }
     */
    private JsonObject verificarServicioImpresion() {
        try {
            String healthUrl = NovusConstante.SECURE_CENTRAL_POINT_PRINT_TICKET_HEALTH;
            NovusUtils.printLn("[CheckPrintServiceHealthUseCase] Verificando servicio de impresión: " + healthUrl);
            
            TreeMap<String, String> header = new TreeMap<>();
            header.put("Content-type", "application/json");
            header.put("Accept", "application/json");
            
            ClientWSAsync healthCheck = new ClientWSAsync(
                "HEALTH CHECK - PRINT SERVICE", 
                healthUrl, 
                NovusConstante.GET, 
                null, 
                NovusConstante.DEBUG_PRINT_TICKET, 
                false, 
                header, 
                timeout
            );
            JsonObject response = healthCheck.esperaRespuesta();
            
            if (response != null) {
                NovusUtils.printLn("[CheckPrintServiceHealthUseCase] Respuesta health check recibida");
                
                // Verificar estado del servicio
                if (response.has("status")) {
                    String status = response.get("status").getAsString();
                    NovusUtils.printLn("[CheckPrintServiceHealthUseCase] Status del servicio: " + status);
                }
                
                // Verificar estado de la impresora
                if (response.has("printer") && response.get("printer").isJsonObject()) {
                    JsonObject printer = response.getAsJsonObject("printer");
                    boolean connected = printer.has("connected") && printer.get("connected").getAsBoolean();
                    String printerMessage = printer.has("message") ? printer.get("message").getAsString() : "Sin mensaje";
                    NovusUtils.printLn("[CheckPrintServiceHealthUseCase] Impresora conectada: " + connected);
                    NovusUtils.printLn("[CheckPrintServiceHealthUseCase] Mensaje impresora: " + printerMessage);
                }
            } else {
                NovusUtils.printLn("[CheckPrintServiceHealthUseCase] Sin respuesta del servicio de impresión");
            }
            
            return response;
        } catch (Exception e) {
            NovusUtils.printLn("[CheckPrintServiceHealthUseCase] Error verificando servicio de impresión: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Limpia el cache del health check (útil para forzar una nueva verificación)
     */
    public static void limpiarCache() {
        synchronized (healthCheckLock) {
            cachedHealthCheck = null;
            healthCheckTimestamp = 0;
            NovusUtils.printLn("[CheckPrintServiceHealthUseCase] Cache limpiado");
        }
    }
    
    /**
     * DTO que encapsula el resultado del health check con métodos de utilidad
     */
    public static class HealthCheckResult {
        private final JsonObject healthResponse;
        
        public HealthCheckResult(JsonObject healthResponse) {
            this.healthResponse = healthResponse;
        }
        
        /**
         * @return true si hay respuesta del servicio, false si es null
         */
        public boolean tieneRespuesta() {
            return healthResponse != null;
        }
        
        /**
         * @return true si el servicio está saludable (status=healthy) y la impresora conectada
         */
        public boolean esSaludable() {
            if (healthResponse == null) {
                return false;
            }
            
            // Verificar que el status sea "healthy"
            if (!healthResponse.has("status") || !"healthy".equalsIgnoreCase(healthResponse.get("status").getAsString())) {
                return false;
            }
            
            // Verificar que la impresora esté conectada
            if (healthResponse.has("printer") && healthResponse.get("printer").isJsonObject()) {
                JsonObject printer = healthResponse.getAsJsonObject("printer");
                return printer.has("connected") && printer.get("connected").getAsBoolean();
            }
            
            return false;
        }
        
        /**
         * Obtiene el mensaje de error apropiado para mostrar al usuario
         * @return El mensaje de error, o null si el servicio está saludable
         */
        public String obtenerMensajeError() {
            if (healthResponse == null) {
                return "SERVICIO DE IMPRESIÓN NO DISPONIBLE. VERIFIQUE QUE EL SERVICIO ESTÉ ACTIVO.";
            }
            
            // Si la impresora no está conectada
            if (healthResponse.has("printer") && healthResponse.get("printer").isJsonObject()) {
                JsonObject printer = healthResponse.getAsJsonObject("printer");
                if (!printer.has("connected") || !printer.get("connected").getAsBoolean()) {
                    String mensaje = printer.has("message") ? printer.get("message").getAsString() : "IMPRESORA NO CONECTADA";
                    return mensaje.toUpperCase();
                }
            }
            
            return "ERROR DESCONOCIDO EN SERVICIO DE IMPRESIÓN";
        }
        
        /**
         * @return El JsonObject completo de la respuesta del health check
         */
        public JsonObject getHealthResponse() {
            return healthResponse;
        }
    }
}

