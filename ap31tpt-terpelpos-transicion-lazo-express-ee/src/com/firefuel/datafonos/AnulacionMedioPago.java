/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.datafonos;

import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.dao.DatafonosDao;
import com.google.gson.JsonObject;

/**
 *
 * @author Devitech
 */
public class AnulacionMedioPago {

    DatafonosDao datfonoDao = new DatafonosDao();
    private final String MENSAJE = "mensaje";
    private final String STATUS = "status";

    public JsonObject anularPago(long idMovimietno, long idMedioPago, String voucher) {
        JsonObject data = respuesta(0, "");
        String url = NovusConstante.SECURE_CENTRAL_POINT_ENVIAR_ANULACION_DATAFONO;
        String funcion = "ANULACION DE MEDIO DE PAGO DATAFONO";
        JsonObject objAnulacion = informacionDatafono(idMovimietno, idMedioPago, voucher);
        if (objAnulacion != null){
            ClientWSAsync client = new ClientWSAsync(funcion, url, NovusConstante.POST, objAnulacion, Boolean.TRUE);
            JsonObject respuesta = client.esperaRespuesta();
            if (respuesta != null){
                data = respuesta(client.getStatus(), "INICIANDO PROCESO DE ANULACIÓN, ACÉRQUESE AL DATAFONO");
            }else{
                data = respuesta(client.getStatus(), "ERROR AL REALIZAR EL PROCESO DE ANULACION");
            }
        }else{
           data = respuesta(500, "ERROR AL REALIZAR EL PROCESO DE ANULACION");
        }
        return data;
    }

    private JsonObject informacionDatafono(long idMovimiento, long idMedioPagos, String voucher) {
        JsonObject objDatafono = datfonoDao.buscarInfoDatafono(idMovimiento, idMedioPagos, voucher);
        return objDatafono;
    }

    private JsonObject respuesta(int status, String mensaje) {
        JsonObject respuesta = new JsonObject();
        respuesta.addProperty(this.MENSAJE, mensaje);
        respuesta.addProperty(this.STATUS, status);
        return respuesta;
    }
}
