package com.infrastructure.adapters;

import com.bean.ResultBean;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.TreeMap;

/**
 * 🔌 Adaptador para comunicación con microservicio Python de impresión de Factura Electrónica.
 * 
 * Responsabilidades (Infrastructure Layer):
 * - Serializar DTOs a JSON
 * - Enviar peticiones HTTP ASÍNCRONAS al microservicio Python
 * - Parsear respuestas del servicio
 * - Manejar errores de conexión y timeouts
 * 
 * ⚡ IMPORTANTE: Usa ClientWSAsync (Thread) para NO BLOQUEAR la interfaz gráfica
 * 
 * @author Infrastructure Layer
 * @version 1.0 - Arquitectura Hexagonal
 */
public class PrintFacturaElectronicaPythonAdapter {
    
    private static final String PRINT_SERVICE_URL = NovusConstante.SECURE_CENTRAL_POINT_PRINT_TICKET_SALES;
    private static final int TIMEOUT_MS = 30000; // 30 segundos
    
    private final Gson gson;
    
    public PrintFacturaElectronicaPythonAdapter() {
        this.gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .setPrettyPrinting()
                .create();
    }
    
    /**
     * 🖨️ Envía datos de venta con FE al microservicio Python para impresión.
     * 
     * @param ventaData Datos completos de la venta con FE (JSON original del sistema)
     * @return ResultBean con resultado de la operación
     */
    public ResultBean imprimirFacturaElectronica(JsonObject ventaData) {
        ResultBean result = new ResultBean();
        
        try {
            // Construir request body según schema del servicio Python
            JsonObject requestBody = buildRequestBody(ventaData);
            
            NovusUtils.printLn("🌐 Conectando al servidor Python de forma ASÍNCRONA...");
            NovusUtils.printLn("   URL: " + PRINT_SERVICE_URL);
            
            // Preparar headers personalizados para Python
            TreeMap<String, String> headers = new TreeMap<>();
            headers.put("Content-Type", "application/json; charset=UTF-8");
            headers.put("Accept", "application/json");
            
            // Crear cliente asíncrono (extiende Thread, NO bloquea UI)
            ClientWSAsync client = new ClientWSAsync(
                "PRINT_FACTURA_ELECTRONICA_PYTHON",
                PRINT_SERVICE_URL,
                NovusConstante.POST,
                requestBody,
                true,  // DEBUG
                false, // isArray
                headers,
                TIMEOUT_MS
            );
            
            // Ejecutar petición de forma ASÍNCRONA (no esperar respuesta)
            // Los logs se generarán en el thread de ClientWSAsync
            client.start();
            
            // Retornar éxito inmediatamente - la impresión se procesa en segundo plano
            result.setSuccess(true);
            result.setMessage("Impresión enviada correctamente al servicio");
            NovusUtils.printLn("✅ Petición enviada al servicio Python (asíncrono) - Los logs aparecerán cuando el servicio responda");
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Error al procesar la impresión. Intente nuevamente");
            NovusUtils.printLn(" EXCEPCIÓN INESPERADA en PrintFacturaElectronicaPythonAdapter:");
            NovusUtils.printLn("   Tipo: " + e.getClass().getName());
            NovusUtils.printLn("   Mensaje: " + e.getMessage());
            NovusUtils.printLn("   URL: " + PRINT_SERVICE_URL);
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 🏗️ Construye el body del request para el microservicio Python.
     * 
     * ⚠️ IMPORTANTE: Compatibilidad con servicio anterior (puerto 8063)
     * El servicio anterior recibía TODO el JSON con los datos completos y NO consultaba la BD.
     * Python puede trabajar de dos formas:
     * 1. Si viene body con datos → usar esos datos (compatibilidad puerto 8063)
     * 2. Si viene body vacío → consultar BD
     * 
     * Schema esperado por Python:
     * {
     *   "flow_type": "CONSULTAR_VENTAS",
     *   "movement_id": 12345,
     *   "report_type": "FACTURA-ELECTRONICA",
     *   "body": { venta, detalle, pagos, cliente, observaciones, etc. }
     * }
     */
    private JsonObject buildRequestBody(JsonObject ventaData) {
        JsonObject requestBody = new JsonObject();
        
        // Extraer movimiento ID desde diferentes posibles ubicaciones
        int movimientoId = extraerMovimientoId(ventaData);
        if (movimientoId > 0) {
            requestBody.addProperty("movement_id", movimientoId);
            NovusUtils.printLn("📋 Movimiento ID extraído: " + movimientoId);
        } else {
            //  ERROR CRÍTICO: Sin ID no se puede imprimir
            String errorMsg = "ERROR CRÍTICO: No se pudo extraer movement_id del JSON. " +
                "El servicio Python requiere este campo obligatoriamente.";
            NovusUtils.printLn(" " + errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        
        //  Schema de Python para /print-ticket/sales
        requestBody.addProperty("flow_type", "CONSULTAR_VENTAS");
        requestBody.addProperty("report_type", "FACTURA-ELECTRONICA");
        
        // Enviar TODOS los datos en el body (compatibilidad con servicio anterior puerto 8063)
        // Python usará estos datos directamente en lugar de consultar la BD
        requestBody.add("body", ventaData);
        NovusUtils.printLn(" Body incluye: venta, detalle, pagos, cliente, observaciones");
        
        // 🔍 LOG PARA DEBUGGING: Verificar datos del cliente que se envían a Python
        if (ventaData.has("cliente")) {
            JsonObject cliente = ventaData.getAsJsonObject("cliente");
            NovusUtils.printLn("═══════════════════════════════════════════════════════════");
            NovusUtils.printLn("🔍 VERIFICACIÓN DATOS DEL CLIENTE ENVIADOS A PYTHON:");
            NovusUtils.printLn("═══════════════════════════════════════════════════════════");
            
            if (cliente.has("nombreComercial") && !cliente.get("nombreComercial").isJsonNull()) {
                NovusUtils.printLn("   nombreComercial: " + cliente.get("nombreComercial").getAsString());
            } else {
                NovusUtils.printLn("   nombreComercial: NULL o NO EXISTE");
            }
            
            if (cliente.has("nombreRazonSocial") && !cliente.get("nombreRazonSocial").isJsonNull()) {
                NovusUtils.printLn("   nombreRazonSocial: " + cliente.get("nombreRazonSocial").getAsString());
            } else {
                NovusUtils.printLn("   nombreRazonSocial: NULL o NO EXISTE");
            }
            
            if (cliente.has("numeroDocumento") && !cliente.get("numeroDocumento").isJsonNull()) {
                NovusUtils.printLn("   numeroDocumento: " + cliente.get("numeroDocumento").getAsString());
            } else {
                NovusUtils.printLn("   numeroDocumento: NULL o NO EXISTE");
            }
            
            NovusUtils.printLn("═══════════════════════════════════════════════════════════");
            NovusUtils.printLn(" JSON COMPLETO DEL CLIENTE:");
            NovusUtils.printLn(cliente.toString());
            NovusUtils.printLn("═══════════════════════════════════════════════════════════");
        } else {
            NovusUtils.printLn("═══════════════════════════════════════════════════════════");
            NovusUtils.printLn(" ADVERTENCIA: El JSON NO CONTIENE campo 'cliente'");
            NovusUtils.printLn("═══════════════════════════════════════════════════════════");
        }
        
        return requestBody;
    }
    
    /**
     * 🔍 Extrae el ID de movimiento del JSON de venta.
     * Intenta múltiples ubicaciones posibles según estructura del JSON.
     */
    private int extraerMovimientoId(JsonObject ventaData) {
        try {
            NovusUtils.printLn(" Extrayendo identificadorMovimiento...");
            NovusUtils.printLn("   JSON recibido: " + ventaData.toString());
            
            // Opción 1: Directamente en el root
            if (ventaData.has("identificadorMovimiento")) {
                int id = ventaData.get("identificadorMovimiento").getAsInt();
                NovusUtils.printLn(" Encontrado en root.identificadorMovimiento: " + id);
                return id;
            }
            
            // Opción 2: Dentro de "venta" → buscar varios campos posibles
            if (ventaData.has("venta") && ventaData.get("venta").isJsonObject()) {
                JsonObject venta = ventaData.getAsJsonObject("venta");
                
                // 2.1: venta.id_venta (ESTE ES EL QUE USA FACTURA ELECTRÓNICA)
                if (venta.has("id_venta")) {
                    int id = venta.get("id_venta").getAsInt();
                    NovusUtils.printLn(" Encontrado en venta.id_venta: " + id);
                    return id;
                }
                
                // 2.2: venta.id
                if (venta.has("id")) {
                    int id = venta.get("id").getAsInt();
                    NovusUtils.printLn(" Encontrado en venta.id: " + id);
                    return id;
                }
                
                // 2.3: venta.idMovimiento
                if (venta.has("idMovimiento")) {
                    int id = venta.get("idMovimiento").getAsInt();
                    NovusUtils.printLn(" Encontrado en venta.idMovimiento: " + id);
                    return id;
                }
                
                // 2.4: venta.identificadorMovimiento
                if (venta.has("identificadorMovimiento")) {
                    int id = venta.get("identificadorMovimiento").getAsInt();
                    NovusUtils.printLn(" Encontrado en venta.identificadorMovimiento: " + id);
                    return id;
                }
                
                // 2.5: venta.consecutivo_id
                if (venta.has("consecutivo_id")) {
                    int id = venta.get("consecutivo_id").getAsInt();
                    NovusUtils.printLn(" Encontrado en venta.consecutivo_id: " + id);
                    return id;
                }
            }
            
            // Opción 3: Campo "id" directo
            if (ventaData.has("id")) {
                int id = ventaData.get("id").getAsInt();
                NovusUtils.printLn(" Encontrado en root.id: " + id);
                return id;
            }
            
            // Opción 4: Campo "idMovimiento" directo
            if (ventaData.has("idMovimiento")) {
                int id = ventaData.get("idMovimiento").getAsInt();
                NovusUtils.printLn(" Encontrado en root.idMovimiento: " + id);
                return id;
            }
            
            NovusUtils.printLn("⚠️  No se encontró identificadorMovimiento en ninguna ubicación");
            NovusUtils.printLn("   Claves disponibles en root: " + ventaData.keySet());
            if (ventaData.has("venta") && ventaData.get("venta").isJsonObject()) {
                NovusUtils.printLn("   Claves disponibles en venta: " + 
                    ventaData.getAsJsonObject("venta").keySet());
            }
            
        } catch (Exception ex) {
            NovusUtils.printLn(" Error al extraer movimientoId: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return 0;
    }
}

