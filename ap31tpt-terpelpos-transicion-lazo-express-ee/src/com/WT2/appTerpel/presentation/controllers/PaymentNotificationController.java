/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.presentation.controllers;

import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.appTerpel.presentation.Mapper.NotificationPaymentMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author USUARIO
 */
public class PaymentNotificationController {

    private static final String PRINT_QUEUE_FILE = "logs/print_queue.txt";

    public void execute(HttpExchange exchange) {

        try {
            // Leer el body del request
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            StringBuilder bodyBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                bodyBuilder.append(line);
            }
            String body = bodyBuilder.toString();
            
            // Parsear el JSON del body
            JsonObject requestJson = JsonParser.parseString(body).getAsJsonObject();
            
            // Verificar si es una notificación de reporte
            // Puede tener report_id y report_type (ventas) o solo report_type (inventario)
            boolean tieneReportType = requestJson.has("report_type");
            boolean tieneReportId = requestJson.has("report_id");
            
            if (tieneReportType) {
                // Es una notificación de reporte - solo eliminar de la cola sin mostrar pantalla
                handleReportNotification(requestJson, exchange);
                return;
            }
            
            // Si no es notificación de reporte, procesar normalmente
            // Recrear el InputStream para el mapper (ya que fue consumido)
            exchange.setStreams(new java.io.ByteArrayInputStream(body.getBytes("utf-8")), exchange.getResponseBody());
            
            NotificationPaymentMapper mapper = new NotificationPaymentMapper();
            JsonObject jsonObject = SingletonMedioPago.ConetextDependecy.getNotifyPaymentAlert().execute(mapper.mapTo(exchange));
            String response = jsonObject.toString();
            System.out.println(response);
            SingletonMedioPago.ConetextDependecy.getDefineViewToAlert().execute(jsonObject);
            Headers respHeaders = exchange.getResponseHeaders();
            respHeaders.add("content-type", "application/json; charset=utf-8");
            respHeaders.add("Access-Control-Allow-Origin", "*");
            byte[] responseBytes = response.getBytes("utf-8");
            exchange.sendResponseHeaders(200, responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();

        } catch (Exception ex) {
            Logger.getLogger(PaymentNotificationController.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    /**
     * Maneja las notificaciones de reportes - elimina el registro de la cola sin mostrar pantalla
     * Soporta dos casos:
     * 1. Con report_id y report_type (ventas) - busca por ambos
     * 2. Solo con report_type (inventario) - busca solo por report_type
     */
    private void handleReportNotification(JsonObject requestJson, HttpExchange exchange) {
        try {
            String reportType = requestJson.get("report_type").getAsString();
            
            // Verificar si report_id existe, no es null, y no está vacío
            boolean tieneReportId = requestJson.has("report_id") 
                && !requestJson.get("report_id").isJsonNull()
                && !requestJson.get("report_id").getAsString().trim().isEmpty();
            
            String reportId = tieneReportId ? requestJson.get("report_id").getAsString() : null;
            boolean estado = requestJson.has("estado") && requestJson.get("estado").getAsBoolean();
            
            System.out.println("[REPORT NOTIFICATION] Recibida notificación de reporte:");
            if (tieneReportId && reportId != null) {
                System.out.println("  - report_id: " + reportId);
            } else {
                System.out.println("  - report_id: (no proporcionado o vacío - búsqueda solo por report_type)");
            }
            System.out.println("  - report_type: " + reportType);
            System.out.println("  - estado: " + estado);
            
            // Eliminar de la cola de impresión
            boolean eliminado;
            if (tieneReportId && reportId != null && !reportId.trim().isEmpty()) {
                // Caso 1: Tiene report_id válido - buscar por ID y tipo (ventas)
                try {
                    eliminado = eliminarDeColaPendiente(Long.parseLong(reportId.trim()), reportType);
                } catch (NumberFormatException e) {
                    System.out.println("[REPORT NOTIFICATION] Error: report_id no es un número válido: " + reportId);
                    // Si el report_id no es válido, buscar solo por tipo
                    eliminado = eliminarDeColaPendientePorTipo(reportType);
                }
            } else {
                // Caso 2: No tiene report_id o está vacío - buscar solo por report_type (inventario)
                eliminado = eliminarDeColaPendientePorTipo(reportType);
            }
            
            // Responder al request
            JsonObject response = new JsonObject();
            response.addProperty("success", eliminado);
            response.addProperty("message", eliminado ? "Registro eliminado de la cola" : "Registro no encontrado en la cola");
            if (tieneReportId) {
                response.addProperty("report_id", reportId);
            }
            response.addProperty("report_type", reportType);
            
            String responseStr = response.toString();
            Headers respHeaders = exchange.getResponseHeaders();
            respHeaders.add("content-type", "application/json");
            respHeaders.add("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, responseStr.getBytes("utf-8").length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseStr.getBytes());
            os.close();
            
            System.out.println("[REPORT NOTIFICATION] Respuesta: " + responseStr);
            
        } catch (Exception ex) {
            Logger.getLogger(PaymentNotificationController.class.getName()).log(Level.SEVERE, 
                    "Error procesando notificación de reporte", ex);
        }
    }

    /**
     * Elimina un registro de la cola de impresión pendiente
     * @param id El ID del reporte
     * @param reportType El tipo de reporte
     * @return true si se eliminó, false si no se encontró
     */
    private synchronized boolean eliminarDeColaPendiente(long id, String reportType) {
        try {
            File file = new File(PRINT_QUEUE_FILE);
            
            if (!file.exists() || file.length() == 0) {
                System.out.println("[REPORT NOTIFICATION] Archivo de cola no existe o está vacío");
                return false;
            }
            
            // Leer el archivo
            JsonArray registros;
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                
                if (content.length() == 0) {
                    return false;
                }
                
                registros = JsonParser.parseString(content.toString()).getAsJsonArray();
            }
            
            // Buscar y eliminar el registro
            JsonArray nuevosRegistros = new JsonArray();
            boolean encontrado = false;
            
            for (JsonElement elemento : registros) {
                JsonObject registro = elemento.getAsJsonObject();
                long registroId = registro.has("id") ? registro.get("id").getAsLong() : 0;
                String registroType = registro.has("report_type") ? registro.get("report_type").getAsString() : "";
                
                // Si coincide el ID y el tipo, no lo agregamos (lo eliminamos)
                if (registroId == id && registroType.equalsIgnoreCase(reportType)) {
                    encontrado = true;
                    System.out.println("[REPORT NOTIFICATION] Eliminando registro - ID: " + id + ", Tipo: " + reportType);
                } else {
                    nuevosRegistros.add(registro);
                }
            }
            
            if (encontrado) {
                // Guardar el archivo sin el registro eliminado
                Gson gson = new Gson();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(gson.toJson(nuevosRegistros));
                }
                System.out.println("[REPORT NOTIFICATION] Registro eliminado exitosamente. Registros restantes: " + nuevosRegistros.size());
            }
            
            return encontrado;
            
        } catch (Exception e) {
            System.err.println("[REPORT NOTIFICATION] Error eliminando de cola: " + e.getMessage());
            Logger.getLogger(PaymentNotificationController.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    /**
     * Elimina un registro de la cola de impresión pendiente solo por report_type (sin report_id)
     * Usado para inventario donde no hay ID, solo report_type
     * @param reportType El tipo de reporte (ej: INVENTARIO_CANASTILLA, INVENTARIO_MARKET)
     * @return true si se eliminó, false si no se encontró
     */
    private synchronized boolean eliminarDeColaPendientePorTipo(String reportType) {
        try {
            File file = new File(PRINT_QUEUE_FILE);
            
            if (!file.exists() || file.length() == 0) {
                System.out.println("[REPORT NOTIFICATION] Archivo de cola no existe o está vacío");
                return false;
            }
            
            // Leer el archivo
            JsonArray registros;
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                
                if (content.length() == 0) {
                    return false;
                }
                
                registros = JsonParser.parseString(content.toString()).getAsJsonArray();
            }
            
            // Buscar y eliminar el registro solo por report_type
            JsonArray nuevosRegistros = new JsonArray();
            boolean encontrado = false;
            
            for (JsonElement elemento : registros) {
                JsonObject registro = elemento.getAsJsonObject();
                String registroType = registro.has("report_type") ? registro.get("report_type").getAsString() : "";
                
                // Si coincide el tipo (sin verificar ID), no lo agregamos (lo eliminamos)
                if (registroType.equalsIgnoreCase(reportType)) {
                    encontrado = true;
                    System.out.println("[REPORT NOTIFICATION] Eliminando registro por tipo - Tipo: " + reportType);
                } else {
                    nuevosRegistros.add(registro);
                }
            }
            
            if (encontrado) {
                // Guardar el archivo sin el registro eliminado
                Gson gson = new Gson();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(gson.toJson(nuevosRegistros));
                }
                System.out.println("[REPORT NOTIFICATION] Registro eliminado exitosamente por tipo. Registros restantes: " + nuevosRegistros.size());
                
                // Notificar a la vista para desbloquear botones si es necesario
                // Esto se puede hacer mediante un listener o evento si es necesario
                // Por ahora, la vista verificará automáticamente al hacer click en la tabla
            }
            
            return encontrado;
            
        } catch (Exception e) {
            System.out.println("[REPORT NOTIFICATION] Error eliminando registro por tipo: " + e.getMessage());
            Logger.getLogger(PaymentNotificationController.class.getName()).log(Level.SEVERE, 
                    "Error eliminando registro de cola por tipo", e);
            return false;
        }
    }

}
