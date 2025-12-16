/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.facade.fidelizacion;

import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.firefuel.Main;
import com.google.gson.JsonObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

/**
 *
 * @author Devitech
 */
public class EnviarAcumulacion {

    SimpleDateFormat sdfISO = new SimpleDateFormat(NovusConstante.FORMAT_DATE_ISO);
    private static final String STATUS = "status";
    private static final String MENSAJE = "mensaje";
    private static final String CODIGO_RESPUESTA = "codigoRespuesta";
    private static final String HEADERS = "headers";
    private static final String ERROR = "error";

    public JsonObject acmular(JsonObject resquest) {
        TreeMap<String, String> header = new TreeMap<>();
        header.put("Authorization", "Basic cGFzc3BvcnR4OlQ0MUFYUWtYSjh6");
        header.put("Content-Type", "application/json");
        header.put("Accept", "*/*");
        header.put("dispositivo", "proyectos");
        header.put("fecha", sdfISO.format(new Date()) + "-05");
        header.put("aplicacion", "lazoexpress");
        String funcion = "ENVIAR ACUMULACION";
        String url = NovusConstante.SECURE_CENTRAL_POINT_ACUMULACION_CLIENTE;
        NovusUtils.printLn(Main.ANSI_GREEN+"enviando venta a fidellizar"+Main.ANSI_RESET);
        if (NovusConstante.HAY_INTERNET) {
            ClientWSAsync client = new ClientWSAsync(funcion, url, NovusConstante.POST, resquest, true, false, header);
            JsonObject respuesta = client.esperaRespuesta();
            JsonObject response = respuesta != null ? respuesta : client.getError();
            return validarRespuesta(response, client.getStatus(), resquest);
        } else {
            //NO HAY INTERNET SI COLOCA EN CONTINGENCIA
            // REMOVER ESTA LOGICA
            resquest.add(HEADERS, header());
            NovusUtils.printLn("no hay internet para hacer la fidelizacion");
            return respuesta("no hay internet para hacer la fidelizacion", 200, resquest, true);
        }
    }

    public JsonObject header() {
        JsonObject head = new JsonObject();
        head.addProperty("Authorization", "Basic cGFzc3BvcnR4OlQ0MUFYUWtYSjh6");
        head.addProperty("Content-Type", "application/json");
        head.addProperty("Accept", "*/*");
        head.addProperty("dispositivo", "proyectos");
        head.addProperty("fecha", sdfISO.format(new Date()) + "-05");
        head.addProperty("aplicacion", "lazoexpress");
        return head;
    }

    public JsonObject validarRespuesta(JsonObject response, int estatus, JsonObject ventaFidelizacion) {
        if (response != null) {
            if (response.has(CODIGO_RESPUESTA) && response.get(CODIGO_RESPUESTA).getAsString().equals("20000")) {
                //CAMINO FELIZ EL SERVICIO RESPONDE Y FIDELIZA
                JsonObject datosFidelizacion = new JsonObject();
                datosFidelizacion.addProperty(STATUS, 200);
                datosFidelizacion.addProperty(MENSAJE, "TRANSACCIÓN EXITOSA");
                return guardarTransmiscion(response, ventaFidelizacion);
            } else {
                //EL SERVICIO NO RESPONDE Y SE VA CONTINGENCIA
                String mensaje = response.get(ERROR) != null ? response.get(ERROR).getAsString() : response.get("mensajeRespuesta").getAsString();
                ventaFidelizacion.add(HEADERS, header());
                return respuesta(mensaje, estatus, ventaFidelizacion, true);
            }
        } else {
            return respuesta("No hay conexión con el Motor de Fidelización", 500, ventaFidelizacion, true);
        }
    }

    private JsonObject guardarTransmiscion(JsonObject response, JsonObject ventaFidelizacion) {
        int status = response.get(STATUS) != null ? response.get(STATUS).getAsInt() : response.get(CODIGO_RESPUESTA).getAsInt();
        if (status == 400) {
            String mensaje = response.get(ERROR) != null ? response.get(ERROR).getAsString() : response.get("mensajeRespuesta").getAsString();
            ventaFidelizacion.add(HEADERS, header());
            return respuesta(mensaje, 400, ventaFidelizacion, true);
        } else {
            JsonObject datosFidelizacion = new JsonObject();
            datosFidelizacion.addProperty(STATUS, 200);
            datosFidelizacion.addProperty(MENSAJE, "TRANSACCIÓN EXITOSA");
            return datosFidelizacion;
        }
    }

    public JsonObject respuesta(String mensaje, int status, JsonObject fidelizacion, boolean contingencia) {
        JsonObject response = new JsonObject();
        response.addProperty(STATUS, status);
        response.addProperty(MENSAJE, mensaje);
        JsonObject body = new JsonObject();
        body.add("body", fidelizacion);
        response.add("fidelizacion", body);
        response.addProperty("fidelizacionContingencia", contingencia);
        NovusUtils.printLn(response.toString());
        return response;
    }
}
