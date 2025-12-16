package com.application.useCases.shiftReports;

import com.application.core.BaseUseCasesWithParams;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.domain.dto.shiftReports.ShiftReportResult;
import com.google.gson.JsonObject;
import java.util.TreeMap;

/**
 * Use Case: Imprimir Fin de Turno usando Print Ticket Service (Python)
 * 
 * Endpoint independiente para imprimir fin de turno sin pasar por LazoExpress.
 * 
 * @author Equipo Desarrollo
 * @version 1.0
 */
public class PrintFinTurnoRemoteUseCase implements BaseUseCasesWithParams<Void, ShiftReportResult> {
    
    private final String endpoint;
    private final long identificadorJornada;
    private final long identificadorPromotor;
    private final int pos;
    private final int copies;
    private final boolean forceReprint;

    /**
     * Constructor para impresión de fin de turno
     * 
     * @param identificadorJornada ID de la jornada (grupos_jornadas.id)
     * @param identificadorPromotor ID del promotor (responsables.identificador)
     * @param pos ID del POS/equipo
     * @param copies Número de copias a imprimir (default: 1)
     * @param forceReprint Forzar reimpresión (default: false)
     */
    public PrintFinTurnoRemoteUseCase(long identificadorJornada, long identificadorPromotor, int pos, int copies, boolean forceReprint) {
        this.identificadorJornada = identificadorJornada;
        this.identificadorPromotor = identificadorPromotor;
        this.pos = pos;
        this.copies = copies;
        this.forceReprint = forceReprint;
        this.endpoint = NovusConstante.PRINT_TICKET_TURNO_FIN;
    }
    
    /**
     * Constructor simplificado con 1 copia y sin forzar reimpresión
     */
    public PrintFinTurnoRemoteUseCase(long identificadorJornada, long identificadorPromotor, int pos) {
        this(identificadorJornada, identificadorPromotor, pos, 1, false);
    }

    @Override
    public ShiftReportResult execute(Void input) {
        try {
            // 1. Construir el payload JSON
            NovusUtils.printLn("[PrintFinTurnoRemoteUseCase] ===== INICIO ======");
            NovusUtils.printLn("[PrintFinTurnoRemoteUseCase] Construyendo payload...");
            JsonObject payload = buildPayload();
            
            // 2. Log de inicio
            NovusUtils.printLn("[PrintFinTurnoRemoteUseCase] Endpoint: " + endpoint);
            NovusUtils.printLn("[PrintFinTurnoRemoteUseCase] Parámetros recibidos:");
            NovusUtils.printLn("  - identificador_jornada: " + identificadorJornada);
            NovusUtils.printLn("  - identificador_promotor: " + identificadorPromotor);
            NovusUtils.printLn("  - pos: " + pos);
            NovusUtils.printLn("  - copies: " + copies);
            NovusUtils.printLn("  - force_reprint: " + forceReprint);
            NovusUtils.printLn("[PrintFinTurnoRemoteUseCase] Payload JSON: " + payload.toString());
            
            // 3. Configurar headers HTTP
            TreeMap<String, String> headers = new TreeMap<>();
            headers.put("Content-Type", "application/json");
            
            // 4. Crear cliente HTTP asíncrono con timeout de 30 segundos (el fin de turno puede tardar más)
            ClientWSAsync client = new ClientWSAsync(
                "PRINT_FIN_TURNO", 
                endpoint, 
                NovusConstante.POST, 
                payload, 
                NovusConstante.DEBUG_PRINT_TICKET,
                false, // isArray
                headers,
                30000   // Timeout HTTP de 30 segundos (más tiempo porque genera reporte completo)
            );
            
            // 5. Ejecutar solicitud y esperar respuesta
            NovusUtils.printLn("[PrintFinTurnoRemoteUseCase] Iniciando solicitud HTTP...");
            long requestStartTime = System.currentTimeMillis();
            client.start();
            JsonObject response = null;
            
            try {
                NovusUtils.printLn("[PrintFinTurnoRemoteUseCase] Esperando respuesta (timeout: 30s)...");
                client.join(30000); // Esperar máximo 30 segundos
                response = client.getResponse();
                long requestEndTime = System.currentTimeMillis();
                long requestDuration = requestEndTime - requestStartTime;
                NovusUtils.printLn("[PrintFinTurnoRemoteUseCase] Respuesta recibida en " + requestDuration + " ms");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                NovusUtils.printLn("[PrintFinTurnoRemoteUseCase] ✗ ERROR: Interrumpido esperando respuesta");
                return ShiftReportResult.failure("Error al imprimir fin de turno");
            } catch (Exception e) {
                NovusUtils.printLn("[PrintFinTurnoRemoteUseCase] ✗ ERROR: " + e.getMessage());
                NovusUtils.printLn("[PrintFinTurnoRemoteUseCase] Tipo de excepción: " + e.getClass().getName());
                return ShiftReportResult.failure("Error al imprimir fin de turno: " + e.getMessage());
            }
            
            // 6. Validar respuesta del servicio Python
            if (response != null) {
                // Verificar si hay error en la respuesta
                if (response.has("success") && !response.get("success").getAsBoolean()) {
                    String errorMsg = response.has("message") 
                        ? response.get("message").getAsString() 
                        : "Error desconocido al imprimir fin de turno";
                    
                    NovusUtils.printLn("[PrintFinTurnoRemoteUseCase] ERROR: " + errorMsg);
                    return ShiftReportResult.failure(errorMsg);
                }
                
                // Respuesta exitosa
                if (response.has("print_job_id") || response.has("success")) {
                    String message = response.has("message") 
                        ? response.get("message").getAsString() 
                        : "Fin de turno impreso exitosamente";
                    
                    NovusUtils.printLn("[PrintFinTurnoRemoteUseCase] SUCCESS: " + message);
                    
                    if (response.has("print_job_id")) {
                        String jobId = response.get("print_job_id").getAsString();
                        NovusUtils.printLn("[PrintFinTurnoRemoteUseCase] Job ID: " + jobId);
                    }
                    
                    return ShiftReportResult.success(message);
                }
                
                // Respuesta sin campos esperados
                NovusUtils.printLn("[PrintFinTurnoRemoteUseCase] WARNING: Respuesta inesperada");
                NovusUtils.printLn("[PrintFinTurnoRemoteUseCase] Response: " + response.toString());
                return ShiftReportResult.failure("Respuesta inesperada del servicio");
            } else {
                // Si la respuesta es nula, verificar si hay un error en el cliente
                JsonObject clientError = client.getError();
                
                if (clientError != null) {
                    String errorMsg = "Error desconocido al imprimir fin de turno";
                    
                    if (clientError.has("mensaje")) {
                        errorMsg = clientError.get("mensaje").getAsString();
                    } else if (clientError.has("message")) {
                        errorMsg = clientError.get("message").getAsString();
                    } else if (clientError.has("error")) {
                        errorMsg = clientError.get("error").getAsString();
                    }
                    
                    NovusUtils.printLn("[PrintFinTurnoRemoteUseCase] ERROR: " + errorMsg);
                    return ShiftReportResult.failure(errorMsg);
                }
                
                // Si no hay error en el cliente, es un problema de conexión
                NovusUtils.printLn("[PrintFinTurnoRemoteUseCase] ERROR - SERVICIO PRINT_TICKET NO DISPONIBLE");
                return ShiftReportResult.failure("No se pudo conectar al servicio de impresión");
            }
            
        } catch (Exception e) {
            NovusUtils.printLn("[PrintFinTurnoRemoteUseCase] ERROR: " + e.getMessage());
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
        payload.addProperty("pos", pos);
        payload.addProperty("copies", copies);
        payload.addProperty("force_reprint", forceReprint);
        
        return payload;
    }
}

