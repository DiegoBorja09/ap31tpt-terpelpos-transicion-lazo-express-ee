/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.facade;

import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.firefuel.Main;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author devitech
 */
public class ClienteFacade {

    public static SimpleDateFormat sdfISO = new SimpleDateFormat(NovusConstante.FORMAT_DATE_ISO);

    public static JsonObject fetchAditionalData(JsonObject request) {
        JsonObject response = null;
        TreeMap<String, String> header = new TreeMap<>();
        header.put("Content-type", "application/json");
        header.put("Authorization", "token");
        header.put("aplicacion", "rumbopos");
        header.put("identificadorDispositivo", "localhost");
        header.put("fecha", sdfISO.format(new Date()));
        ClientWSAsync clientWS = new ClientWSAsync("ENVIO DATOS ADICIONALES",
                NovusConstante.SECURE_CENTRAL_POINT_ADITIONAL_DATA,
                NovusConstante.POST,
                request,
                true, false, header);
        try {
            response = clientWS.esperaRespuesta();
            if (response == null) {
                return clientWS.getError();
            }
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
        return response;
    }

    public static JsonObject consultaValidacionVenta(JsonObject request) {
        JsonObject response;
        NovusUtils.printLn(Main.ANSI_GREEN + request + Main.ANSI_RESET);
        ClientWSAsync clientWS = new ClientWSAsync("AUTORIZACION VENTA",
                NovusConstante.getServer(NovusConstante.SECURE_CENTRAL_POINT_SALE_AUTHORIZATION_LAZO),
                NovusConstante.POST,
                request,
                true, false);
        try {
            response = clientWS.esperaRespuesta();
            if (response == null) {
                return clientWS.getError();
            }
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
            return null;
        }

        return response;
    }

    /**
     * Consulta validación de venta por placa para clientes propios
     * @param request JsonObject con los datos de la consulta
     * @return JsonObject con la respuesta del servidor
     * @author Diego Borja Padilla
     */
    public static JsonObject consultaValidacionVentaPorPlaca(JsonObject request) {
        JsonObject response;
        NovusUtils.printLn(Main.ANSI_GREEN + "[consultaValidacionVentaPorPlaca] Request: " + request + Main.ANSI_RESET);
        
        // Usar la constante con la URL completa del servidor
        String url = NovusConstante.SECURE_CENTRAL_POINT_SALE_AUTHORIZATION_LAZO;
        NovusUtils.printLn(Main.ANSI_CYAN + "[consultaValidacionVentaPorPlaca] URL: " + url + Main.ANSI_RESET);
        
        ClientWSAsync clientWS = new ClientWSAsync("CONSULTA AUTORIZACION POR PLACA",
                url,
                NovusConstante.POST,
                request,
                true, false);
        try {
            response = clientWS.esperaRespuesta();
            
            int httpStatus = clientWS.getStatus();
            if (response != null) {
                response.addProperty("httpStatus", httpStatus);
                NovusUtils.printLn(Main.ANSI_YELLOW + "[consultaValidacionVentaPorPlaca] HTTP Status: " + httpStatus + Main.ANSI_RESET);
            } else {
                // Si response es null, crear objeto con el status
                JsonObject errorResponse = clientWS.getError();
                if (errorResponse != null) {
                    errorResponse.addProperty("httpStatus", httpStatus);
                    return errorResponse;
                } else {
                    JsonObject nullResponse = new JsonObject();
                    nullResponse.addProperty("httpStatus", httpStatus);
                    return nullResponse;
                }
            }
        } catch (Exception e) {
            NovusUtils.printLn("[consultaValidacionVentaPorPlaca] Error: " + e.getMessage());
            Logger.getLogger(ClienteFacade.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return response;
    }

    public static JsonObject consultaValidacionVentaGLP(JsonObject request) {
        JsonObject response;
        ClientWSAsync clientWS = new ClientWSAsync("AUTORIZACION VENTA",
                NovusConstante.getServer(NovusConstante.SECURE_CENTRAL_POINT_SALE_AUTHORIZATION_LAZO_GLP),
                NovusConstante.POST,
                request,
                true, false);
        try {
            response = clientWS.esperaRespuesta();
            if (response == null) {
                return clientWS.getError();
            }
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
            return null;
        }
        return response;
    }
    public static JsonObject consultaVehiculoSicom(String placa) {
        JsonObject response;
        NovusUtils.printLn("🔍 [SICOM] Consultando placa: " + placa);
        // Usar directamente la constante sin getServer() para evitar concatenación incorrecta
        String url = NovusConstante.BACKEND_POINT_VALIDAR_PLACA_GLP + "/" + placa;
        NovusUtils.printLn("🔗 [SICOM] URL: " + url);
        
        // Configurar headers
        TreeMap<String, String> header = new TreeMap<>();
        header.put("Accept", "application/json");
        header.put("User-Agent", "TerpelPOS/1.0");
        
        // Crear cliente HTTP
        ClientWSAsync clientWS = new ClientWSAsync("VALIDAR PLACA SICOM",
                url,
                NovusConstante.GET,
                null, // Sin body para GET
                true, false, header);
        
        try {
            response = clientWS.esperaRespuesta();
            int statusCode = clientWS.getStatus();
            
            NovusUtils.printLn("📡 [SICOM] Status HTTP: " + statusCode);
            
            if (statusCode == 200) {
                NovusUtils.printLn("✅ [SICOM] Respuesta exitosa - Código 200");
                if (response != null) {
                    response.addProperty("httpStatus", statusCode);
                    NovusUtils.printLn("📋 [SICOM] Respuesta: " + response.toString());
                }
            } else {
                NovusUtils.printLn("❌ [SICOM] Respuesta fallida - Código " + statusCode);
                if (response == null) {
                    response = new JsonObject();
                    response.addProperty("httpStatus", statusCode);
                    response.addProperty("error", "Error HTTP " + statusCode);
                }
            }
            
            if (response == null) {
                response = clientWS.getError();
                if (response == null) {
                    response = new JsonObject();
                    response.addProperty("error", "Sin respuesta del servidor");
                }
            }
            
        } catch (Exception e) {
            NovusUtils.printLn("❌ [SICOM] Excepción: " + e.getMessage());
            Logger.getLogger(ClienteFacade.class.getName()).log(Level.SEVERE, null, e);
            
            response = new JsonObject();
            response.addProperty("error", "Error de conexión: " + e.getMessage());
            response.addProperty("httpStatus", 500);
        }
        
        return response;
    }
    // Consulta vehiculo en SICOM ya no es necesario
    // public static JsonObject consultaVehiculoSicom(String placa) {
    //     JsonObject response;
    //     TreeMap<String, String> header = new TreeMap<>();
    //     header.put("Content-type", "application/json");
    //     header.put("Host", "eds.sicom.gov.co");
    //     header.put("Origin", "https://www.sicom.gov.co");
    //     header.put("Referer", "https://www.sicom.gov.co/");
    //     header.put("User-Agent", "Mozilla/5.0");
    //     String url = getUrlSicom();
    //     ClientWSAsync clientWS = new ClientWSAsync("AUTORIZACION VENTA",
    //             url + placa,
    //             NovusConstante.GET, true);
    //     try {
    //         response = clientWS.esperaRespuesta();
    //         int statusCode = clientWS.getStatus();

    //         if(statusCode == 200){
    //             System.out.println("[SICOM] Respuesta Exitosa - Codigo 200");
    //             if(response != null){
    //                 response.addProperty("httpStatus", statusCode);
    //             }
    //         }
    //         else{
    //             System.out.println("[SICOM] Respuesta Fallida - Codigo " + statusCode);
    //             if(response == null){
    //                 response.addProperty("httpStatus", statusCode);
    //             }
    //         }
    //         if (response == null) {
    //             return clientWS.getError();
    //         }
    //     } catch (Exception e) {
    //         NovusUtils.printLn(e.getMessage());
    //         Logger.getLogger(ClienteFacade.class.getName()).log(Level.SEVERE, null, e);
    //         return null;
    //     }
    //     return response;
    // }

    public static String getUrlSicom() {
        String url = SingletonMedioPago.ConetextDependecy
                .getValidateUrlGlp().execute(null);
        if (url.isEmpty()) {
            url = NovusConstante.SECURE_URL_SICOM_GLP;
        }
        return url;
    }

}
