package com.firefuel.asignarCliente.useCase;

import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.FacturacionElectronicaDao;
import com.firefuel.Main;
import com.google.gson.JsonObject;
import java.util.TreeMap;
import com.application.useCases.wacherparametros.FindByParameterUseCase;
import com.application.commons.CtWacherParametrosEnum;

/**
 * 🔄 MIGRADO A SERVICIO PYTHON
 * Impresión de ventas desde asignación de cliente
 * 
 * @version 2.0 - Migrado a Python
 */
public class ImprimirVenta {
    
    public static void imprimirVentaFe(long id, long idTransmision) {
        FindByParameterUseCase findByParameterUseCase = new FindByParameterUseCase(CtWacherParametrosEnum.CODIGO.getColumnName(), "REMISION");
        long idMovimiento = 0L;
        if (findByParameterUseCase.execute()) {
            idMovimiento = Main.mdao.buscarMOvimientoIdRemision(idTransmision);
        } else {
            idMovimiento = Main.mdao.buscarMOvimientoId(id);
        }
        
        // 🚀 MIGRACIÓN A SERVICIO PYTHON - INICIO
        NovusUtils.printLn("╔════════════════════════════════════════════════════════════╗");
        NovusUtils.printLn("║  🐍 SERVICIO PYTHON - ASIGNAR CLIENTE                    ║");
        NovusUtils.printLn("╚════════════════════════════════════════════════════════════╝");
        NovusUtils.printLn("📋 Imprimiendo venta desde asignación de cliente");
        NovusUtils.printLn("   - ID Venta: " + id);
        NovusUtils.printLn("   - ID Transmisión: " + idTransmision);
        NovusUtils.printLn("   - ID Movimiento: " + idMovimiento);
        
        String funcion = "IMPRIMIR VENTAS - ASIGNAR CLIENTE";
        JsonObject json = new JsonObject();
        json.addProperty("identificadorMovimiento", idMovimiento);
        json.addProperty("movement_id", idMovimiento);
        json.addProperty("flow_type", "ASIGNAR_CLIENTE");
        
        boolean esRemision = findByParameterUseCase.execute();
        String tipoDocumento = esRemision ? "REMISION" : "FACTURA-ELECTRONICA";
        json.addProperty("report_type", tipoDocumento);
        
        NovusUtils.printLn("   - Tipo Documento: " + tipoDocumento);
        
        // URL del servicio Python
        String url = NovusConstante.SECURE_CENTRAL_POINT_PRINT_TICKET_SALES;
        NovusUtils.printLn("🌐 URL Servicio Python: " + url);
        NovusUtils.printLn("📤 Payload: " + json.toString());
        
        TreeMap<String, String> header = new TreeMap<>();
        header.put("Content-type", "application/json; charset=UTF-8");
        header.put("Accept", "application/json");
        
        if (!esRemision) {
            FacturacionElectronicaDao electronicaDao = new FacturacionElectronicaDao();
            electronicaDao.actualizarEstadoImpresion(idMovimiento);
            NovusUtils.printLn("✅ Estado de impresión actualizado en BD");
        }
        
        ClientWSAsync client = new ClientWSAsync(funcion, url, NovusConstante.POST, json, true, false, header, 10000);
        client.start();
        
        JsonObject response = client.getResponse();
        if (response != null) {
            NovusUtils.printLn("✅ Respuesta del servicio Python recibida: " + response.toString());
        } else {
            NovusUtils.printLn("⚠️  Sin respuesta del servicio Python (puede estar apagado)");
        }
        
        NovusUtils.printLn("═══════════════════════════════════════════════════════════");
        // 🚀 MIGRACIÓN A SERVICIO PYTHON - FIN
    }

}
