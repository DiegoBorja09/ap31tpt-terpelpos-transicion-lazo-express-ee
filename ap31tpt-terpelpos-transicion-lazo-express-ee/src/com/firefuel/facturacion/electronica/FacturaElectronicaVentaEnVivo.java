/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.facturacion.electronica;

import com.application.useCases.movimientos.BuscarTransaccionDatafonoCaseUse;
import com.bean.Notificador;
import com.bean.ReciboExtended;
import com.controllers.NovusUtils;
import com.dao.MovimientosDao;
import com.dao.SurtidorDao;
import com.firefuel.asignarCliente.beans.RespuestaMensaje;
import com.google.gson.JsonObject;
import javax.swing.JFrame;
import com.application.useCases.wacherparametros.FindByParameterUseCase;
import com.application.commons.CtWacherParametrosEnum;
import com.application.useCases.sutidores.ActualizarVentaParaImprimirUseCase;
import com.application.useCases.transmisiones.ObtenerIdRemisionDesdeMovimientoUseCase;


/**
 *
 * @author Devitech
 */
public class FacturaElectronicaVentaEnVivo {
    FindByParameterUseCase findByParameterUseCase = new FindByParameterUseCase(CtWacherParametrosEnum.CODIGO.getColumnName(), "REMISION");
    ActualizarVentaParaImprimirUseCase actualizarVentaParaImprimirUseCase = new ActualizarVentaParaImprimirUseCase();
    JsonObject datosImpresion = new JsonObject();
    boolean ventaSinFacturar;
    boolean ventaEnVivo;
    MovimientosDao mdao = new MovimientosDao();
    private Boolean principal;

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
        errorJson.add("datosImpresion", datosImpresion);
        errorJson.addProperty("venta_en_vivo", ventaEnVivo);
        errorJson.addProperty("loader", false);
        errorJson.addProperty("venta_sin_facturar", ventaSinFacturar);
        if (this.principal != null && this.principal) {
            errorJson.addProperty("principal", true);
        }

        notificadorView.send(errorJson);
    }

    public void setLoader(String mensaje, Notificador notificadorView) {
        JsonObject errorJson = new JsonObject();
        errorJson.addProperty("mensajeError", mensaje);
        errorJson.addProperty("icono", "/com/firefuel/resources/loader_fac.gif");
        errorJson.addProperty("habilitar", false);
        errorJson.addProperty("autoclose", false);
        errorJson.addProperty("loader", true);
        notificadorView.send(errorJson);
    }

    public void datosCliente(JsonObject cliente, JFrame principal, Notificador notificadorView, JsonObject objPlaca, JsonObject objManguera) {
        cliente.addProperty("imprimir", false);
        SurtidorDao dao = new SurtidorDao();
        ReciboExtended recibo = new ReciboExtended();
        recibo.setSurtidor(objManguera.get("surtidor").getAsInt() + "");
        recibo.setManguera(objManguera.get("id").getAsInt() + "");
        recibo.setCara(objManguera.get("cara").getAsInt() + "");
        //datos de la placa para actualizar
        dao.updateVentasEncurso(recibo, objPlaca, 2);
        //datos de la factura electronica
        ventaEnVivo = true;
        datosImpresion.addProperty("cara", objManguera.get("cara").getAsInt() + "");
        datosImpresion.addProperty("manguera", objManguera.get("id").getAsInt() + "");
        int tipoVenta = 3;
        if (findByParameterUseCase.execute()) {
            tipoVenta = 4;
        }
        int tipo = cliente.get("tipoDocumento") != null ? cliente.get("tipoDocumento").getAsInt() : cliente.get("identificacion_cliente").getAsInt();
        cliente.addProperty("descripcionTipoDocumento", NovusUtils.tipoDocumento(tipo));
        dao.updateVentasEncurso(recibo, cliente, tipoVenta);
        setMensaje("<center>SE ACTUALIZA LOS DATOS DE FACTURA EN MANGUERA " + objManguera.get("id").getAsInt() + "</center>", "/com/firefuel/resources/btOk.png", notificadorView);
    }

    // public void datosCliente(JsonObject cliente, JFrame principal, Notificador notificadorView, JsonObject objPlaca, JsonObject objManguera) {
    //     cliente.addProperty("imprimir", false);
    //     SurtidorDao dao = new SurtidorDao();
    //     ReciboExtended recibo = new ReciboExtended();
    //     recibo.setSurtidor(objManguera.get("surtidor").getAsInt() + "");
    //     recibo.setManguera(objManguera.get("id").getAsInt() + "");
    //     recibo.setCara(objManguera.get("cara").getAsInt() + "");
    //     //datos de la placa para actualizar
    //     dao.updateVentasEncurso(recibo, objPlaca, 2);
    //     //datos de la factura electronica
    //     ventaEnVivo = true;
    //     datosImpresion.addProperty("cara", objManguera.get("cara").getAsInt() + "");
    //     datosImpresion.addProperty("manguera", objManguera.get("id").getAsInt() + "");
    //     int tipoVenta = 3;
    //     if (mdao.remisionActiva()) {
    //         tipoVenta = 4;
    //     }
    //     int tipo = cliente.get("tipoDocumento") != null ? cliente.get("tipoDocumento").getAsInt() : cliente.get("identificacion_cliente").getAsInt();
    //     cliente.addProperty("descripcionTipoDocumento", NovusUtils.tipoDocumento(tipo));
    //     dao.updateVentasEncurso(recibo, cliente, tipoVenta);
    //     setMensaje("<center>SE ACTUALIZA LOS DATOS DE FACTURA EN MANGUERA " + objManguera.get("id").getAsInt() + "</center>", "/com/firefuel/resources/btOk.png", notificadorView);
    // }

    public void imprimirVenta(JsonObject json) {
        //SurtidorDao dao = new SurtidorDao();
        actualizarVentaParaImprimirUseCase.execute(json);
    }

    public void datosSinfacturar(JsonObject cliente, JsonObject datosCliente, long numeroVenta, JFrame principal, Notificador notificadorView, boolean datafono, long idMovimiento, String message, boolean isAppTerpel) {
        MovimientosDao sdao = new MovimientosDao();
        boolean venta;

        // 🔍 DEBUG: Verificar estado del parámetro REMISION
        boolean isRemisionActiva = findByParameterUseCase.execute();
        System.out.println("🔍 DEBUG datosSinfacturar():");
        System.out.println("    - Parámetro REMISION activo: " + isRemisionActiva);
        System.out.println("    - numeroVenta recibido: " + numeroVenta);
        System.out.println("    - idMovimiento recibido: " + idMovimiento);

        if (isRemisionActiva) {
            // ✅ Fallback si numeroVenta viene en 0
            if (numeroVenta == 0) {
                System.out.println("✅ ID remisión recuperado desde movimiento: " + idMovimiento);
                //Long idRemision = sdao.obtenerIdRemisionDesdeMovimiento(idMovimiento);
                Long idRemision = new ObtenerIdRemisionDesdeMovimientoUseCase(idMovimiento).execute();
                if (idRemision != null) {
                    numeroVenta = idRemision;
                    System.out.println("✅ ID remisión recuperado desde movimiento: " + numeroVenta);
                } else {
                    System.err.println("❌ No se encontró remisión para el movimiento " + idMovimiento);
                    setMensaje("ERROR AL ENVIAR FACTURA ELECTRONICA", "", notificadorView);
                    return;
                }
            }

            venta = sdao.updateRemisionSinFacturar(cliente, datosCliente, numeroVenta);

        } else {
            venta = sdao.updateVentasSinFacturar(cliente, datosCliente, numeroVenta, datafono, isAppTerpel, idMovimiento);
        }

        ventaSinFacturar = true;
        ventaEnVivo = false;
        datosImpresion = null;

        if (venta) {
            String mensaje = new BuscarTransaccionDatafonoCaseUse(idMovimiento).execute() ? message.toLowerCase() : "FACTURA ELECTRONICA ENVIADA";
            this.mdao.updateFEtoProcesoAppterpel(idMovimiento);
            setMensaje(mensaje, "/com/firefuel/resources/btOk.png", notificadorView);
        } else {
            setMensaje("ERROR AL ENVIAR FACTURA ELECTRONICA", "", notificadorView);
        }
    }

    // public void datosSinfacturar(JsonObject cliente, JsonObject datosCliente, long numeroVenta, JFrame principal, Notificador notificadorView, boolean datafono, long idMovimiento, String message, boolean isAppTerpel) {
    //     MovimientosDao sdao = new MovimientosDao();
    //     boolean venta;

    //     if (sdao.remisionActiva()) {
    //         // ✅ Fallback si numeroVenta viene en 0
    //         if (numeroVenta == 0) {
    //             Long idRemision = sdao.obtenerIdRemisionDesdeMovimiento(idMovimiento);
    //             if (idRemision != null) {
    //                 numeroVenta = idRemision;
    //                 System.out.println("✅ ID remisión recuperado desde movimiento: " + numeroVenta);
    //             } else {
    //                 System.err.println("❌ No se encontró remisión para el movimiento " + idMovimiento);
    //                 setMensaje("ERROR AL ENVIAR FACTURA ELECTRONICA", "", notificadorView);
    //                 return;
    //             }
    //         }

    //         venta = sdao.updateRemisionSinFacturar(cliente, datosCliente, numeroVenta);

    //     } else {
    //         venta = sdao.updateVentasSinFacturar(cliente, datosCliente, numeroVenta, datafono, isAppTerpel);
    //     }

    //     ventaSinFacturar = true;
    //     ventaEnVivo = false;
    //     datosImpresion = null;

    //     if (venta) {
    //         String mensaje = mdao.buscarTransaccionDatafono(idMovimiento) ? message.toLowerCase() : "FACTURA ELECTRONICA ENVIADA";
    //         this.mdao.updateFEtoProcesoAppterpel(idMovimiento);
    //         setMensaje(mensaje, "/com/firefuel/resources/btOk.png", notificadorView);
    //     } else {
    //         setMensaje("ERROR AL ENVIAR FACTURA ELECTRONICA", "", notificadorView);
    //     }
    // }

    // public RespuestaMensaje datosSinfacturar(JsonObject cliente, JsonObject datosCliente, long numeroVenta, boolean datafono, long idMovimiento, String message, boolean isAppTerpel) {
    //     RespuestaMensaje respuestaMensaje = new RespuestaMensaje();
    //     MovimientosDao sdao = new MovimientosDao();
    //     boolean venta;
    //     if (sdao.remisionActiva()) {
    //         venta = sdao.updateRemisionSinFacturar(cliente, datosCliente, numeroVenta);
    //     } else {
    //         venta = sdao.updateVentasSinFacturar(cliente, datosCliente, numeroVenta, datafono, isAppTerpel);
    //     }
    //     ventaSinFacturar = true;
    //     ventaEnVivo = false;
    //     datosImpresion = null;
    //     if (venta) {
    //         String mensaje = mdao.buscarTransaccionDatafono(idMovimiento) ? message.toLowerCase() : "FACTURA ELECTRONICA ENVIADA";
    //         respuestaMensaje.setMensaje(mensaje);
    //         respuestaMensaje.setError(false);
    //         this.mdao.updateFEtoProcesoAppterpel(idMovimiento);
    //     } else {
    //         respuestaMensaje.setMensaje("ERROR AL ENVIAR FACTURA ELECTRONICA");
    //         respuestaMensaje.setError(true);
    //     }

    //     return respuestaMensaje;
    // }
    public RespuestaMensaje datosSinfacturar(JsonObject cliente, JsonObject datosCliente, long numeroVenta, boolean datafono, long idMovimiento, String message, boolean isAppTerpel) {
        RespuestaMensaje respuestaMensaje = new RespuestaMensaje();
        MovimientosDao sdao = new MovimientosDao();
        boolean venta;
        if (findByParameterUseCase.execute()) {
            venta = sdao.updateRemisionSinFacturar(cliente, datosCliente, numeroVenta);
        } else {
            venta = sdao.updateVentasSinFacturar(cliente, datosCliente, numeroVenta, datafono, isAppTerpel, idMovimiento);
        }
        ventaSinFacturar = true;
        ventaEnVivo = false;
        datosImpresion = null;
        if (venta) {
            String mensaje = new BuscarTransaccionDatafonoCaseUse(idMovimiento).execute() ? message.toLowerCase() : "FACTURA ELECTRONICA ENVIADA";
            respuestaMensaje.setMensaje(mensaje);
            respuestaMensaje.setError(false);
            this.mdao.updateFEtoProcesoAppterpel(idMovimiento);
        } else {
            respuestaMensaje.setMensaje("ERROR AL ENVIAR FACTURA ELECTRONICA");
            respuestaMensaje.setError(true);
        }

        return respuestaMensaje;
    }


}
