package com.firefuel.controlImpresion.useCase;

import com.WT2.ImpresonesFE.domain.entities.RespuestaFeImprimir;
import com.WT2.commons.domain.adapters.IHttpClientRepository;
import com.WT2.commons.domain.entity.Request;
import com.WT2.commons.domain.valueObject.HttpContentType;
import com.WT2.commons.domain.valueObject.HttpMethod;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.firefuel.controlImpresion.dto.PeticionImpresion;
import com.firefuel.controlImpresion.dto.RespuestaImpresion;
import com.google.gson.JsonObject;
import java.util.Date;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 🔄 MIGRADO A SERVICIO PYTHON
 * Envía impresiones de ventas al microservicio Python de impresión
 * 
 * @version 2.0 - Migrado a Python
 */
public class EnviarImpresion {

    private static final Logger LOGGER = Logger.getLogger(EnviarImpresion.class.getName());

    @SuppressWarnings("unchecked")
    public void enviarImpresion(PeticionImpresion peticion, IHttpClientRepository httpClient) {

        try {
            // 🚀 MIGRACIÓN A SERVICIO PYTHON - INICIO
            NovusUtils.printLn("╔════════════════════════════════════════════════════════════╗");
            NovusUtils.printLn("║  🐍 SERVICIO PYTHON - IMPRESIÓN AUTOMÁTICA FE            ║");
            NovusUtils.printLn("╚════════════════════════════════════════════════════════════╝");
            NovusUtils.printLn("📋 Detalles de la petición:");
            NovusUtils.printLn("   - ID Movimiento: " + peticion.getIdentificadorMovimiento());
            NovusUtils.printLn("   - ID Equipo: " + peticion.getIdentificadorEquipo());
            NovusUtils.printLn("   - Placa: " + peticion.getPlaca());
            
            // Construir JSON para servicio Python
            JsonObject comando = new JsonObject();
            comando.addProperty("movement_id", peticion.getIdentificadorMovimiento());
            comando.addProperty("identificadorEquipo", peticion.getIdentificadorEquipo());
            
            if (peticion.getPlaca() != null && !peticion.getPlaca().trim().isEmpty()) {
                comando.addProperty("placa", peticion.getPlaca());
            }
            
            if (peticion.getOdometro() != null && !peticion.getOdometro().trim().isEmpty() 
                && !peticion.getOdometro().equals("0")) {
                comando.addProperty("odometro", peticion.getOdometro());
            }
            
            if (peticion.getNumero() != null && !peticion.getNumero().trim().isEmpty()) {
                comando.addProperty("numero", peticion.getNumero());
            }
            
            if (peticion.getOrden() != null && !peticion.getOrden().trim().isEmpty()) {
                comando.addProperty("orden", peticion.getOrden());
            }
            
            comando.addProperty("flow_type", "HISTORICO_IMPRIMIR");
            comando.addProperty("report_type", "FACTURA_ELECTRONICA");
            
            // URL del servicio Python
            String url = NovusConstante.SECURE_CENTRAL_POINT_PRINT_TICKET_SALES;
            NovusUtils.printLn("🌐 URL Servicio Python: " + url);
            NovusUtils.printLn("📤 Payload: " + comando.toString());
            
            // Configurar headers
            TreeMap<String, String> headers = new TreeMap<>();
            headers.put("Content-Type", "application/json; charset=UTF-8");
            headers.put("Accept", "application/json");
            
            // Enviar de forma asíncrona al servicio Python
            ClientWSAsync client = new ClientWSAsync(
                "CONTROL_IMPRESION_AUTOMATICO_PYTHON",
                url,
                NovusConstante.POST,
                comando,
                true,  // isDebug
                false, // isArray
                headers,
                10000  // timeout 10 segundos
            );
            
            client.start();
            JsonObject response = client.getResponse();
            
            if (response != null) {
                NovusUtils.printLn("✅ Respuesta del servicio Python recibida:");
                NovusUtils.printLn("   " + response.toString());
                
                if (response.has("success") && response.get("success").getAsBoolean()) {
                    NovusUtils.printLn("✅ IMPRESIÓN EXITOSA EN SERVICIO PYTHON");
                } else {
                    String mensaje = response.has("message") ? response.get("message").getAsString() : "Sin mensaje";
                    NovusUtils.printLn("⚠️  Servicio Python respondió con success=false: " + mensaje);
                }
            } else {
                NovusUtils.printLn("╔════════════════════════════════════════════════════════════╗");
                NovusUtils.printLn("║  ⚠️  SERVICIO DE IMPRESIÓN PYTHON NO RESPONDIÓ            ║");
                NovusUtils.printLn("╚════════════════════════════════════════════════════════════╝");
                NovusUtils.printLn("ERROR: Sin respuesta del microservicio Python");
                NovusUtils.printLn("URL: " + url);
                NovusUtils.printLn("Posibles causas:");
                NovusUtils.printLn("   - Microservicio Python no está levantado");
                NovusUtils.printLn("   - Timeout o error de red");
                NovusUtils.printLn("Solución: Verificar que el servicio Python esté corriendo en el puerto 8001");
            }
            
            NovusUtils.printLn("═══════════════════════════════════════════════════════════");
            // 🚀 MIGRACIÓN A SERVICIO PYTHON - FIN
            
        } catch (Exception ex) {
            NovusUtils.printLn("❌ ERROR en EnviarImpresion (Servicio Python): " + ex.getMessage());
            LOGGER.log(Level.SEVERE, "Error enviando impresión al servicio Python", ex);
            ex.printStackTrace();
        }

    }

}
