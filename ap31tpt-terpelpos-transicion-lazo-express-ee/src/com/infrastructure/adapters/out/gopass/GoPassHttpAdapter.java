package com.infrastructure.adapters.out.gopass;

import com.application.ports.out.gopass.GoPassHttpPort;
import com.WT2.goPass.domain.entity.beans.PlacaGopass;
import com.bean.GopassResponse;
import com.bean.VentaGo;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.controllers.WSException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ✅ ARQUITECTURA HEXAGONAL - Adaptador de Salida
 * Adaptador HTTP para comunicación con la API de GoPass
 * Implementa el puerto GoPassHttpPort
 * Contiene toda la lógica de infraestructura (HTTP, JSON, etc.)
 */
public class GoPassHttpAdapter implements GoPassHttpPort {

    private static final Logger LOGGER = Logger.getLogger(GoPassHttpAdapter.class.getName());

    @Override
    public ArrayList<PlacaGopass> consultarPlacas(Long ventaId, int timeoutMs) throws GoPassHttpException {
        LOGGER.info("✅ [ADAPTER] Consultando placas vía HTTP para venta: " + ventaId);

        try {
            // Construir URL
            String url = NovusConstante.SECURE_CENTRAL_POINT_BUSCAR_PLACAS_GOPASS + ventaId;
            
            // Preparar headers
            TreeMap<String, String> header = new TreeMap<>();
            header.put("Content-type", "application/json");

            LOGGER.info("URL: " + url);
            LOGGER.info("Timeout: " + timeoutMs + "ms");

            // Llamada HTTP
            ClientWSAsync client = new ClientWSAsync(
                    "BUSCAR PLACAS GOPASS",
                    url,
                    NovusConstante.GET,
                    new JsonObject(),
                    true, // isDebug
                    false, // isArray
                    header,
                    timeoutMs
            );

            JsonObject response = client.execute();
            JsonObject error = client.getError();

            // Procesar respuesta
            if (response != null) {
                LOGGER.info("✅ Respuesta exitosa de API GoPass");
                JsonArray placasArray = response.get("datos").getAsJsonArray();
                return mapearPlacas(placasArray);
                
            } else if (error != null) {
                String mensajeError = extraerMensajeError(error);
                LOGGER.warning("❌ Error en respuesta: " + mensajeError);
                throw new GoPassHttpException(mensajeError);
                
            } else {
                throw new GoPassHttpException("Fallo de red - Error de conexión - intente con otro medio de pago");
            }

        } catch (WSException ex) {
            LOGGER.log(Level.SEVERE, "❌ Error de servicio web", ex);
            throw new GoPassHttpException("Error de comunicación con GoPass", ex);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error inesperado en adaptador HTTP", e);
            throw new GoPassHttpException("Error inesperado al consultar placas", e);
        }
    }

    @Override
    public GopassResponse procesarPago(VentaGo venta, PlacaGopass placa, long timeoutMs) throws GoPassHttpException {
        // TODO: Implementar cuando se refactorice ProcesarPagoGoPassUseCase
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    @Override
    public JsonObject consultarEstadoPago(String refCobro, String establecimiento, long idTransaccion) throws GoPassHttpException {
        LOGGER.info("✅ [ADAPTER] Consultando estado de pago vía HTTP");

        try {
            String url = NovusConstante.SECURE_CENTRAL_POINT_CONSULTAR_ESTADO.trim();
            
            // Construir request
            JsonObject requestJson = new JsonObject();
            requestJson.addProperty("refCobro", refCobro);
            requestJson.addProperty("establecimiento", establecimiento);
            requestJson.addProperty("id", idTransaccion);

            // Configurar headers
            TreeMap<String, String> header = new TreeMap<>();
            header.put("Content-type", "application/json");

            LOGGER.info("URL: " + url);

            // Realizar llamada
            ClientWSAsync client = new ClientWSAsync(
                "CONSULTAR ESTADO PAGO GOPASS",
                url,
                NovusConstante.POST,
                requestJson,
                true, // isDebug
                false, // isArray
                header
            );

            JsonObject response = client.esperaRespuesta();
            
            if (response == null) {
                response = client.getError();
                
                if (response != null) {
                    String mensajeError = extraerMensajeError(response);
                    throw new GoPassHttpException(mensajeError);
                } else {
                    throw new GoPassHttpException("Error de comunicación con el servicio");
                }
            }

            return response;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error consultando estado", e);
            throw new GoPassHttpException("Error al consultar el estado", e);
        }
    }

    /**
     * Extrae el mensaje de error del JsonObject
     */
    private String extraerMensajeError(JsonObject error) {
        if (error.has("mensajeError") && !error.get("mensajeError").isJsonNull()) {
            String mensaje = error.get("mensajeError").getAsString();
            if (mensaje != null && !mensaje.isEmpty()) {
                return mensaje;
            }
        }
        return "Fallo de red - Error de conexión - intente con otro medio de pago";
    }

    /**
     * Mapea el JsonArray a una lista de PlacaGopass
     */
    private ArrayList<PlacaGopass> mapearPlacas(JsonArray placasArray) {
        ArrayList<PlacaGopass> placas = new ArrayList<>();
        
        for (JsonElement element : placasArray) {
            try {
                JsonObject json = element.getAsJsonObject();
                PlacaGopass placa = new PlacaGopass();
                
                placa.setPlaca(obtenerString(json, "placa"));
                placa.setTagGopass(obtenerString(json, "tagGopass"));
                placa.setNombreUsuario(obtenerString(json, "nombreUsuario"));
                placa.setIsla(obtenerString(json, "isla"));
                placa.setFechahora(obtenerString(json, "fechahora"));
                
                placas.add(placa);
                
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error mapeando placa", e);
            }
        }
        
        return placas;
    }

    /**
     * Obtiene un String de un JsonObject de forma segura
     */
    private String obtenerString(JsonObject json, String key) {
        if (json.has(key) && !json.get(key).isJsonNull()) {
            return json.get(key).getAsString();
        }
        return "";
    }
}

