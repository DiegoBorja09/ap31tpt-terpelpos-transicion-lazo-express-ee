/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel;

import com.bean.MediosPagosBean;
import com.bean.ReciboExtended;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.MovimientosDao;
import com.facade.ConfigurationFacade;
import com.facade.FidelizacionFacade;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;

/**
 *
 * @author USUARIO
 */
public class ValidacionBonosViveTerpel {

    MovimientosDao mdao = new MovimientosDao();

    public boolean ActualizarMovViveTerpel(long idMovimiento, JsonArray bonos) {
        boolean isValido = false;
        Long idVenta = idMovimiento;
        try {
            if (mdao.ActualizarAtributosViveTerpel(idVenta, bonos)) {
                NovusUtils.printLn("ATRIBUTOS VIVE TERPEL ACTUALIZADOS");
                isValido = true;
            } else {
                NovusUtils.printLn("NO SE PUDO ACTUALIZAR ATRIBUTOS");
                isValido = false;
            }
        } catch (DAOException e) {
            NovusUtils.printLn(e.getMessage());
        }
        return isValido;
    }

    public JsonObject ReclamacionBonoViveTerpel(ReciboExtended recibo, ArrayList<MediosPagosBean> mediosPagoVenta, long idMovimiento, JsonArray bonos) {
        ReciboExtended saleFacture = recibo;
        String idPuntoVenta = ConfigurationFacade.fetchSalePointIdentificator();
        ArrayList<MediosPagosBean> mediosP = mediosPagoVenta;
        JsonObject response = FidelizacionFacade.fetchClientReclamacion(saleFacture, "", "", idPuntoVenta, mediosP);
        return response;
    }

    public JsonObject procesamientRespuestaReclamacion(JsonObject response, long idMovimiento, JsonArray bonos) {
        JsonObject respuesta = new JsonObject();
        final int CLIENT_IDENTIFIED_CODE = 20000;
        boolean continuar = false;
        int statusCode = 0;
        if (response != null && !response.get("statusCode").isJsonNull()) {
            statusCode = response.get("statusCode").getAsInt();
        }
        NovusUtils.printLn("STATUS CODE: " + statusCode);
        String mensaje;
        switch (statusCode) {
            case 200:
                if (response != null) {
                    if (response.get("codigoRespuesta") != null && !response.get("codigoRespuesta").isJsonNull()) {
                        String responseMessage = response.get("mensajeRespuesta") != null
                                ? response.get("mensajeRespuesta").getAsString().toUpperCase()
                                : "ERROR EN EL PROCESO";
                        int responseCode = response.get("codigoRespuesta").getAsInt();

                        if (responseCode == CLIENT_IDENTIFIED_CODE) {
                            continuar = ActualizarMovViveTerpel(idMovimiento, bonos);
                            if (continuar == true) {
                                MedioPagosConfirmarViewController.bonosValidados = true;
                                mensaje = "BONOS VALIDADOS EXITOSAMENTE";
                                respuesta = objetoRespuesta(mensaje, "/com/firefuel/resources/btOk.png", true, true, true);
                            } else {
                                mensaje = "NO SE PUEDE EDITAR MEDIO PAGO";
                                respuesta = objetoRespuesta(mensaje, "/com/firefuel/resources/btBad.png", true, true, false);
                            }
                        } else {
                            mensaje = responseMessage;
                            respuesta = objetoRespuesta(mensaje, "/com/firefuel/resources/btBad.png", true, true, false);
                        }
                    } else {
                        mensaje = response.get("mensajeError").getAsString();
                        respuesta = objetoRespuesta(mensaje, "/com/firefuel/resources/btBad.png", true, true, false);
                    }
                } else {
                    mensaje = "La transacción no se puede realizar en este momento";
                    respuesta = objetoRespuesta(mensaje, "/com/firefuel/resources/btBad.png", true, true, false);
                }
                break;
            default:
                mensaje = "La transacción no se puede realizar en este momento";
                respuesta = objetoRespuesta(mensaje, "/com/firefuel/resources/btBad.png", true, true, false);
        }
        return respuesta;
    }

    private JsonObject objetoRespuesta(String mensaje, String icono, boolean cerrar, boolean autoclose, boolean aprobado) {
        JsonObject data = new JsonObject();
        data.addProperty("mensaje", mensaje);
        data.addProperty("icono", icono);
        data.addProperty("cerrar", cerrar);
        data.addProperty("autoclose", autoclose);
        data.addProperty("aprobado", aprobado);
        return data;
    }

    public void setTimeout(int delay, Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep((long) delay * 1000);
                if (runnable != null) {
                    runnable.run();
                }
            } catch (InterruptedException e) {
                NovusUtils.printLn("Interrupted.setTimeout" + e.getLocalizedMessage());
                Thread.currentThread().interrupt();
            }
        }).start();
    }

}
