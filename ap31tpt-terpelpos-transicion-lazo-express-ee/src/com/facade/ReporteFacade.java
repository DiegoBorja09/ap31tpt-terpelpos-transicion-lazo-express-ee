/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.facade;

import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.google.gson.JsonObject;
import java.util.TreeMap;

/**
 *
 * @author devitech
 */
public class ReporteFacade {

    public static JsonObject imprimirReporteCierreDiarioConsolidado(JsonObject request) {
        JsonObject response = null;
        TreeMap<String, String> header = new TreeMap<>();
        header.put("content-type", "application/json");
        header.put("authorization", "1");
        header.put("uuid", "519a5c11-ae7f-4470-9f67-e212a62ba704");
        header.put("fecha", "2021-07-28T22:57:00-05");
        header.put("aplicacion", "lazoexpress");
        header.put("original", "http://localhost:8010");

        ClientWSAsync clientWS = new ClientWSAsync(
                "IMPRESION CIERRE DIARIO CONSOLIDADO",
                NovusConstante.SECURE_CENTRAL_POINT_PRINT_TICKET_REPORTS,
                NovusConstante.POST,
                request,
                true,
                false,
                header
        );

        try {
            System.out.println("[DEBUG] Enviando payload a impresión de cierre diario:");
            System.out.println(request.toString());

            response = clientWS.esperaRespuesta();

            if (response == null) {
                System.out.println("[ERROR] La respuesta del backend fue null. Obteniendo error desde clientWS...");
                return clientWS.getError();
            }

            System.out.println("[DEBUG] Respuesta recibida:");
            System.out.println(response.toString());

            // Validación lógica adicional (opcional pero recomendada)
            if (response.has("success") && !response.get("success").getAsBoolean()) {
                System.out.println("[WARN] El backend respondió con success = false:");
                return response;
            }

            if (response.has("error")) {
                System.out.println("[ERROR] El backend incluyó un campo 'error': " + response.get("error"));
            }

        } catch (Exception e) {
            System.out.println("[ERROR] Excepción al ejecutar impresión:");
            e.printStackTrace();
        }

        return response;
    }

}
