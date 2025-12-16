package com.firefuel.facturacion.electronica;

import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Clase para cachear la respuesta de la consulta de Cliente FE,
 * evitando llamadas repetidas al mismo servicio.
 */
public class ClienteFECache {

    // Mapa de caché. Clave: "documento_tipoDoc". Valor: Respuesta JSON ya consultada.
    private static final Map<String, JsonObject> CACHE_CLIENTES = new ConcurrentHashMap<>();

    // Constructor privado para evitar instanciación (utilidad estática)
    private ClienteFECache() {
        // No se debe instanciar esta clase.
    }
    public static int getCacheSize() {
        return CACHE_CLIENTES.size();
    }
    /**
     * Obtiene los datos de cliente FE desde la caché; si no existe, llama al servicio.
     *
     * @param numeroDocumento  número de documento del cliente
     * @param tipoDocumento    tipo de documento
     * @param debug            indica si se imprimen logs de debug
     * @return JsonObject con la información del cliente o con campos de error
     */
    public static JsonObject obtenerDatosClienteFE(String numeroDocumento, long tipoDocumento, boolean debug) {
        // Generamos una llave única para cachear
        String key = numeroDocumento + "_" + tipoDocumento;
        // Verificamos si ya está en caché
        JsonObject cached = CACHE_CLIENTES.get(key);
        if (cached != null) {
            return cached;
        }

        // No está en caché, sincronizamos para evitar que varios hilos consulten simultáneamente
        synchronized (ClienteFECache.class) {
            // Doble verificación: si otro hilo ya lo agregó mientras tanto
            cached = CACHE_CLIENTES.get(key);
            if (cached != null) {
                return cached;
            }

            // Llamada real al servicio, utilizando el método privado.
            JsonObject respuesta = llamarServicioCliente(numeroDocumento, tipoDocumento, debug);
            // Guardamos en caché (incluso si la respuesta indica error) para evitar múltiples consultas seguidas.
            CACHE_CLIENTES.put(key, respuesta);
            return respuesta;
        }
    }

    /**
     * Limpia la caché eliminando todas las entradas.
     * Este método se debe llamar cada vez que se envían las ventas para forzar una nueva consulta.
     */
    public static void clearCache() {
        CACHE_CLIENTES.clear();
    }

    /**
     * Encapsula la invocación real al servicio FE.
     *
     * @param numeroDocumento  número de documento del cliente
     * @param tipoDocumento    tipo de documento
     * @param debug            bandera para activar logs de depuración
     * @return JsonObject con la respuesta del servicio
     */
    private static JsonObject llamarServicioCliente(String numeroDocumento, long tipoDocumento, boolean debug) {
        String funcion = "CONSULTA DE CLIENTE FACTURACION FE - CACHE";
        // Puedes usar el endpoint configurado en NovusConstante o una URL de pruebas.
        String url = NovusConstante.getServer(NovusConstante.SOURCE_END_POINT_CONSULTA_CLIENTE);
        //xString url = "http://localhost:7012/proxi.terpel/consultarCliente";
        String method = "POST";

        // Construcción del JSON de solicitud
        JsonObject json = new JsonObject();
        json.addProperty("documentoCliente", numeroDocumento);
        json.addProperty("tipoDocumentoCliente", tipoDocumento);

        // Llamada al servicio utilizando ClientWSAsync
        ClientWSAsync client = new ClientWSAsync(funcion, url, method, json, debug);
        JsonObject respuesta = client.esperaRespuesta();

        // Manejo de la respuesta
        if (respuesta != null) {
            // Se procesan los caracteres especiales, si es necesario
            respuesta = NovusUtils.remplazarCaracteresEspecialesClientesFe(respuesta);
            respuesta.addProperty("error", false);
            respuesta.addProperty("errorServicio", false);
        } else {
            // En caso de error o respuesta nula, se crea un objeto de error.
            respuesta = new JsonObject();
            if (client.getError() != null) {
                respuesta.addProperty("error", true);
                respuesta.addProperty("errorServicio", true);
            } else {
                respuesta.addProperty("errorServicio", true);
            }
        }

        return respuesta;
    }
}
