/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.facade.fidelizacion;

import com.WT2.loyalty.domain.entities.beans.IdentificationClient;
import com.WT2.loyalty.domain.entities.response.RespuestasAcumulacion;
import com.WT2.loyalty.presentation.handler.AcomulacionPuntosHandler;
import com.bean.MediosPagosBean;
import com.bean.MovimientosBean;
import com.bean.MovimientosDetallesBean;
import com.controllers.NovusConstante;
import com.firefuel.Main;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.utils.enums.TipoNegociosFidelizacion;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Devitech
 */
public class AcumulacionFidelizacion {

    private SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
    private static final String STATUS = "status";
    private static final String MENSAJE = "mensaje";

    public RespuestasAcumulacion acumular(IdentificationClient datosCliente, MovimientosBean movimientosBean, String identificadorPuntoDeVenta, String negocio, String consecutivo) {

        Map<String, Object> venta = new HashMap();
        venta.put("fechaTransaccion", sdf.format(new Date()));
        venta.put("identificacionPuntoVenta", identificadorPuntoDeVenta);

        String tipoVenta = "";
        if (Main.SIN_SURTIDOR) {
            tipoVenta = NovusConstante.PARAMETER_CDL;
        } else {
            tipoVenta = (negocio.endsWith(TipoNegociosFidelizacion.KIOSCO.getTipoNegocio()) ? "K" : "L");
        }

        venta.put("origenVenta", negocio);
        venta.put("tipoVenta", tipoVenta);
        venta.put("identificacionPromotor", movimientosBean.getPersonaNit() + "");
        venta.put("identificacionVenta", consecutivo);
        venta.put("valorTotalVenta", Math.round(movimientosBean.getVentaTotal()));
        venta.put("totalImpuesto", Math.round(movimientosBean.getImpuestoTotal()));
        venta.put("descuentoVenta", Math.round(movimientosBean.getDescuentoTotal()));
        venta.put("pagoTotal", Math.round(movimientosBean.getVentaTotal()));
        venta.put("identificacionCliente", datosCliente);
        venta.put("moviemiento", movimientosBean);
        venta.put("complementario", true);
        AcomulacionPuntosHandler automaticaHandler = new AcomulacionPuntosHandler();
        return automaticaHandler.execute(venta);

    }

    private JsonArray detalleVenta(MovimientosBean movimientosBean) {
        JsonArray productos = new JsonArray();
        for (Map.Entry<Long, MovimientosDetallesBean> entry : movimientosBean.getDetalles().entrySet()) {
            MovimientosDetallesBean detalle = entry.getValue();
            JsonObject objDetalle = new JsonObject();
            objDetalle.addProperty("identificacionProducto", detalle.getId() + "");
            objDetalle.addProperty("cantidadProducto", Math.round(detalle.getCantidadUnidad()));
            objDetalle.addProperty("valorUnitarioProducto", Math.round(detalle.getPrecio()));
            productos.add(objDetalle);
        }
        return productos;
    }

    private JsonArray detalleMediosPagos(MovimientosBean movimientosBean) {
        JsonArray pagos = new JsonArray();
        for (Map.Entry<Long, MediosPagosBean> entry : movimientosBean.getMediosPagos().entrySet()) {
            MediosPagosBean detallePagos = entry.getValue();
            JsonObject objDetallePago = new JsonObject();
            objDetallePago.addProperty("MediosPagosBean", detallePagos.getId());
            objDetallePago.addProperty("valorPago", Math.round(detallePagos.getRecibido()));
            pagos.add(objDetallePago);
        }
        return pagos;
    }

    public JsonObject respuesta(String mensaje, int status) {
        JsonObject response = new JsonObject();
        response.addProperty(STATUS, status);
        response.addProperty(MENSAJE, mensaje);
        return response;
    }
}
