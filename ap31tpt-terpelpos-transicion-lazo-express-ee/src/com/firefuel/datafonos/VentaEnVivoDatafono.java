/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.firefuel.datafonos;

import com.bean.Notificador;
import com.bean.ReciboExtended;
import com.dao.MovimientosDao;
import com.dao.SurtidorDao;
import com.google.gson.JsonObject;

/**
 *
 * @author Devitech
 */
public class VentaEnVivoDatafono {
    MovimientosDao  mdao = new MovimientosDao();

    public void setMensaje(String mensaje, String icono, Notificador notificadorView) {
        JsonObject errorJson = new JsonObject();
        errorJson.addProperty("mensajeError", mensaje);
        if (icono.length() == 0) {
            errorJson.addProperty("icono", "/com/firefuel/resources/btBad.png");
            errorJson.addProperty("error", true);
        } else {
            errorJson.addProperty("icono", icono);
            errorJson.addProperty("error", false);
        }
        errorJson.addProperty("habilitar", true);
        errorJson.addProperty("autoclose", true);
        notificadorView.send(errorJson);
    }

    public void datosDatafonosEnCurso(JsonObject placa, JsonObject objManguera, JsonObject datafono, boolean datosPacla, Notificador notificadorView) {
        SurtidorDao sdao = new SurtidorDao();
        ReciboExtended recibo = new ReciboExtended();
        recibo.setSurtidor(objManguera.get("surtidor").getAsInt() + "");
        recibo.setManguera(objManguera.get("id").getAsInt() + "");
        recibo.setCara(objManguera.get("cara").getAsInt() + "");
        if (datosPacla) {
            mdao.VentaEnCursoDatafono(datafono.get("codigoAutorizacion").getAsLong(),"A","DATAFONO");
            sdao.updateVentasEncurso(recibo, placa, 2);
            sdao.updateVentasEncurso(recibo, datafono, 5);
            if (notificadorView != null){
                setMensaje("<html><center>SE ACTUALIZARON LOS DATOS DE FACTURA EN MANGUERA " + objManguera.get("id").getAsInt() + "</center></html>",
                    "/com/firefuel/resources/btOk.png",
                    notificadorView);
            }            
        } else {
            mdao.VentaEnCursoDatafono(datafono.get("codigoAutorizacion").getAsLong(),"A","DATAFONO");
            sdao.updateVentasEncurso(recibo, datafono, 5);
            if (notificadorView != null) {
                setMensaje("<html><center>SE ACTUALIZARON LOS DATOS DE FACTURA EN MANGUERA " + objManguera.get("id").getAsInt() + "</center></html>",
                        "/com/firefuel/resources/btOk.png",
                        notificadorView);
            }
        }

    }
}
