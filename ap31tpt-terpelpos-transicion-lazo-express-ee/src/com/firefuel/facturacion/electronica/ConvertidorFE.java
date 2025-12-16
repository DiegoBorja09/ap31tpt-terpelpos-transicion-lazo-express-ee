/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.facturacion.electronica;

import com.bean.MovimientosBean;
import com.bean.Notificador;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.MovimientosDao;
import com.firefuel.Main;
import com.google.gson.JsonObject;
import com.application.useCases.wacherparametros.FindByParameterUseCase;
import com.application.commons.CtWacherParametrosEnum;

/**
 *
 * @author Devitech
 */
public class ConvertidorFE {

    ConfiguracionFE config = new ConfiguracionFE();
    JsonObject datosImpresion = new JsonObject();
    MovimientosDao mdao = new MovimientosDao();
    FindByParameterUseCase findByParameterUseCase = new FindByParameterUseCase(CtWacherParametrosEnum.CODIGO.getColumnName(), "REMISION");

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
        errorJson.addProperty("loader", false);
        errorJson.addProperty("habilitar", true);
        errorJson.addProperty("autoclose", true);
        notificadorView.send(errorJson);
    }

    public void setLoader(String mensaje, Notificador notificadorView) {
        JsonObject errorJson = new JsonObject();
        errorJson.addProperty("mensajeError", mensaje);
        errorJson.addProperty("icono", "/com/firefuel/resources/loader_fac.gif");
        errorJson.addProperty("loader", true);
        errorJson.addProperty("habilitar", false);
        errorJson.addProperty("autoclose", false);
        errorJson.addProperty("error", false);
        notificadorView.send(errorJson);
    }
    public void ConvertidorPOSaFE(Notificador notificadorView, MovimientosBean movimiento, JsonObject clientJson, JsonObject notaP) {
        NovusUtils.printLn("ConvertidorPOSaFE");
        if (!notaP.get("isElectronica").getAsBoolean()) {
            String funcion = "FAC POS A FACT ELECTRONICA.";
            String url = NovusConstante.SECURE_CENTRAL_POINT_FAC_ELECTRONICA;
            String method = "POST";
            if (findByParameterUseCase.execute()) {
                url = NovusConstante.SECURE_CENTRAL_POINT_ANULACIONES;
                method = "PUT";
            }

            JsonObject json = new JsonObject();
            JsonObject jsonTransaccion = new JsonObject();
            jsonTransaccion.addProperty("identificadorMovimiento", movimiento.getId());
            jsonTransaccion.addProperty("identificadorPromotor", Main.persona.getId());
            jsonTransaccion.addProperty("identificadorNegocio", Main.credencial.getEmpresa().getNegocioId());
            if (findByParameterUseCase.execute()) {
                json.addProperty("identificadorTipoDocumento", 0);
                json.addProperty("tipoVenta", 100);
                json.addProperty("identificadorMovimiento", movimiento.getId());
                json.addProperty("identificadorPromotor", Main.persona.getId());
                json.addProperty("identificadorNegocio", Main.credencial.getEmpresa().getNegocioId());
                json.addProperty("devolucionInventario",false);
                json.addProperty("observaciones", "");
            } else {
                json.add("transaccion", jsonTransaccion);
            }
            json.add("cliente", clientJson);

            setLoader("POR FAVOR , ESPERE UN MOMENTO...", notificadorView);
            ClientWSAsync client = new ClientWSAsync(funcion, url, method, json, true);
            Thread resp = new Thread() {
                @Override
                public void run() {
                    JsonObject respuesta = client.esperaRespuesta();
                    if (respuesta != null) {
                        String resp = config.mensajesFE(200, "conversion-fe");
                        setMensaje(resp, "/com/firefuel/resources/btOk.png", notificadorView);
                    } else {
                        String resp = config.mensajesFE(400, "conversion-fe");
                        setMensaje(resp, "", notificadorView);
                    }

                }
            
            };
            resp.start();
        }
    }

    // public void ConvertidorPOSaFE(Notificador notificadorView, MovimientosBean movimiento, JsonObject clientJson, JsonObject notaP) {
    //     NovusUtils.printLn("ConvertidorPOSaFE");
    //     if (!notaP.get("isElectronica").getAsBoolean()) {
    //         String funcion = "FAC POS A FACT ELECTRONICA.";
    //         String url = NovusConstante.SECURE_CENTRAL_POINT_FAC_ELECTRONICA;
    //         String method = "POST";
    //         if (mdao.remisionActiva()) {
    //             url = NovusConstante.SECURE_CENTRAL_POINT_ANULACIONES;
    //             method = "PUT";
    //         }

    //         JsonObject json = new JsonObject();
    //         JsonObject jsonTransaccion = new JsonObject();
    //         jsonTransaccion.addProperty("identificadorMovimiento", movimiento.getId());
    //         jsonTransaccion.addProperty("identificadorPromotor", Main.persona.getId());
    //         jsonTransaccion.addProperty("identificadorNegocio", Main.credencial.getEmpresa().getNegocioId());
    //         if (mdao.remisionActiva()) {
    //             json.addProperty("identificadorTipoDocumento", 0);
    //             json.addProperty("tipoVenta", 100);
    //             json.addProperty("identificadorMovimiento", movimiento.getId());
    //             json.addProperty("identificadorPromotor", Main.persona.getId());
    //             json.addProperty("identificadorNegocio", Main.credencial.getEmpresa().getNegocioId());
    //             json.addProperty("devolucionInventario",false);
    //             json.addProperty("observaciones", "");
    //         } else {
    //             json.add("transaccion", jsonTransaccion);
    //         }
    //         json.add("cliente", clientJson);

    //         setLoader("POR FAVOR , ESPERE UN MOMENTO...", notificadorView);
    //         ClientWSAsync client = new ClientWSAsync(funcion, url, method, json, true);
    //         Thread resp = new Thread() {
    //             @Override
    //             public void run() {
    //                 JsonObject respuesta = client.esperaRespuesta();
    //                 if (respuesta != null) {
    //                     String resp = config.mensajesFE(200, "conversion-fe");
    //                     setMensaje(resp, "/com/firefuel/resources/btOk.png", notificadorView);
    //                 } else {
    //                     String resp = config.mensajesFE(400, "conversion-fe");
    //                     setMensaje(resp, "", notificadorView);
    //                 }

    //             }
            
    //         };
    //         resp.start();
    //     }
    // }

}
