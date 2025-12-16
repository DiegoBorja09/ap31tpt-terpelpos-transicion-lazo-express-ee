package com.application.useCases.inventario;

import com.application.core.BaseUseCasesWithParams;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.google.gson.JsonObject;
import java.util.TreeMap;

/**
 * Use Case: Imprimir inventario usando Print Ticket Service (Python)
 * 
 * Arquitectura Limpia: Use Case que encapsula la lógica de negocio
 * para enviar solicitudes de impresión al microservicio Python.
 * 
 * Toda la información necesaria se proporciona en el constructor,
 * por lo que el método execute() no requiere parámetros adicionales.
 * 
 * @author Equipo Desarrollo
 * @version 1.0
 */
public class PrintInventoryRemoteUseCase implements BaseUseCasesWithParams<Void, PrintInventoryRemoteUseCase.ResultadoImpresion> {
    
    private final String endpoint;
    private final String tipoInventario;
    private final int numeroPOS;
    private final boolean soloSaldoPositivo;
    private final int copies;

    /**
     * Constructor para impresión de inventario
     * 
     * @param tipoInventario CANASTILLA, KIOSCO, CDL, MARKET
     * @param numeroPOS Número del POS
     * @param soloSaldoPositivo true para imprimir solo saldo positivo
     * @param copies Número de copias a imprimir
     */
    public PrintInventoryRemoteUseCase(String tipoInventario, int numeroPOS, 
                                      boolean soloSaldoPositivo, int copies) {
        this.tipoInventario = tipoInventario;
        this.numeroPOS = numeroPOS;
        this.soloSaldoPositivo = soloSaldoPositivo;
        this.copies = copies;
        
        // Seleccionar endpoint según si es solo saldo positivo o todos
        this.endpoint = soloSaldoPositivo 
            ? NovusConstante.PRINT_TICKET_INVENTORY_PRINT_POSITIVE
            : NovusConstante.PRINT_TICKET_INVENTORY_PRINT;
    }

    @Override
    public ResultadoImpresion execute(Void input) {
        try {
            // 1. Construir el payload JSON
            JsonObject payload = buildPayload();
            
            // 2. Log de inicio
            NovusUtils.printLn("[PrintInventoryRemoteUseCase] Enviando solicitud a: " + endpoint);
            NovusUtils.printLn("Payload: " + payload.toString());
            
            // 3. Configurar headers HTTP
            TreeMap<String, String> headers = new TreeMap<>();
            headers.put("Content-Type", "application/json");
            
            // 4. Crear cliente HTTP asíncrono
            ClientWSAsync client = new ClientWSAsync(
                "PRINT_INVENTORY", 
                endpoint, 
                NovusConstante.POST, 
                payload, 
                NovusConstante.DEBUG_PRINT_TICKET, // DEBUG
                false, // isArray
                headers,
                10000 // Timeout de 10 segundos (solo para el envío, no esperamos respuesta)
            );
            
            // 5. Enviar solicitud SIN esperar respuesta (fire and forget)
            // La notificación llegará después vía PaymentNotificationController
            client.start();
            
            NovusUtils.printLn("[PrintInventoryRemoteUseCase] ✅ Solicitud de impresión enviada al servicio Python");
            NovusUtils.printLn("   - Endpoint: " + endpoint);
            NovusUtils.printLn("   - Tipo: " + tipoInventario);
            NovusUtils.printLn("   - Solo saldo positivo: " + soloSaldoPositivo);
            NovusUtils.printLn("   - Copias: " + copies);
            NovusUtils.printLn("   - La notificación llegará cuando la impresión se complete");
            
            // Retornar éxito inmediatamente - la notificación llegará después
            return ResultadoImpresion.exito("IMPRESIÓN ENVIADA EXITOSAMENTE");
            
        } catch (Exception e) {
            NovusUtils.printLn("[PrintInventoryRemoteUseCase] Error: " + e.getMessage());
            e.printStackTrace();
            return ResultadoImpresion.error("No se pudo conectar al servicio de impresión");
        }
    }

    /**
     * Construye el payload JSON según el formato esperado por el servicio Python
     */
    private JsonObject buildPayload() {
        JsonObject payload = new JsonObject();
        
        // Mapear tipo de inventario a tipo de negocio
        String tipoNegocio = mapTipoInventarioToTipoNegocio(tipoInventario);

        payload.addProperty("tipo_inventario", tipoInventario);
        // Diferenciar report_type según si es saldo positivo o todos los productos
        String reportType = soloSaldoPositivo 
            ? "INVENTARIO_" + tipoInventario + "_SALDO_POSITIVO"
            : "INVENTARIO_" + tipoInventario;
        payload.addProperty("report_type", reportType);
        payload.addProperty("numero_pos", numeroPOS);
        payload.addProperty("solo_saldo_positivo", soloSaldoPositivo);
        payload.addProperty("tipo_negocio", tipoNegocio);
        payload.addProperty("copies", copies);
        
        return payload;
    }
    
    /**
     * Mapea el tipo de inventario al tipo de negocio correspondiente
     */
    private String mapTipoInventarioToTipoNegocio(String tipo) {
        switch (tipo) {
            case NovusConstante.INVENTORY_TYPE_KIOSCO:
            case NovusConstante.INVENTORY_TYPE_MARKET:
                return "KIOSCO";
            case NovusConstante.INVENTORY_TYPE_CDL:
                return "CDL";
            case NovusConstante.INVENTORY_TYPE_CANASTILLA:
            default:
                return "CANASTILLA";
        }
    }

    public static class ResultadoImpresion {
        private final boolean exito;
        private final String mensaje;

        private ResultadoImpresion(boolean exito, String mensaje) {
            this.exito = exito;
            this.mensaje = mensaje;
        }

        public static ResultadoImpresion exito(String mensaje) {
            return new ResultadoImpresion(true, mensaje);
        }

        public static ResultadoImpresion error(String mensaje) {
            return new ResultadoImpresion(false, mensaje);
        }

        public boolean esExito() {
            return exito;
        }

        public String getMensaje() {
            return mensaje;
        }
    }
}

