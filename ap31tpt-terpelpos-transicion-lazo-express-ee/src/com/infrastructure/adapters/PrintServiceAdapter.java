package com.infrastructure.adapters;

import com.bean.ResultBean;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.domain.dto.reportes.ArqueoProcesadoDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.TreeMap;

/**
 * 🔌 Adaptador para comunicación con microservicio de impresión Python.
 * 
 * Responsabilidades:
 * - Serializar DTOs a JSON
 * - Enviar peticiones HTTP ASÍNCRONAS al microservicio Python (usando ClientWSAsync)
 * - Parsear respuestas
 * - Manejar errores de conexión
 * 
 * ⚡ IMPORTANTE: Usa ClientWSAsync (Thread) para NO BLOQUEAR la interfaz gráfica
 * 
 * @author Infrastructure Layer
 * @version 2.2 - Producción (Asíncrono con ClientWSAsync)
 */
public class PrintServiceAdapter {
    
    private static final String PRINT_SERVICE_URL = "http://localhost:8001/api/print/arqueo-promotor";
    private static final int TIMEOUT_MS = 30000; // 30 segundos
    
    // 🧪 MOCK_MODE: Solo para desarrollo/testing
    // ⚠️ IMPORTANTE: Si MOCK_MODE = true, NO se llama al servicio Python real
    //    - Simula una impresión exitosa sin validar conectividad
    //    - Solo usar en desarrollo local cuando Python no está disponible
    //    - NUNCA dejar en true en producción
    private static final boolean MOCK_MODE = false;
    
    private final Gson gson;
    
    public PrintServiceAdapter() {
        this.gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .setPrettyPrinting()
                .create();
    }
    
    /**
     * 🖨️ Envía datos de arqueo al microservicio Python para impresión.
     * 
     * @param arqueoData Datos procesados del arqueo
     * @return ResultBean con resultado de la operación
     */
    public ResultBean imprimirArqueoPromotor(ArqueoProcesadoDTO arqueoData) {
        ResultBean result = new ResultBean();
        
        if (MOCK_MODE) {
            return imprimirMock(arqueoData);
        } else {
            return imprimirReal(arqueoData);
        }
    }
    
    /**
     * 🧪 Modo MOCK - Simula impresión (para desarrollo)
     */
    private ResultBean imprimirMock(ArqueoProcesadoDTO arqueoData) {
        ResultBean result = new ResultBean();
        
        try {
            NovusUtils.printLn("╔════════════════════════════════════════════════════════════╗");
            NovusUtils.printLn("║          🖨️  MOCK - SIMULACIÓN DE IMPRESIÓN               ║");
            NovusUtils.printLn("╚════════════════════════════════════════════════════════════╝");
            NovusUtils.printLn("");
            
            // Construir JSON que se enviaría a Python
            JsonObject requestBody = buildRequestBody(arqueoData);
            String jsonFormatted = gson.toJson(requestBody);
            
            NovusUtils.printLn("📤 JSON QUE SE ENVIARÍA AL MICROSERVICIO PYTHON:");
            NovusUtils.printLn("🌐 URL: " + PRINT_SERVICE_URL);
            NovusUtils.printLn("");
            NovusUtils.printLn(jsonFormatted);
            NovusUtils.printLn("");
            
            NovusUtils.printLn("═══════════════════════════════════════════════════════════");
            NovusUtils.printLn("📊 RESUMEN DEL ARQUEO:");
            NovusUtils.printLn("═══════════════════════════════════════════════════════════");
            NovusUtils.printLn("Promotor: " + arqueoData.getPromotor());
            NovusUtils.printLn("Turno ID: " + arqueoData.getTurnoId());
            NovusUtils.printLn("Fecha: " + arqueoData.getFechaInicio());
            NovusUtils.printLn("");
            NovusUtils.printLn("Ventas Combustible: " + arqueoData.getVentasCombustible().size() + " items");
            NovusUtils.printLn("Ventas Canastilla: " + arqueoData.getVentasCanastilla().size() + " items");
            NovusUtils.printLn("Ventas Market: " + arqueoData.getVentasMarket().size() + " items");
            NovusUtils.printLn("Medios de Pago: " + arqueoData.getMediosPago().size() + " medios");
            NovusUtils.printLn("");
            NovusUtils.printLn("Total General: $" + String.format("%,.0f", arqueoData.getTotales().getTotalGeneral()));
            NovusUtils.printLn("═══════════════════════════════════════════════════════════");
            NovusUtils.printLn("");
            
            // Simular respuesta exitosa
            result.setSuccess(true);
            result.setMessage("Impresión simulada correctamente (MODO MOCK)");
            // No usar setData() porque ResultBean solo acepta ArrayList<ResultDetailsBean>
            
            NovusUtils.printLn("═══════════════════════════════════════════════════════════");
            NovusUtils.printLn("⚠️  ADVERTENCIA: MODO MOCK ACTIVO - NO SE IMPRIMIÓ REALMENTE");
            NovusUtils.printLn("═══════════════════════════════════════════════════════════");
            NovusUtils.printLn("✅ SIMULACIÓN COMPLETADA");
            NovusUtils.printLn("   Para impresión real, cambiar MOCK_MODE = false en PrintServiceAdapter");
            NovusUtils.printLn("   ⚠️  NO usar MOCK_MODE en producción");
            NovusUtils.printLn("");
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Error en simulación: " + e.getMessage());
            NovusUtils.printLn("❌ Error en MOCK: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 🌐 Modo REAL - Llamada HTTP ASÍNCRONA al microservicio Python usando ClientWSAsync
     */
    private ResultBean imprimirReal(ArqueoProcesadoDTO arqueoData) {
        ResultBean result = new ResultBean();
        
        try {
            // Construir request body
            JsonObject requestBody = buildRequestBody(arqueoData);
            
            NovusUtils.printLn("╔════════════════════════════════════════════════════════════╗");
            NovusUtils.printLn("║  INICIANDO IMPRESIÓN CON SERVICIO PYTHON                  ║");
            NovusUtils.printLn("╚════════════════════════════════════════════════════════════╝");
            NovusUtils.printLn("Conectando al servidor Python de forma ASÍNCRONA...");
            NovusUtils.printLn("URL: " + PRINT_SERVICE_URL);
            NovusUtils.printLn("Timeout: " + TIMEOUT_MS + "ms");
            
            // Preparar headers personalizados para Python (sin headers de autenticación de Terpel)
            TreeMap<String, String> headers = new TreeMap<>();
            headers.put("Content-Type", "application/json; charset=UTF-8");
            headers.put("Accept", "application/json");
            
            // Crear cliente asíncrono (extiende Thread, NO bloquea UI)
            ClientWSAsync client = new ClientWSAsync(
                "PRINT_ARQUEO_PROMOTOR",    // función
                PRINT_SERVICE_URL,           // url
                NovusConstante.POST,         // method
                requestBody,                 // json payload
                true,                        // DEBUG
                false,                       // isArray
                headers,                     // headers personalizados
                TIMEOUT_MS                   // timeout
            );
            
            // Ejecutar petición de forma ASÍNCRONA y esperar respuesta
            // start() + join() = ejecuta en hilo separado pero espera resultado
            JsonObject response = client.esperaRespuesta();
            
            // Procesar respuesta
            int statusCode = client.getStatus();
            NovusUtils.printLn("📡 Respuesta recibida - HTTP " + statusCode);
            
            if (statusCode >= 200 && statusCode < 300 && response != null) {
                //  Respuesta exitosa (HTTP 2xx)
                boolean success = response.has("success") ? 
                    response.get("success").getAsBoolean() : true;
                
                String message = response.has("message") ? 
                    response.get("message").getAsString() : "Impresión completada";
                
                result.setSuccess(success);
                result.setMessage(message);
                
                if (success) {
                    NovusUtils.printLn("✅ Arqueo impreso correctamente en Python (ASÍNCRONO)");
                } else {
                    NovusUtils.printLn("⚠️  Python respondió con success=false: " + message);
                }
                
            } else if (statusCode >= 500 && response != null) {
                // ❌ Error del servidor Python (HTTP 5xx)
                // Intentar extraer el mensaje específico que Python envió
                String errorMessage = "Error al procesar la impresión. Intente nuevamente";
                
                if (response.has("detail")) {
                    // FastAPI envía errores en el campo "detail"
                    String pythonMessage = response.get("detail").getAsString();
                    
                    // Si Python envió un mensaje específico (ej: error de impresora), usarlo
                    if (pythonMessage != null && !pythonMessage.isEmpty()) {
                        errorMessage = analizarMensajeError(pythonMessage);
                    }
                } else if (response.has("message")) {
                    // También verificar el campo "message"
                    String pythonMessage = response.get("message").getAsString();
                    if (pythonMessage != null && !pythonMessage.isEmpty()) {
                        errorMessage = analizarMensajeError(pythonMessage);
                    }
                } else if (response.has("error")) {
                    // También verificar el campo "error"
                    String pythonMessage = response.get("error").getAsString();
                    if (pythonMessage != null && !pythonMessage.isEmpty()) {
                        errorMessage = analizarMensajeError(pythonMessage);
                    }
                }
                
                result.setSuccess(false);
                result.setMessage(errorMessage);
                
                // 📋 Logs técnicos detallados
                NovusUtils.printLn("═══════════════════════════════════════════════════════════");
                NovusUtils.printLn("❌ ERROR HTTP " + statusCode + " desde Python");
                NovusUtils.printLn("═══════════════════════════════════════════════════════════");
                NovusUtils.printLn("Mensaje: " + errorMessage);
                NovusUtils.printLn("URL: " + PRINT_SERVICE_URL);
                NovusUtils.printLn("═══════════════════════════════════════════════════════════");
                
            } else if (client.getError() != null) {
                // ❌ Error capturado por ClientWSAsync
                JsonObject error = client.getError();
                String errorMessage = "Error al procesar la impresión. Intente nuevamente";
                
                // Intentar extraer mensaje del error
                if (error.has("detail")) {
                    String pythonMessage = error.get("detail").getAsString();
                    if (pythonMessage != null && !pythonMessage.isEmpty()) {
                        errorMessage = analizarMensajeError(pythonMessage);
                    }
                } else if (error.has("message")) {
                    String pythonMessage = error.get("message").getAsString();
                    if (pythonMessage != null && !pythonMessage.isEmpty()) {
                        errorMessage = analizarMensajeError(pythonMessage);
                    }
                } else if (error.has("error")) {
                    String pythonMessage = error.get("error").getAsString();
                    if (pythonMessage != null && !pythonMessage.isEmpty()) {
                        errorMessage = analizarMensajeError(pythonMessage);
                    }
                }
                
                result.setSuccess(false);
                result.setMessage(errorMessage);
                
                // 📋 Logs técnicos detallados
                NovusUtils.printLn("❌ Error capturado por ClientWSAsync - HTTP " + statusCode);
                NovusUtils.printLn("   Error: " + error.toString());
                
            } else if (response == null) {
                // ❌ Timeout o error de conexión - Mensaje amigable para usuario
                result.setSuccess(false);
                result.setMessage("No se pudo conectar al servicio de impresión");
                
                // 📋 Logs técnicos detallados para soporte
                NovusUtils.printLn("╔════════════════════════════════════════════════════════════╗");
                NovusUtils.printLn("║  SERVICIO DE IMPRESIÓN PYTHON ESTÁ APAGADO                ║");
                NovusUtils.printLn("╚════════════════════════════════════════════════════════════╝");
                NovusUtils.printLn("ERROR: Sin respuesta del microservicio Python");
                NovusUtils.printLn("URL: " + PRINT_SERVICE_URL);
                NovusUtils.printLn("");
                NovusUtils.printLn("Posibles causas:");
                NovusUtils.printLn("   - Microservicio Python no está levantado (CAUSA MÁS PROBABLE)");
                NovusUtils.printLn("   - Error de red/conectividad");
                NovusUtils.printLn("");
                NovusUtils.printLn("Solución: Verificar que el servicio Python esté corriendo en el puerto 8001");
                
            } else {
                // ❌ Código de estado no exitoso - Mensaje amigable
                String errorMessage = "Error al procesar la impresión. Intente nuevamente";
                
                // Intentar extraer mensaje específico de la respuesta
                if (response.has("detail")) {
                    String pythonMessage = response.get("detail").getAsString();
                    if (pythonMessage != null && !pythonMessage.isEmpty()) {
                        errorMessage = analizarMensajeError(pythonMessage);
                    }
                } else if (response.has("message")) {
                    String pythonMessage = response.get("message").getAsString();
                    if (pythonMessage != null && !pythonMessage.isEmpty()) {
                        errorMessage = analizarMensajeError(pythonMessage);
                    }
                } else if (response.has("error")) {
                    String pythonMessage = response.get("error").getAsString();
                    if (pythonMessage != null && !pythonMessage.isEmpty()) {
                        errorMessage = analizarMensajeError(pythonMessage);
                    }
                }
                
                result.setSuccess(false);
                result.setMessage(errorMessage);
                
                // 📋 Logs técnicos detallados para soporte
                NovusUtils.printLn("❌ Error HTTP: " + statusCode);
                if (response.has("detail")) {
                    NovusUtils.printLn("   Detalle: " + response.get("detail").getAsString());
                }
            }
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Error inesperado: " + e.getMessage());
            NovusUtils.printLn(" EXCEPCIÓN INESPERADA en PrintServiceAdapter:");
            NovusUtils.printLn("   Tipo: " + e.getClass().getName());
            NovusUtils.printLn("   Mensaje: " + e.getMessage());
            NovusUtils.printLn("   URL: " + PRINT_SERVICE_URL);
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 🔍 Analiza el mensaje de error del servicio de printikkt y devuelve un mensaje amigable
     * Detecta errores relacionados con la impresora no disponible
     * 
     * @param mensajeError Mensaje de error original del servicio
     * @return Mensaje de error amigable para el usuario
     */
    private String analizarMensajeError(String mensajeError) {
        if (mensajeError == null || mensajeError.isEmpty()) {
            return "Error al procesar la impresión. Intente nuevamente";
        }
        
        String mensajeLower = mensajeError.toLowerCase();
        
        // Detectar errores relacionados con la impresora no disponible
        if (mensajeLower.contains("impresora") && 
            (mensajeLower.contains("no disponible") || 
             mensajeLower.contains("no está disponible") ||
             mensajeLower.contains("no disponible") ||
             mensajeLower.contains("no conectada") ||
             mensajeLower.contains("desconectada") ||
             mensajeLower.contains("no responde") ||
             mensajeLower.contains("timeout") ||
             mensajeLower.contains("connection refused") ||
             mensajeLower.contains("connection error") ||
             mensajeLower.contains("no se pudo conectar") ||
             mensajeLower.contains("no se puede conectar"))) {
            return "Impresora no disponible. Verifique la conexión de la impresora";
        }
        
        // Detectar errores de conexión con la impresora
        if (mensajeLower.contains("connection") && 
            (mensajeLower.contains("refused") || 
             mensajeLower.contains("timeout") ||
             mensajeLower.contains("error") ||
             mensajeLower.contains("failed"))) {
            return "Error de conexión con la impresora. Verifique que la impresora esté encendida y conectada";
        }
        
        // Detectar errores de timeout específicos de impresora
        if (mensajeLower.contains("timeout") && 
            (mensajeLower.contains("impresora") || 
             mensajeLower.contains("printer") ||
             mensajeLower.contains("print"))) {
            return "La impresora no responde. Verifique la conexión";
        }
        
        // Si el mensaje ya es amigable, devolverlo tal cual
        // Si contiene información técnica, simplificarlo
        if (mensajeLower.contains("error al procesar") || 
            mensajeLower.contains("error processing")) {
            return mensajeError; // Mantener el mensaje original si ya es descriptivo
        }
        
        // Por defecto, devolver el mensaje original si no coincide con ningún patrón conocido
        return mensajeError;
    }
    
    /**
     * 🏗️ Construye el body del request para el microservicio Python
     */
    private JsonObject buildRequestBody(ArqueoProcesadoDTO arqueoData) {
        JsonObject body = new JsonObject();
        
        body.addProperty("reporte", "ARQUEO_PROMOTOR");
        body.addProperty("identificadorMovimiento", arqueoData.getTurnoId());
        
        // Datos del arqueo en el body
        JsonObject data = new JsonObject();
        data.addProperty("promotor", arqueoData.getPromotor());
        data.addProperty("promotor_id", arqueoData.getPromotorId());
        data.addProperty("fecha_inicio", arqueoData.getFechaInicio().toString());
        
        // Convertir DTOs a JSON
        data.add("ventas_combustible", gson.toJsonTree(arqueoData.getVentasCombustible()));
        data.add("ventas_canastilla", gson.toJsonTree(arqueoData.getVentasCanastilla()));
        data.add("ventas_market", gson.toJsonTree(arqueoData.getVentasMarket()));
        data.add("ventas_complementarios", gson.toJsonTree(arqueoData.getVentasComplementarios()));
        data.add("medios_pago", gson.toJsonTree(arqueoData.getMediosPago()));
        data.add("totales", gson.toJsonTree(arqueoData.getTotales()));
        
        // Metadata
        data.addProperty("num_transacciones_combustible", arqueoData.getNumeroTransaccionesCombustible());
        data.addProperty("num_transacciones_canastilla", arqueoData.getNumeroTransaccionesCanastilla());
        data.addProperty("num_transacciones_market", arqueoData.getNumeroTransaccionesMarket());
        
        body.add("body", data);
        
        return body;
    }
}

