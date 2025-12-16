package com.application.useCases.shiftReports;

import com.application.core.BaseUseCasesWithParams;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.domain.dto.shiftReports.ShiftReportResult;
import com.google.gson.JsonObject;
import java.util.TreeMap;

/**
 * Use Case: Imprimir Información de Turno usando Print Ticket Service (Python)
 * 
 * Arquitectura Limpia: Use Case que encapsula la lógica de negocio
 * para enviar solicitudes de impresión de reportes de turno (Opción 3)
 * al microservicio Python.
 * 
 * Reemplaza: PrinterFacade.printJornadaActual() para Opción 3
 * 
 * @author Equipo Desarrollo
 * @version 1.0
 */
public class PrintShiftInformationRemoteUseCase implements BaseUseCasesWithParams<Void, ShiftReportResult> {
    
    private final String endpoint;
    private final long identificadorJornada;
    private final long identificadorPromotor;
    private final int copies;

    /**
     * Constructor para impresión de información de turno
     * 
     * @param identificadorJornada ID del turno (jornada.getGrupoJornada())
     * @param identificadorPromotor ID del promotor (Main.persona.getId())
     * @param copies Número de copias a imprimir (default: 1)
     */
    public PrintShiftInformationRemoteUseCase(long identificadorJornada, long identificadorPromotor, int copies) {
        this.identificadorJornada = identificadorJornada;
        this.identificadorPromotor = identificadorPromotor;
        this.copies = copies;
        this.endpoint = NovusConstante.PRINT_TICKET_SHIFT_INFORMATION;
    }
    
    /**
     * Constructor simplificado con 1 copia por defecto
     */
    public PrintShiftInformationRemoteUseCase(long identificadorJornada, long identificadorPromotor) {
        this(identificadorJornada, identificadorPromotor, 1);
    }

    @Override
    public ShiftReportResult execute(Void input) {
        try {
            // 1. Construir el payload JSON
            JsonObject payload = buildPayload();
            
            // 2. Log de inicio
            NovusUtils.printLn("[PrintShiftInformationRemoteUseCase] Enviando solicitud a: " + endpoint);
            NovusUtils.printLn("[PrintShiftInformationRemoteUseCase] Payload: " + payload.toString());
            
            // 3. Configurar headers HTTP
            TreeMap<String, String> headers = new TreeMap<>();
            headers.put("Content-Type", "application/json");
            
            // 4. Crear cliente HTTP asíncrono con timeout de 15 segundos (suficiente para esperar el timeout de la impresora)
            ClientWSAsync client = new ClientWSAsync(
                "PRINT_SHIFT_INFORMATION", 
                endpoint, 
                NovusConstante.POST, 
                payload, 
                NovusConstante.DEBUG_PRINT_TICKET,
                false, // isArray
                headers,
                15000   // Timeout HTTP de 15 segundos - permite que print-ticket intente conectar a la impresora (timeout 10s)
            );
            
            // 5. Ejecutar solicitud y esperar respuesta
            client.start();
            JsonObject response = null;
            
            try {
                client.join(15000); // Esperar máximo 15 segundos
                response = client.getResponse();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                NovusUtils.printLn("[PrintShiftInformationRemoteUseCase] ERROR: Interrumpido esperando respuesta");
                return ShiftReportResult.failure("Error al imprimir reporte");
            } catch (Exception e) {
                NovusUtils.printLn("[PrintShiftInformationRemoteUseCase] ERROR: " + e.getMessage());
                return ShiftReportResult.failure("Error al imprimir reporte: " + e.getMessage());
            }
            
            // 6. Validar respuesta del servicio Python
            if (response != null) {
                // Verificar si hay error en la respuesta
                if (response.has("success") && !response.get("success").getAsBoolean()) {
                    String errorMsg = response.has("message") 
                        ? response.get("message").getAsString() 
                        : "Error desconocido al imprimir reporte";
                    
                    // Logs técnicos detallados
                    NovusUtils.printLn("=====================================================");
                    NovusUtils.printLn("ERROR AL IMPRIMIR REPORTE DE TURNO (OPCION 3)");
                    NovusUtils.printLn("=====================================================");
                    NovusUtils.printLn("Mensaje técnico: " + errorMsg);
                    NovusUtils.printLn("Turno ID: " + identificadorJornada);
                    NovusUtils.printLn("Promotor ID: " + identificadorPromotor);
                    NovusUtils.printLn("");
                    NovusUtils.printLn("Posibles causas:");
                    NovusUtils.printLn("   - Impresora desconfigurada (IP incorrecta)");
                    NovusUtils.printLn("   - Impresora apagada/desconectada");
                    NovusUtils.printLn("   - Timeout de conexión con la impresora");
                    NovusUtils.printLn("=====================================================");
                    
                    return ShiftReportResult.failure(errorMsg);
                }
                
                // Respuesta exitosa
                if (response.has("print_job_id") || response.has("success")) {
                    String message = response.has("message") 
                        ? response.get("message").getAsString() 
                        : "Reporte impreso exitosamente";
                    
                    NovusUtils.printLn("[PrintShiftInformationRemoteUseCase] SUCCESS: " + message);
                    
                    if (response.has("print_job_id")) {
                        String jobId = response.get("print_job_id").getAsString();
                        NovusUtils.printLn("[PrintShiftInformationRemoteUseCase] Job ID: " + jobId);
                    }
                    
                    return ShiftReportResult.success(message);
                }
                
                // Respuesta sin campos esperados
                NovusUtils.printLn("[PrintShiftInformationRemoteUseCase] WARNING: Respuesta inesperada");
                NovusUtils.printLn("[PrintShiftInformationRemoteUseCase] Response: " + response.toString());
                return ShiftReportResult.failure("Respuesta inesperada del servicio");
            } else {
                // Si la respuesta es nula, verificar si hay un error en el cliente (como en BodegasFacade)
                NovusUtils.printLn("[DEBUG] response es null, verificando client.getError()...");
                JsonObject clientError = client.getError();
                NovusUtils.printLn("[DEBUG] client.getError() = " + (clientError != null ? clientError.toString() : "NULL"));
                
                if (clientError != null) {
                    JsonObject errorResponse = clientError;
                    String errorMsg = "Error desconocido al imprimir reporte";
                    
                    // Intentar extraer el mensaje de error del JsonObject
                    if (errorResponse.has("mensaje")) {
                        errorMsg = errorResponse.get("mensaje").getAsString();
                    } else if (errorResponse.has("message")) {
                        errorMsg = errorResponse.get("message").getAsString();
                    } else if (errorResponse.has("error")) {
                        errorMsg = errorResponse.get("error").getAsString();
                    }
                    
                    NovusUtils.printLn("[DEBUG] errorMsg extraído: " + errorMsg);
                    
                    // Logs técnicos detallados con el mensaje de error de print-ticket
                    NovusUtils.printLn("=====================================================");
                    NovusUtils.printLn("ERROR AL IMPRIMIR REPORTE DE TURNO (OPCION 3)");
                    NovusUtils.printLn("=====================================================");
                    NovusUtils.printLn("Mensaje técnico: " + errorMsg);
                    NovusUtils.printLn("Turno ID: " + identificadorJornada);
                    NovusUtils.printLn("Promotor ID: " + identificadorPromotor);
                    NovusUtils.printLn("");
                    NovusUtils.printLn("Posibles causas:");
                    NovusUtils.printLn("   - Impresora desconfigurada (IP incorrecta)");
                    NovusUtils.printLn("   - Impresora apagada/desconectada");
                    NovusUtils.printLn("   - Timeout de conexión con la impresora");
                    NovusUtils.printLn("=====================================================");
                    
                    return ShiftReportResult.failure(errorMsg);
                }
                
                // Si no hay error en el cliente, es un problema de conexión
                NovusUtils.printLn("=====================================================");
                NovusUtils.printLn("ERROR - SERVICIO PRINT_TICKET NO DISPONIBLE");
                NovusUtils.printLn("=====================================================");
                NovusUtils.printLn("Mensaje técnico: Respuesta nula del servicio");
                NovusUtils.printLn("URL: " + endpoint);
                NovusUtils.printLn("Turno ID: " + identificadorJornada);
                NovusUtils.printLn("Promotor ID: " + identificadorPromotor);
                NovusUtils.printLn("");
                NovusUtils.printLn("Posibles causas:");
                NovusUtils.printLn("   - Microservicio Python no está levantado (CAUSA MÁS PROBABLE)");
                NovusUtils.printLn("   - Error de red/conectividad");
                NovusUtils.printLn("   - Puerto incorrecto o bloqueado");
                NovusUtils.printLn("=====================================================");
                return ShiftReportResult.failure("No se pudo conectar al servicio de impresión");
            }
            
        } catch (Exception e) {
            NovusUtils.printLn("[PrintShiftInformationRemoteUseCase] ERROR: " + e.getMessage());
            e.printStackTrace();
            return ShiftReportResult.failure("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Construye el payload JSON según el formato esperado por el servicio Python
     */
    private JsonObject buildPayload() {
        JsonObject payload = new JsonObject();
        
        payload.addProperty("identificador_jornada", identificadorJornada);
        payload.addProperty("identificador_promotor", identificadorPromotor);
        payload.addProperty("copies", copies);
        payload.addProperty("force_reprint", false);
        payload.addProperty("flow_type", "INFORMACION_TURNO");
        
        return payload;
    }
}

