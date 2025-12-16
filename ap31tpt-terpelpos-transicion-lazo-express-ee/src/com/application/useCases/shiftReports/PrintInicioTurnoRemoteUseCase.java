package com.application.useCases.shiftReports;

import com.application.core.BaseUseCasesWithParams;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.domain.dto.shiftReports.ShiftReportResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.application.useCases.printService.CheckPrintServiceHealthUseCase;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Use Case: Imprimir Inicio de Turno usando Print Ticket Service (Python)
 * 
 * Endpoint independiente para imprimir inicio de turno sin pasar por LazoExpress.
 * 
 * @author Equipo Desarrollo
 * @version 1.0
 */
public class PrintInicioTurnoRemoteUseCase implements BaseUseCasesWithParams<Void, ShiftReportResult> {
    
    private static final String PRINT_QUEUE_FILE = "logs/print_queue.txt";
    private static final Logger logger = Logger.getLogger(PrintInicioTurnoRemoteUseCase.class.getName());
    
    // Caso de uso para health check del servicio de impresión (con cache integrado)
    private final CheckPrintServiceHealthUseCase checkPrintServiceHealthUseCase = new CheckPrintServiceHealthUseCase();
    
    private final String endpoint;
    private final long identificadorJornada;
    private final long identificadorPromotor;
    private final int pos;
    private final int copies;

    /**
     * Constructor para impresión de inicio de turno
     * 
     * @param identificadorJornada ID de la jornada (grupos_jornadas.id)
     * @param identificadorPromotor ID del promotor (responsables.identificador)
     * @param pos ID del POS/equipo
     * @param copies Número de copias a imprimir (default: 1)
     */
    public PrintInicioTurnoRemoteUseCase(long identificadorJornada, long identificadorPromotor, int pos, int copies) {
        this.identificadorJornada = identificadorJornada;
        this.identificadorPromotor = identificadorPromotor;
        this.pos = pos;
        this.copies = copies;
        this.endpoint = NovusConstante.PRINT_TICKET_TURNO_INICIO;
    }
    
    /**
     * Constructor simplificado con 1 copia por defecto
     */
    public PrintInicioTurnoRemoteUseCase(long identificadorJornada, long identificadorPromotor, int pos) {
        this(identificadorJornada, identificadorPromotor, pos, 1);
    }

    @Override
    public ShiftReportResult execute(Void input) {
        try {
            // 1. Verificar si ya está en cola de impresión
            String reportType = "IMPRIMIR_TURNO_INICIO";
            if (existeEnColaPendiente(identificadorJornada, reportType)) {
                NovusUtils.printLn("[PrintInicioTurnoRemoteUseCase] El registro ya está en cola de impresión - ID: " + identificadorJornada);
                return ShiftReportResult.failure("La impresión ya está en proceso");
            }
            
            // 2. HEALTH CHECK: Verificar servicio de impresión antes de imprimir (usando caso de uso con cache)
            NovusUtils.printLn("[PrintInicioTurnoRemoteUseCase] 🔍 Verificando servicio de impresión...");
            CheckPrintServiceHealthUseCase.HealthCheckResult healthResult = checkPrintServiceHealthUseCase.execute(null);
            
            if (!healthResult.tieneRespuesta() || !healthResult.esSaludable()) {
                NovusUtils.printLn("[PrintInicioTurnoRemoteUseCase] ❌ Servicio de impresión no está disponible");
                String mensaje = healthResult.obtenerMensajeError();
                return ShiftReportResult.failure(mensaje);
            }
            
            NovusUtils.printLn("[PrintInicioTurnoRemoteUseCase] ✅ Servicio de impresión activo e impresora conectada");
            
            // 3. Guardar registro en cola antes de enviar
            guardarRegistroPendiente(identificadorJornada, reportType);
            
            // 4. Construir el payload JSON
            NovusUtils.printLn("[PrintInicioTurnoRemoteUseCase] ===== INICIO ======");
            NovusUtils.printLn("[PrintInicioTurnoRemoteUseCase] Construyendo payload...");
            JsonObject payload = buildPayload();
            
            // 5. Log de inicio
            NovusUtils.printLn("[PrintInicioTurnoRemoteUseCase] Endpoint: " + endpoint);
            NovusUtils.printLn("[PrintInicioTurnoRemoteUseCase] Parámetros recibidos:");
            NovusUtils.printLn("  - identificador_jornada: " + identificadorJornada);
            NovusUtils.printLn("  - identificador_promotor: " + identificadorPromotor);
            NovusUtils.printLn("  - pos: " + pos);
            NovusUtils.printLn("  - copies: " + copies);
            NovusUtils.printLn("[PrintInicioTurnoRemoteUseCase] Payload JSON: " + payload.toString());
            
            // 6. Configurar headers HTTP
            TreeMap<String, String> headers = new TreeMap<>();
            headers.put("Content-Type", "application/json");
            
            // 7. Crear cliente HTTP asíncrono con timeout de 15 segundos
            ClientWSAsync client = new ClientWSAsync(
                "PRINT_INICIO_TURNO", 
                endpoint, 
                NovusConstante.POST, 
                payload, 
                NovusConstante.DEBUG_PRINT_TICKET,
                false, // isArray
                headers,
                15000   // Timeout HTTP de 15 segundos
            );
            
            // 8. Ejecutar solicitud sin esperar respuesta (asíncrono)
            NovusUtils.printLn("[PrintInicioTurnoRemoteUseCase] Iniciando solicitud HTTP (asíncrono)...");
            client.start();
            
            // Retornar respuesta exitosa inmediatamente
            NovusUtils.printLn("[PrintInicioTurnoRemoteUseCase] ✅ Request de impresión enviado - Jornada: " + identificadorJornada + ", Tipo: IMPRIMIR_TURNO_INICIO");
            return ShiftReportResult.success("Trabajo agregado a la cola de impresión");
            
        } catch (Exception e) {
            NovusUtils.printLn("[PrintInicioTurnoRemoteUseCase] ERROR: " + e.getMessage());
            // Eliminar de la cola en caso de error
            eliminarRegistroPendiente(identificadorJornada, "IMPRIMIR_TURNO_INICIO");
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
        
        return payload;
    }
    
    /**
     * Verifica si un ID existe en la cola de impresión pendiente
     * @param id El ID del registro a buscar (identificador_jornada)
     * @param reportType El tipo de reporte (IMPRIMIR_TURNO_INICIO)
     * @return true si existe en la cola, false si no existe
     */
    private synchronized boolean existeEnColaPendiente(long id, String reportType) {
        try {
            File file = new File(PRINT_QUEUE_FILE);
            
            if (!file.exists() || file.length() == 0) {
                return false;
            }
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                
                if (content.length() > 0) {
                    JsonArray registros = JsonParser.parseString(content.toString()).getAsJsonArray();
                    for (JsonElement elemento : registros) {
                        JsonObject registro = elemento.getAsJsonObject();
                        if (registro.has("id") && registro.get("id").getAsLong() == id 
                            && registro.has("report_type") && registro.get("report_type").getAsString().equals(reportType)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            NovusUtils.printLn("Error verificando cola de impresión: " + e.getMessage());
            logger.log(Level.SEVERE, "Error verificando cola de impresión", e);
        }
        return false;
    }
    
    /**
     * Guarda un registro de impresión pendiente en el archivo TXT
     * @param id El ID de la jornada
     * @param reportType El tipo de reporte (IMPRIMIR_TURNO_INICIO)
     */
    private synchronized void guardarRegistroPendiente(long id, String reportType) {
        try {
            // Crear carpeta logs si no existe
            File logsFolder = new File("logs");
            if (!logsFolder.exists()) {
                logsFolder.mkdir();
            }

            // Leer archivo existente o crear nuevo array
            JsonArray registros = new JsonArray();
            File file = new File(PRINT_QUEUE_FILE);
            
            if (file.exists() && file.length() > 0) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line);
                    }
                    if (content.length() > 0) {
                        registros = JsonParser.parseString(content.toString()).getAsJsonArray();
                    }
                } catch (Exception e) {
                    NovusUtils.printLn("Error leyendo archivo de cola de impresión: " + e.getMessage());
                    registros = new JsonArray();
                }
            }

            // Verificar si el ID ya existe en el array (doble verificación para evitar duplicados)
            for (JsonElement elemento : registros) {
                JsonObject registro = elemento.getAsJsonObject();
                if (registro.has("id") && registro.get("id").getAsLong() == id
                    && registro.has("report_type") && registro.get("report_type").getAsString().equals(reportType)) {
                    NovusUtils.printLn("Registro ya existe en cola de impresión - ID: " + id + " (no se duplica)");
                    return; // No agregar si ya existe
                }
            }

            // Crear nuevo registro
            JsonObject nuevoRegistro = new JsonObject();
            nuevoRegistro.addProperty("id", id);
            nuevoRegistro.addProperty("report_type", reportType);
            nuevoRegistro.addProperty("status", "PENDING");
            nuevoRegistro.addProperty("message", "IMPRIMIENDO...");

            // Agregar al array
            registros.add(nuevoRegistro);

            // Guardar archivo con formato legible
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(gson.toJson(registros));
            }

            NovusUtils.printLn("Registro guardado en cola de impresión - ID: " + id + ", Tipo: " + reportType);

        } catch (Exception e) {
            NovusUtils.printLn("Error guardando registro en cola de impresión: " + e.getMessage());
            logger.log(Level.SEVERE, "Error guardando registro en cola de impresión", e);
        }
    }
    
    /**
     * Elimina un registro de la cola de impresión del archivo TXT
     * Se llama cuando se recibe notificación del servicio o cuando hay error
     * @param id El ID de la jornada a eliminar
     * @param reportType El tipo de reporte (IMPRIMIR_TURNO_INICIO)
     */
    private synchronized void eliminarRegistroPendiente(long id, String reportType) {
        try {
            File file = new File(PRINT_QUEUE_FILE);
            
            if (!file.exists() || file.length() == 0) {
                NovusUtils.printLn("No hay registros en cola de impresión para eliminar");
                return;
            }
            
            // Leer archivo existente
            JsonArray registros;
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                if (content.length() > 0) {
                    registros = JsonParser.parseString(content.toString()).getAsJsonArray();
                } else {
                    return;
                }
            }
            
            // Buscar y eliminar el registro
            JsonArray registrosActualizados = new JsonArray();
            boolean encontrado = false;
            for (JsonElement elemento : registros) {
                JsonObject registro = elemento.getAsJsonObject();
                if (registro.has("id") && registro.get("id").getAsLong() == id
                    && registro.has("report_type") && registro.get("report_type").getAsString().equals(reportType)) {
                    encontrado = true;
                    NovusUtils.printLn("🗑️ Eliminando registro de cola de impresión - ID: " + id + ", Tipo: " + reportType);
                    // No agregar este registro (lo eliminamos)
                } else {
                    registrosActualizados.add(registro);
                }
            }
            
            if (encontrado) {
                // Guardar archivo actualizado
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(gson.toJson(registrosActualizados));
                }
                NovusUtils.printLn("✅ Registro eliminado de cola de impresión - ID: " + id);
            }
            
        } catch (Exception e) {
            NovusUtils.printLn("Error eliminando registro de cola de impresión: " + e.getMessage());
            logger.log(Level.SEVERE, "Error eliminando registro de cola de impresión", e);
        }
    }
    
}

