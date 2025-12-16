package com.facade;

import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.controllers.WSException;
import com.firefuel.Main;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.neo.app.bean.Manguera;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GopassFacade {

    public static JsonObject consultarPlacasGopass(Manguera manguera, int timeoutGopass) {
        JsonObject json = new JsonObject();
        JsonArray infoPlacas = null;

        String url = "http://localhost:7011/api/placaEnCurso";

        TreeMap<String, String> header = new TreeMap<>();
        header.put("Content-type", "application/json");

        JsonObject data = new JsonObject();
        data.addProperty("isla", Main.credencial.getIsla());
        data.addProperty("cara", manguera.getCara());
        data.addProperty("surtidor", Main.credencial.getSurtidor());
        NovusUtils.printLn("______________________________________");
        NovusUtils.printLn("CONSULTANDO A PLACAS GOPASS STATUS PUMP");
        NovusUtils.printLn(url);
        NovusUtils.printLn("Request : " + data);
        boolean isArray = false;
        boolean isDebug = true;
        JsonObject response;
        JsonObject error = new JsonObject();

        ClientWSAsync client = new ClientWSAsync(
                    "BUSCAR PLACAS GOPASS",
                    url,
                    NovusConstante.POST,
                    data,
                    isDebug,
                    isArray,
                    header,
                    timeoutGopass);
        
        try {            
            client.execute();
            error = client.getError();
            response = client.getResponse();
            
            if (response != null) {
                NovusUtils.printLn("Response Placas Gopass: " + response);
                infoPlacas = response.get("datos").getAsJsonArray();
                json.add("datos", infoPlacas);
            } else if (error != null) {
                String mensajeError = error.get("mensajeError").getAsString();
                if (mensajeError == null || mensajeError.isEmpty()) {
                    mensajeError = "Fallo de red - Error de conexión - intente con otro medio de pago";
                }
                error.addProperty("mensajeError", mensajeError);
                json.add("error", error);
            } else {
                System.out.println("3");
                if (error == null) {
                    error = new JsonObject();
                }
                error.addProperty("mensajeError", "Fallo de red - Error de conexión - intente con otro medio de pago");
                json.add("error", error);
            }       

            return json;
        } catch (Exception e) {
            if (error == null) {
                error = new JsonObject();
            }
            error.addProperty("mensajeError", "Fallo de red - Error de conexión - intente con otro medio de pago");
            NovusUtils.printLn("consultarPlacasGopass " + e.getMessage());
        } catch (WSException ex) {
            error.addProperty("mensajeError", "Fallo de red - Error de conexión - intente con otro medio de pago");
            Logger.getLogger(GopassFacade.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        json.add("error", error);
        return json;
    }

}
