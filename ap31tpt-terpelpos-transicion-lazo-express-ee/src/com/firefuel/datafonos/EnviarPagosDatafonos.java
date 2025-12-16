/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.datafonos;

import com.application.useCases.movimientos.ObtenerDatosVentaPendienteDatafonoUseCase;
import com.bean.MovimientosBean;
import com.bean.MovimientosDetallesBean;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DatafonosDao;
import com.dao.MovimientosDao;
import com.firefuel.Main;
import com.firefuel.VentasHistorialView;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author Devitech
 */
public class EnviarPagosDatafonos {

    MovimientosDao mdao = new MovimientosDao();
    ArrayList<Integer> idTranssacciones = new ArrayList<>();
    String transaccionRecibidaIcono = "/com/firefuel/resources/transaccionRecibida.gif";
    String errorDatafonoIcono = "/com/firefuel/resources/error enviar pago.png";
    private final String ESTADO = "estado";
    private final String MENSAJE = "mensaje";
    private final String INFORMACION_DATAFONO = "informacionDatafono";
    private final String TRANSACCIONES = "transacciones";
    private final String ICONO = "icocno";
    private final String CERRAR = "cerrar";
    DatafonosDao datafonosDao = new DatafonosDao();

    public JsonObject enviarVentaDatafonos(float tototalMeido, MovimientosBean movimiento, String adquiriente, String codigoTerminal, int idAquiriente, boolean pagoMixto) {
        JsonObject data = new JsonObject();
        JsonObject respuesta = objetoRespuesta(0, "", "", "", "", Boolean.TRUE);
        int codigoAutorizacion = mdao.getCodigoAutorizacion();
        idTranssacciones.add(codigoAutorizacion);
        String url = NovusConstante.SECURE_CENTRAL_POINT_ENVIAR_PAGO_DATAFONO;
        data.addProperty("proveedor", adquiriente);
        data.addProperty("codigoDatafono", codigoTerminal);
        data.addProperty("negocio", "COM");
        data.addProperty("total", tototalMeido);
        data.addProperty("codigoAutorizacion", codigoAutorizacion);
        data.addProperty("cantidad", cantidad(movimiento));
        data.addProperty("promotor", Main.persona.getId());
        data.addProperty("proveedorId", idAquiriente);
        data.addProperty("movimientoId", movimiento.getId());
        data.addProperty("pos",  Main.credencial.getEquipos_id()+"");
        data.addProperty("tipoVenta", pagoMixto ? "MIXTO" : "UNICO");
        ClientWSAsync client = new ClientWSAsync("enviando pago datafono", url, NovusConstante.POST, data, true);
        JsonObject response = client.esperaRespuesta();
        if (response != null) {
            String mensaje = response.get("mensaje").getAsString();
            mensaje = client.getStatus() == 200 ? "" : mensaje;
            respuesta = respuestaDatafono(client.getStatus(), mensaje, adquiriente);
        } else {
            if (client.getError() != null && !client.getError().get("mensajeError").isJsonNull()) {
                String mensaje;
                if (client.getError().get("mensajeError").isJsonPrimitive()) {
                    mensaje = client.getError().get("mensajeError").getAsString();
                } else {
                    mensaje = client.getError().get("mensajeError").getAsJsonObject().get("mensaje").getAsString();
                }
                respuesta = respuestaDatafono(client.getStatus(), mensaje, adquiriente);
            } else {
                respuesta = respuestaDatafono(client.getStatus(), "", adquiriente);
            }
        }
        return respuesta;
    }
    
    private float cantidad(MovimientosBean movimientosBean){
        return datafonosDao.cantidadVenta(movimientosBean.getId());
    }

    public JsonObject actualizarPagoMixto(long idTransacionDatafono) {
        ObtenerDatosVentaPendienteDatafonoUseCase useCase = new ObtenerDatosVentaPendienteDatafonoUseCase(idTransacionDatafono);
        JsonObject dataActulizar = useCase.execute();

        JsonObject respuesta = objetoRespuesta(0, "", "", "", "", Boolean.TRUE);
        String url = NovusConstante.SECURE_CENTRAL_POINT_ENVIAR_PAGO_DATAFONO + "/" + idTransacionDatafono;
        if (dataActulizar != null) {
            JsonObject dataEnviar = new JsonObject();
            dataEnviar.addProperty("proveedor", dataActulizar.get("proveedor").getAsString());
            dataEnviar.addProperty("proveedorId", dataActulizar.get("idAdquiriente").getAsString());
            dataEnviar.addProperty("pos", Main.credencial.getEquipos_id()+"");
            ClientWSAsync client = new ClientWSAsync("enviando pago datafono", url, NovusConstante.PUT, dataEnviar, true);
            JsonObject clieJsonObject = client.esperaRespuesta();
            if (clieJsonObject != null) {
                String mensaje = clieJsonObject.get("mensaje").getAsString();
                mensaje = client.getStatus() == 200 ? "" : mensaje;
                respuesta = respuestaDatafono(client.getStatus(), mensaje, dataActulizar.get("proveedor").getAsString());
            } else {
                if (client.getError() != null && !client.getError().get("mensajeError").isJsonNull()) {
                    String mensaje;
                    if (client.getError().get("mensajeError").isJsonPrimitive()) {
                        mensaje = client.getError().get("mensajeError").getAsString();
                    } else {
                        mensaje = client.getError().get("mensajeError").getAsJsonObject().get("mensaje").getAsString();
                    }
                    respuesta = respuestaDatafono(client.getStatus(), mensaje,dataActulizar.get("proveedor").getAsString());
                } else {
                    respuesta = respuestaDatafono(client.getStatus(), "",dataActulizar.get("proveedor").getAsString());
                }

            }
        }
        return respuesta;
    }

    private JsonObject respuestaDatafono(int status, String mensaje, String adquiriente) {
        JsonObject data = new JsonObject();
        switch (status) {
            case 200:
                String msjError = !mensaje.isEmpty() ? "( " + mensaje + " )" : "";
                VentasHistorialView.setEstadoActulizarDatafono(true);
                data = objetoRespuesta(status, "<html><center>PAGO ENVIADO, ACERQUE LA TARJETA AL DATAFONO <br>".concat(msjError)+"</center></html>",
                        "ADQUIRIENTE "+adquiriente,
                        "ID TRANSACCIÓN ".concat(idTranssacciones.toString()),
                        transaccionRecibidaIcono,
                        Boolean.TRUE);
                System.out.println(Main.ANSI_GREEN + " se ha enviado bien :) " + Main.ANSI_RESET);
                break;
            case 400:
            case 500:
                String error = !mensaje.isEmpty() ? "( " + mensaje + " )" : "";
                data = objetoRespuesta(status,
                        "<html><center>ERROR AL ENVIAR EL PAGO AL DATAFONO <br>".concat(error)+"</center></html>",
                        "ADQUIRIENTE "+adquiriente,
                        "ID TRANSACCIÓN ".concat(idTranssacciones.toString()),
                        errorDatafonoIcono,
                        Boolean.TRUE);
                System.out.println(Main.ANSI_RED + " ha ocurrido un error F :( " + Main.ANSI_RESET);
                break;
            default:
                data = objetoRespuesta(status,
                        "ERROR AL ENVIAR EL PAGO AL DATAFONO",
                        "",
                        "",
                        errorDatafonoIcono,
                        Boolean.TRUE);
                System.out.println(Main.ANSI_RED + " ha ocurrido un error F :( " + Main.ANSI_RESET);
        }

        return data;
    }

    private JsonObject objetoRespuesta(int estado, String mensaje, String informacionDatafono, String transacciones, String icono, boolean cerrar) {
        JsonObject data = new JsonObject();
        data.addProperty(ESTADO, estado);
        data.addProperty(MENSAJE, mensaje);
        data.addProperty(INFORMACION_DATAFONO, informacionDatafono);
        data.addProperty(TRANSACCIONES, transacciones);
        data.addProperty(ICONO, icono);
        data.addProperty(CERRAR, cerrar);
        return data;
    }
}
