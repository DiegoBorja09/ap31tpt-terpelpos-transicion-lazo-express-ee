package com.infrastructure.adapters.out.gopass;

import com.application.constants.GopassConstants;
import com.application.ports.out.gopass.ImpresionPort;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.google.gson.JsonObject;

import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImpresionAdapter implements ImpresionPort {

    private static final Logger LOGGER = Logger.getLogger(ImpresionAdapter.class.getName());
    
    private static final int TIMEOUT_DEFAULT = 5000;

    @Override
    public ResultadoImpresion imprimir(long ventaId, String tipoDocumento, int timeoutMs) {
        LOGGER.info("[ADAPTER] Imprimiendo venta ID: " + ventaId + " - Tipo: " + tipoDocumento);
        
        JsonObject solicitud = construirSolicitudImpresion(ventaId, tipoDocumento);
        
        String url = NovusConstante.SECURE_CENTRAL_POINT_PRINT_TICKET_SALES;
        TreeMap<String, String> headers = new TreeMap<>();
        headers.put("Content-type", GopassConstants.CONTENT_TYPE_JSON);
        
        LOGGER.info("URL impresión: " + url + " (timeout: " + timeoutMs + "ms)");
        
        ClientWSAsync client = new ClientWSAsync(
            GopassConstants.FUNCION_IMPRIMIR_VENTAS,
            url,
            NovusConstante.POST,
            solicitud,
            true,
            false,
            headers,
            timeoutMs
        );
        
        LOGGER.info("Esperando respuesta del servicio de impresión (timeout: " + timeoutMs + "ms)");
        
        client.start();
        
        try {
            client.join(timeoutMs + 1000);
            
            int statusCode = client.getStatus();
            JsonObject error = client.getError();
            JsonObject response = client.getResponse();
            
            LOGGER.info("Respuesta recibida - Código HTTP: " + statusCode);
            
            if (statusCode >= 200 && statusCode < 300) {
                LOGGER.info("Impresión exitosa (HTTP " + statusCode + ") para venta: " + ventaId);
                NovusUtils.printLn("Venta " + ventaId + " impresa correctamente");
                return ResultadoImpresion.exitoso("Venta impresa correctamente");
            }
            
            if (statusCode >= 400) {
                String errorMsg = "Error HTTP " + statusCode;
                
                try {
                    if (response != null) {
                        if (response.has("message") && !response.get("message").isJsonNull()) {
                            errorMsg = response.get("message").getAsString();
                        } else if (response.has("mensajeError") && !response.get("mensajeError").isJsonNull()) {
                            errorMsg = response.get("mensajeError").getAsString();
                        }
                    }
                    
                    if (errorMsg.equals("Error HTTP " + statusCode) && error != null) {
                        if (error.has("message") && !error.get("message").isJsonNull()) {
                            errorMsg = error.get("message").getAsString();
                        } else if (error.has("mensajeError") && !error.get("mensajeError").isJsonNull()) {
                            errorMsg = error.get("mensajeError").getAsString();
                        }
                    }
                } catch (Exception e) {
                    LOGGER.warning("Error extrayendo mensaje de error: " + e.getMessage());
                }
                
                LOGGER.warning("Impresión falló (HTTP " + statusCode + ") para venta " + ventaId + ": " + errorMsg);
                NovusUtils.printLn("Impresión falló (HTTP " + statusCode + "): " + errorMsg);
                
                String errorLower = errorMsg.toLowerCase();
                if (statusCode == 503 || 
                    errorLower.contains("rechazó la conexión") || 
                    errorLower.contains("no responde") ||
                    errorLower.contains("no está activo") ||
                    errorLower.contains("no disponible")) {
                    return ResultadoImpresion.error(
                        errorMsg,
                        TipoError.SERVICIO_APAGADO
                    );
                }
                
                return ResultadoImpresion.error(
                    errorMsg,
                    TipoError.ERROR_RED
                );
            }
            
            if (error != null) {
                String errorMsg = error.has("mensajeError") && !error.get("mensajeError").isJsonNull()
                    ? error.get("mensajeError").getAsString() 
                    : "Error desconocido";
                
                LOGGER.warning("Error en impresión para venta " + ventaId + ": " + errorMsg);
                return analizarError(errorMsg, url, timeoutMs);
            }
            
            LOGGER.warning("Respuesta sin código de estado claro para venta " + ventaId);
            return ResultadoImpresion.error(
                "Respuesta inesperada del servicio de impresión", 
                TipoError.ERROR_DESCONOCIDO
            );
            
        } catch (InterruptedException e) {
            LOGGER.warning("Timeout esperando respuesta de impresión para venta " + ventaId);
            Thread.currentThread().interrupt();
            return ResultadoImpresion.error(
                "Timeout esperando respuesta de impresión", 
                TipoError.TIMEOUT
            );
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Excepción esperando respuesta de impresión", e);
            return ResultadoImpresion.error(
                "Error inesperado en impresión: " + e.getMessage(), 
                TipoError.ERROR_DESCONOCIDO
            );
        }
    }
    
    private ResultadoImpresion analizarError(String errorMsg, String url, int timeoutMs) {
        String mensajeLower = errorMsg.toLowerCase();
        
        if (mensajeLower.contains("connection refused") || mensajeLower.contains("connect")) {
            String mensaje = "SERVICIO DE IMPRESIÓN NO DISPONIBLE - " +
                           "El servicio está apagado o no responde. " +
                           "La venta se procesó correctamente pero NO se imprimió.";
            
            LOGGER.log(Level.WARNING, mensaje);
            NovusUtils.printLn("SERVICIO DE IMPRESIÓN APAGADO en: " + url);
            
            return ResultadoImpresion.error(mensaje, TipoError.SERVICIO_APAGADO);
        }
        
        if (mensajeLower.contains("timeout") || mensajeLower.contains("timed out")) {
            String mensaje = "TIMEOUT EN IMPRESIÓN - " +
                           "El servicio no respondió a tiempo (" + timeoutMs + "ms). " +
                           "La venta se procesó correctamente pero NO se imprimió.";
            
            LOGGER.log(Level.WARNING, mensaje);
            NovusUtils.printLn("TIMEOUT en servicio de impresión después de " + timeoutMs + "ms");
            
            return ResultadoImpresion.error(mensaje, TipoError.TIMEOUT);
        }
        
        String mensaje = "ERROR DE RED EN IMPRESIÓN - " + errorMsg + 
                       ". La venta se procesó correctamente pero NO se imprimió.";
        
        LOGGER.log(Level.WARNING, mensaje);
        NovusUtils.printLn("Error de red en impresión: " + errorMsg);
        
        return ResultadoImpresion.error(mensaje, TipoError.ERROR_RED);
    }

    /**
     * Construye la solicitud JSON para imprimir
     */
    private JsonObject construirSolicitudImpresion(long ventaId, String tipoDocumento) {
        JsonObject json = new JsonObject();
        json.addProperty("movement_id", ventaId);
        json.addProperty("flow_type", GopassConstants.TIPO);
        json.addProperty("report_type", tipoDocumento.toUpperCase());

        JsonObject bodyJson = construirDatosCliente();
        json.add("body", bodyJson);
        
        return json;
    }
    
    /**
     * Construye los datos del cliente por defecto
     */
    private JsonObject construirDatosCliente() {
        JsonObject bodyJson = new JsonObject();
        bodyJson.addProperty("tipoDocumento", GopassConstants.TIPO_DOCUMENTO_DEFAULT);
        bodyJson.addProperty("numeroDocumento", GopassConstants.NUMERO_DOCUMENTO_DEFAULT);
        bodyJson.addProperty("identificadorTipoPersona", GopassConstants.IDENTIFICADOR_TIPO_PERSONA_DEFAULT);
        bodyJson.addProperty("nombreComercial", GopassConstants.NOMBRE_COMERCIAL_DEFAULT);
        bodyJson.addProperty("nombreRazonSocial", GopassConstants.NOMBRE_COMERCIAL_DEFAULT);
        bodyJson.addProperty("ciudad", GopassConstants.CIUDAD_DEFAULT);
        bodyJson.addProperty("departamento", GopassConstants.DEPARTAMENTO_DEFAULT);
        bodyJson.addProperty("regimenFiscal", GopassConstants.REGIMEN_FISCAL_DEFAULT);
        bodyJson.addProperty("tipoResponsabilidad", GopassConstants.TIPO_RESPONSABILIDAD_DEFAULT);
        bodyJson.addProperty("codigoSAP", GopassConstants.CODIGO_SAP_DEFAULT);
        
        return bodyJson;
    }
}

