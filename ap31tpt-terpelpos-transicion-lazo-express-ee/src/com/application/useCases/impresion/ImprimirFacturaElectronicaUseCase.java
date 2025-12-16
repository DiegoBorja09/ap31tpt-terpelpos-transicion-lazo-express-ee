package com.application.useCases.impresion;

import com.application.core.AbstractUseCase;
import com.bean.ResultBean;
import com.controllers.NovusUtils;
import com.controllers.NovusConstante;
import com.controllers.ClientWSAsync;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.infrastructure.adapters.PrintFacturaElectronicaPythonAdapter;
import com.application.useCases.printService.CheckPrintServiceHealthUseCase;
import java.util.TreeMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 🎯 Caso de uso para imprimir Factura Electrónica usando el microservicio Python.
 * 
 * Responsabilidades (Application Layer):
 * - Recibir datos de la venta con FE
 * - Validar datos de entrada
 * - Delegar impresión al adaptador (Infrastructure)
 * - Retornar resultado normalizado
 * 
 * Flujo:
 * 1. Validar que los datos de venta sean válidos
 * 2. Llamar al adaptador de Python
 * 3. Procesar y retornar el resultado
 * 
 * @author Application Layer - Clean Architecture
 * @version 1.0
 */
public class ImprimirFacturaElectronicaUseCase extends AbstractUseCase<ResultBean> {
    
    private final JsonObject ventaData;
    private final PrintFacturaElectronicaPythonAdapter printAdapter;
    private static final String PRINT_QUEUE_FILE = "logs/print_queue.txt";
    
    // Caso de uso para health check del servicio de impresión (con cache integrado)
    private final CheckPrintServiceHealthUseCase checkPrintServiceHealthUseCase = new CheckPrintServiceHealthUseCase();
    
    /**
     * Constructor principal
     * @param ventaData Datos completos de la venta con FE
     */
    public ImprimirFacturaElectronicaUseCase(JsonObject ventaData) {
        this.ventaData = ventaData;
        this.printAdapter = new PrintFacturaElectronicaPythonAdapter();
    }
    
    /**
     * Constructor con inyección de dependencias (para testing)
     * @param ventaData Datos de la venta
     * @param printAdapter Adaptador mockeado para pruebas
     */
    public ImprimirFacturaElectronicaUseCase(JsonObject ventaData, PrintFacturaElectronicaPythonAdapter printAdapter) {
        this.ventaData = ventaData;
        this.printAdapter = printAdapter;
    }
    
    @Override
    public ResultBean run() {
        try {
            NovusUtils.printLn("═══════════════════════════════════════════════════════════");
            NovusUtils.printLn("║    ARQUITECTURA HEXAGONAL - FACTURA ELECTRÓNICA      ║");
            NovusUtils.printLn("═══════════════════════════════════════════════════════════");
            NovusUtils.printLn("🖨️ Iniciando impresión de Factura Electrónica vía Python");
            
            // 1. Validar datos de entrada
            if (ventaData == null || ventaData.isJsonNull()) {
                ResultBean errorResult = new ResultBean();
                errorResult.setSuccess(false);
                errorResult.setMessage("Error: Datos de venta no pueden estar vacíos");
                NovusUtils.printLn(" Validación falló: ventaData es null");
                return errorResult;
            }
            
            // 2. Validar que exista información de venta
            if (!ventaData.has("venta") && !ventaData.has("id")) {
                ResultBean errorResult = new ResultBean();
                errorResult.setSuccess(false);
                errorResult.setMessage("Error: No se encontró información de venta en los datos");
                NovusUtils.printLn(" Validación falló: JSON no contiene 'venta' ni 'id'");
                return errorResult;
            }
            
            NovusUtils.printLn(" Datos validados correctamente");
            
            // 3. HEALTH CHECK: Verificar servicio de impresión antes de enviar (usando caso de uso con cache)
            NovusUtils.printLn("🔍 Verificando servicio de impresión (health check)...");
            CheckPrintServiceHealthUseCase.HealthCheckResult healthResult = checkPrintServiceHealthUseCase.execute(null);
            
            if (!healthResult.tieneRespuesta() || !healthResult.esSaludable()) {
                ResultBean errorResult = new ResultBean();
                errorResult.setSuccess(false);
                errorResult.setMessage(healthResult.obtenerMensajeError());
                NovusUtils.printLn("❌ Health check falló: " + errorResult.getMessage());
                return errorResult;
            }
            
            NovusUtils.printLn("✅ Health check OK - Servicio e impresora disponibles");
            
            // 4. Extraer movement_id y guardar en cola
            long movementId = extraerMovementId(ventaData);
            if (movementId > 0) {
                NovusUtils.printLn("📝 Guardando en cola - Movement ID: " + movementId);
                guardarRegistroPendiente(movementId, "CONSULTAR_VENTAS");
            }
            
            // 5. Enviar impresión de forma ASÍNCRONA (el adaptador ya usa start())
            NovusUtils.printLn("📤 Enviando datos al microservicio Python (asíncrono)...");
            printAdapter.imprimirFacturaElectronica(ventaData);
            
            // Retornar éxito inmediatamente después de enviar (status 200)
            ResultBean successResult = new ResultBean();
            successResult.setSuccess(true);
            successResult.setMessage("Impresión enviada correctamente al servicio");
            
            NovusUtils.printLn("✅ Impresión enviada - Retornando success inmediatamente");
            NovusUtils.printLn("═══════════════════════════════════════════════════════════");
            
            return successResult;
            
        } catch (Exception e) {
            NovusUtils.printLn(" Excepción al imprimir Factura Electrónica: " + e.getMessage());
            e.printStackTrace();
            
            ResultBean errorResult = new ResultBean();
            errorResult.setSuccess(false);
            errorResult.setMessage("Error inesperado al imprimir: " + e.getMessage());
            return errorResult;
        }
    }
    
    // ============================================
    // MÉTODOS DE HEALTH CHECK
    // ============================================
    
    
    // ============================================
    // MÉTODOS DE COLA DE IMPRESIÓN
    // ============================================
    
    /**
     * Extrae el movement_id del JSON de venta
     */
    private long extraerMovementId(JsonObject ventaData) {
        try {
            if (ventaData.has("venta") && ventaData.get("venta").isJsonObject()) {
                JsonObject venta = ventaData.getAsJsonObject("venta");
                if (venta.has("id_venta")) {
                    return venta.get("id_venta").getAsLong();
                }
            }
            if (ventaData.has("id")) {
                return ventaData.get("id").getAsLong();
            }
        } catch (Exception e) {
            NovusUtils.printLn("Error extrayendo movement_id: " + e.getMessage());
        }
        return 0;
    }
    
    /**
     * Guarda un registro en la cola de impresión pendiente
     */
    private void guardarRegistroPendiente(long id, String reportType) {
        try {
            File queueFile = new File(PRINT_QUEUE_FILE);
            File parentDir = queueFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            JsonArray registros = new JsonArray();
            if (queueFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(queueFile))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line);
                    }
                    if (content.length() > 0) {
                        registros = com.google.gson.JsonParser.parseString(content.toString()).getAsJsonArray();
                    }
                } catch (Exception e) {
                    NovusUtils.printLn("Error leyendo archivo de cola de impresión: " + e.getMessage());
                    registros = new JsonArray();
                }
            }
            
            JsonObject nuevoRegistro = new JsonObject();
            nuevoRegistro.addProperty("id", id);
            nuevoRegistro.addProperty("report_type", reportType);
            nuevoRegistro.addProperty("timestamp", System.currentTimeMillis());
            registros.add(nuevoRegistro);
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(queueFile))) {
                writer.write(registros.toString());
            }
            
            NovusUtils.printLn("✅ Registro guardado en cola de impresión - ID: " + id + ", Tipo: " + reportType);
            
        } catch (Exception e) {
            NovusUtils.printLn("Error guardando registro en cola de impresión: " + e.getMessage());
            Logger.getLogger(ImprimirFacturaElectronicaUseCase.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}

