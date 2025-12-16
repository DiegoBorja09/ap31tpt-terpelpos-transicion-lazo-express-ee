package com.facade;

import com.application.useCases.movimientos.UpadteByEstateMovimientoUseCase;
import com.bean.BodegaBean;
import com.controllers.NovusConstante;
import com.bean.EmpresaBean;
import com.bean.ImpuestosBean;
import com.bean.MediosPagosBean;
import com.bean.MovimientosBean;
import com.bean.MovimientosDetallesBean;
import com.bean.ProductoBean;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.EquipoDao;
import com.dao.MovimientosDao;
import com.firefuel.Main;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author novus
 */
public class PedidoFacade {

    public static boolean consumoPropio;

    public JsonObject sendVentaKIOSCO(MovimientosBean movimiento, boolean transmitir, boolean cortesia) throws DAOException {
        NovusUtils.printLn("sendVentaKIOSCO");
        JsonObject json = new JsonObject();
        SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
        JsonObject response = null;

        MovimientosDao mdao = new MovimientosDao();
        EquipoDao dao = new EquipoDao();
        try {
            EmpresaBean empresa = dao.findEmpresa(Main.credencial);
            movimiento.setEmpresa(empresa);

        } catch (DAOException ex) {
            Logger.getLogger(PedidoFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        String url = NovusConstante.SECURE_CENTRAL_POINT_SUBIR_VENTA_KIOSCO;
        try {
            JsonObject movimientoJson = new JsonObject();
            //OBTENER BODEGA CORRECTAMENTE PARA DESCONTAR EN LA WEB HO
            BodegaBean bodega = null;
            String finalidad = "K";
            for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
                MovimientosDetallesBean value = entry.getValue();
                if (value.getIngredientes() != null && !value.getIngredientes().isEmpty()) {
                    for (ProductoBean ingrediente : value.getIngredientes()) {
                        bodega = dao.findBodegaByProductoId(finalidad, ingrediente.getId());
                        break;
                    }
                } else {
                    bodega = dao.findBodegaByProductoId(finalidad, value.getProductoId());
                }
                break;
            }
            movimiento.setBodega(bodega);

            movimientoJson.addProperty("identificadorNegocio", movimiento.getEmpresa().getNegocioId());
            movimientoJson.addProperty("identificacionEstacion", movimiento.getEmpresasId());
            movimientoJson.addProperty("codigoEstacion", movimiento.getEmpresa().getCodigo());
            movimientoJson.addProperty("prefijo", movimiento.getConsecutivo().getPrefijo());
            movimientoJson.addProperty("nombreEstacion", movimiento.getEmpresa().getRazonSocial());
            movimientoJson.addProperty("aliasEstacion", movimiento.getEmpresa().getAlias());
            movimientoJson.addProperty("identificadorEstacion", movimiento.getEmpresa().getNit());
            movimientoJson.addProperty("identificadorTicketVenta", movimiento.getConsecutivo().getConsecutivo_actual());
            movimientoJson.addProperty("identificadorTicket", movimiento.getConsecutivo().getId());
            movimientoJson.addProperty("idTransaccionVenta", movimiento.getId());
            movimientoJson.addProperty("fechaTransaccion", sdf.format(movimiento.getFecha().getTime()));
            movimientoJson.addProperty("identificadorPromotor", movimiento.getPersonaId());
            movimientoJson.addProperty("nombresPromotor", movimiento.getPersonaNombre());
            movimientoJson.addProperty("apellidosPromotor",
                    movimiento.getPersonaApellidos() != null && !movimiento.getPersonaApellidos().trim().equals("")
                    ? movimiento.getPersonaApellidos()
                    : movimiento.getPersonaNombre());
            movimientoJson.addProperty("identificacionPromotor", movimiento.getPersonaNit());
            movimientoJson.addProperty("identificadorPersona", movimiento.getClienteId());
            movimientoJson.addProperty("identificacionPersona", movimiento.getClienteNit());
            movimientoJson.addProperty("nombresPersona", movimiento.getClienteNombre());
            movimientoJson.addProperty("apellidosPersona", " ");
            movimientoJson.addProperty("identificadorProveedor", 0);
            movimientoJson.addProperty("identificadorBodega", movimiento.getBodega().getId());
            movimientoJson.addProperty("nombresBodega", movimiento.getBodega().getDescripcion());
            movimientoJson.addProperty("codigoBodega", movimiento.getBodega().getCodigo());
            movimientoJson.addProperty("costoTotal", movimiento.getCostoTotal());
            movimientoJson.addProperty("ventaTotal", movimiento.getVentaTotal());
            movimientoJson.addProperty("descuentoTotal", movimiento.getDescuentoTotal());
            movimientoJson.addProperty("impuestoTotal", movimiento.getImpuestoTotal());
            if (consumoPropio) {
                movimientoJson.addProperty("ventaTotal", movimiento.getImpuestoTotal());
            }
            JsonObject atributos = new JsonObject();
            atributos.addProperty("fidelizada", movimiento.getVentaFidelizada());
            atributos.addProperty("tipo_negocio", NovusUtils.idTipoNegocio(NovusUtils.getTipoNegocioComplementario()));
            atributos.addProperty("", cortesia);
            atributos.addProperty("", cortesia);
            atributos.addProperty("", cortesia);
            atributos.addProperty("", cortesia);
            movimientoJson.add("atributos", atributos);
            movimientoJson.addProperty("identificadorEquipo", Main.credencial.getEquipos_id());
            movimientoJson.addProperty("impresoTiquete", "S");
            movimientoJson.addProperty("usoDolar", 0);
            movimientoJson.addProperty("identificadorJornada", movimiento.getGrupoJornadaId());
            movimientoJson.addProperty("identificadorOrigen", 0);
            JsonArray detallesVentasArray = new JsonArray();
            for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
                Long key = entry.getKey();
                MovimientosDetallesBean value = entry.getValue();
                JsonObject detalleJson = new JsonObject();
                detalleJson.addProperty("idTransaccionVentaDetalle", value.getRemotoId());

                // detalleJson.addProperty("idTransaccionDetalleVenta", value.getId()); //->
                // Mandar en cero
                detalleJson.addProperty("idTransaccionDetalleVenta", 0);

                detalleJson.addProperty("identificadorProducto", value.getProductoId());
                detalleJson.addProperty("nombreProducto", value.getDescripcion());
                detalleJson.addProperty("identificacionProducto", value.getPlu());
                detalleJson.addProperty("fechaTransaccion", sdf.format(movimiento.getFecha().getTime()));
                detalleJson.addProperty("cantidadVenta", value.getCantidadUnidad());
                detalleJson.addProperty("identificadorUnidad", value.getUnidades_medida_id());
                detalleJson.addProperty("costoProducto", value.getCosto());
                detalleJson.addProperty("precioProducto", value.getPrecio());
                detalleJson.addProperty("identificadorDescuento", value.getDescuentoId());
                detalleJson.addProperty("descuentoTotal", value.getDescuentoProducto());
                detalleJson.addProperty("subTotalVenta", value.getSubtotal());

                JsonObject jsonAtributos = new JsonObject();
                jsonAtributos.addProperty("categoriaId", value.getCategoriaId());
                jsonAtributos.addProperty("categoriaDescripcion", value.getCategoriaDesc());
                jsonAtributos.addProperty("tipo", value.getTipo());

                long idPrecioEspecial = mdao.getIdPrecioEspecial(value.getProductoId());

                if (idPrecioEspecial != 0) {
                    jsonAtributos.addProperty("precio_especial_id", idPrecioEspecial);
                }

                if (value.getTipo() == NovusConstante.PRODUCTO_DESPACHO_COMBUSTIBLE_VENTA_MIXTA) {
                    jsonAtributos.addProperty("unidad", value.getUnidades_medida());
                }
                detalleJson.add("atributos", jsonAtributos);
                JsonArray ingredientesArray = new JsonArray();

                for (ProductoBean ingrediente : value.getIngredientes()) {
                    JsonObject ing = new JsonObject();
                    ing.addProperty("identificadorProducto", ingrediente.getId());
                    ing.addProperty("cantidadVenta", ingrediente.getProducto_compuesto_cantidad() * value.getCantidadUnidad());
                    ing.addProperty("costo", ingrediente.getProducto_compuesto_costo() * value.getCantidadUnidad()
                            * ingrediente.getProducto_compuesto_cantidad());
                    ingredientesArray.add(ing);
                }
                detalleJson.add("ingredientesAplicados", ingredientesArray);
                JsonArray impuestosArray = new JsonArray();
                if (!value.getImpuestos().isEmpty()) {
                    float impuestoComsumoPropio = 0;
                    for (ImpuestosBean impuesto : value.getImpuestos()) {
                        JsonObject imp = new JsonObject();
                        imp.addProperty("identificadorImpuesto", impuesto.getId());
                        imp.addProperty("nombreImpuesto", impuesto.getDescripcion());
                        imp.addProperty("tipoImpuesto", impuesto.getPorcentaje_valor());
                        imp.addProperty("valorImpAplicado", impuesto.getValor());
                        imp.addProperty("valorImpuestoAplicado", impuesto.getCalculado());
                        if (impuesto.getPorcentaje_valor().equals("%")) {
                            impuestoComsumoPropio = impuestoComsumoPropio + impuesto.getCalculado();
                        }
                        if (!consumoPropio) {
                            impuestosArray.add(imp);
                        }
                    }
                    if (consumoPropio) {
                        detalleJson.addProperty("subTotalVenta", impuestoComsumoPropio);
                    }
                } else {
                    if (consumoPropio) {
                        detalleJson.addProperty("subTotalVenta", 0);
                    }
                }
                detalleJson.add("impuestosAplicados", impuestosArray);
                detallesVentasArray.add(detalleJson);
            }

            JsonArray MediosPagosArray = new JsonArray();

            for (Map.Entry<Long, MediosPagosBean> entry : movimiento.getMediosPagos().entrySet()) {
                Long key = entry.getKey();
                MediosPagosBean mediopago = entry.getValue();
                JsonObject objMedioPago = new JsonObject();

                objMedioPago.addProperty("descripcionMedio", mediopago.getDescripcion());
                objMedioPago.addProperty("identificacionMediosPagos", mediopago.getId());
                if (consumoPropio) {
                    objMedioPago.addProperty("recibidoMedioPago", movimiento.getImpuestoTotal());
                    objMedioPago.addProperty("totalMedioPago", movimiento.getImpuestoTotal());
                } else {
                    objMedioPago.addProperty("recibidoMedioPago", mediopago.getRecibido());
                    objMedioPago.addProperty("totalMedioPago", mediopago.getValor());
                }
                objMedioPago.addProperty("vueltoMedioPago", mediopago.getCambio());
                objMedioPago.addProperty("identificacionComprobante",
                        mediopago.getVoucher() != null ? mediopago.getVoucher() : "");
                objMedioPago.addProperty("monedaLocal", "S");
                objMedioPago.addProperty("trm", 0);
                MediosPagosArray.add(objMedioPago);
            }

            json.add("transaccion", movimientoJson);
            json.add("detallesVenta", detallesVentasArray);
            json.add("mediosPagos", MediosPagosArray);
            if (movimiento.isFidelizar()) {
                JsonObject header = movimiento.getDatosFidelizacion().get("body").getAsJsonObject().get("headers").getAsJsonObject();
                movimiento.getDatosFidelizacion().get("body").getAsJsonObject().remove("headers");
                json.add("fidelizacion", movimiento.getDatosFidelizacion());
                json.get("fidelizacion").getAsJsonObject().addProperty("urlAcumulacionCliente", NovusConstante.SECURE_CENTRAL_POINT_ACUMULACION_CLIENTE);
                json.get("fidelizacion").getAsJsonObject().addProperty("urlConsultaCliente", NovusConstante.SECURE_CENTRAL_POINT_CONSULTA_CLIENTE);
                json.get("fidelizacion").getAsJsonObject().addProperty("identificadorNegocio", movimiento.getEmpresasId());
                json.get("fidelizacion").getAsJsonObject().add("headers", header);
            }
            NovusUtils.printLn(" Procesando Movimiento Kiosko ");
            JsonObject respuesta = mdao.procesarVentasKiosco(json, NovusConstante.TIPO_VENTA_KCO, NovusConstante.ESTADO_MOVIMIENTO_KCO);
            int codigo = respuesta.get("codigo").getAsInt();
            if (transmitir) {
                if (codigo == 0 || codigo != 200) {
                    EquipoDao e = new EquipoDao();
                    try {
                        e.guardarTransmisionKioscoCanastilla(Main.credencial, json.toString(), url, NovusConstante.POST);
                    } catch (DAOException ex) {
                        Logger.getLogger(PedidoFacade.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(PedidoFacade.class.getName()).log(Level.SEVERE, null, e);
            NovusUtils.printLn(e.getMessage());
        }
        if (transmitir) {
            return response;
        } else {
            return json;
        }
    }      

    public JsonObject sendVentaKIOSCOtoServer(MovimientosBean movimiento, boolean transmitir) {
        NovusUtils.printLn("sendVentaKIOSCOtoServer");
        JsonObject json = new JsonObject();
        SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
        JsonObject response = null;
        boolean DEBUG = true;
        EquipoDao dao = new EquipoDao();
        MovimientosDao mdao = new MovimientosDao();
        try {
            EmpresaBean empresa = dao.findEmpresa(Main.credencial);
            movimiento.setEmpresa(empresa);

        } catch (DAOException ex) {
            Logger.getLogger(PedidoFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        String url = NovusConstante.SECURE_CENTRAL_POINT_SUBIR_VENTA_KIOSCO;
        try {

            JsonObject movimientoJson = new JsonObject();

            int index = 0;

            movimientoJson.addProperty("identificadorContrato", 0);
            movimientoJson.addProperty("recaudo", 0);
            movimientoJson.addProperty("identificadorNegocio", movimiento.getEmpresa().getNegocioId());
            movimientoJson.addProperty("isRecaudo", false);
            movimientoJson.addProperty("identificacionEstacion", movimiento.getEmpresasId());
            movimientoJson.addProperty("codigoEstacion", movimiento.getEmpresa().getCodigo());
            movimientoJson.addProperty("estado", "A");
            movimientoJson.addProperty("sincronizado", "0");
            movimientoJson.addProperty("prefijoTransaccionVenta", movimiento.getConsecutivo().getPrefijo());
            movimientoJson.addProperty("nombreEstacion", movimiento.getEmpresa().getRazonSocial());
            movimientoJson.addProperty("aliasEstacion", movimiento.getEmpresa().getAlias());
            movimientoJson.addProperty("identificadorEstacion", movimiento.getEmpresa().getNit());
            movimientoJson.addProperty("identificadorTicketVenta", movimiento.getConsecutivo().getConsecutivo_actual());
            movimientoJson.addProperty("idTransaccionVenta", movimiento.getId());
            // movimientoJson.addProperty("identificadorTicket",
            // movimiento.getConsecutivo().getId());
            movimientoJson.addProperty("fechaTransaccion", sdf.format(movimiento.getFecha().getTime()));

            movimientoJson.addProperty("identificadorPromotor", movimiento.getPersonaId());
            movimientoJson.addProperty("nombresPromotor", movimiento.getPersonaNombre());
            movimientoJson.addProperty("apellidosPromotor", " ");
            movimientoJson.addProperty("identificacionPromotor", movimiento.getPersonaNit());

            movimientoJson.addProperty("identificadorPersona", 3);
            movimientoJson.addProperty("identificacionPersona", 333333);
            movimientoJson.addProperty("nombresPersona", "CLIENTES REGISTRADOS");
            // movimientoJson.addProperty("apellidosPersona", " ");

            movimientoJson.addProperty("identificadorProveedor", 0);

            movimientoJson.addProperty("identificadorBodega", movimiento.getBodega().getId());
            movimientoJson.addProperty("nombresBodega", movimiento.getBodega().getDescripcion());
            movimientoJson.addProperty("codigoBodega", movimiento.getBodega().getCodigo());

            movimientoJson.addProperty("costoTotal", movimiento.getCostoTotal());
            movimientoJson.addProperty("ventaTotal", movimiento.getVentaTotal());
            if (consumoPropio) {
                movimientoJson.addProperty("ventaTotal", movimiento.getImpuestoTotal());
            }
            movimientoJson.addProperty("descuentoTotal", movimiento.getDescuentoTotal());
            movimientoJson.addProperty("impuestoTotal", movimiento.getImpuestoTotal());

            movimientoJson.addProperty("identificadorEquipo", Main.credencial.getEquipos_id());
            movimientoJson.addProperty("impresoTiquete", "S");
            movimientoJson.addProperty("usoDolar", 0);
            movimientoJson.addProperty("identificadorJornada", movimiento.getGrupoJornadaId());
            movimientoJson.addProperty("identificadorOrigen", 0);

            JsonObject atributos = new JsonObject();
            atributos.addProperty("responsables_nombre", movimiento.getPersonaNombre());
            atributos.addProperty("responsables_identificacion", movimiento.getPersonaNit());

            atributos.addProperty("personas_nombre", "CLIENTES VARIOS");
            atributos.addProperty("personas_identificacion", "2222222");

            atributos.addProperty("tercero_nombre", "");
            atributos.addProperty("tercero_identificacion", "");

            JsonObject consecutivo = new JsonObject();
            consecutivo.addProperty("id", movimiento.getConsecutivo().getId());
            consecutivo.addProperty("prefijo", movimiento.getConsecutivo().getPrefijo());

            atributos.add("consecutivo", consecutivo);
            movimientoJson.add("atributos", atributos);

            movimientoJson.addProperty("identificadorTicket", movimiento.getConsecutivo().getId());

            JsonArray detallesVentasArray = new JsonArray();
            for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {

                Long key = entry.getKey();
                MovimientosDetallesBean value = entry.getValue();
                JsonObject detalleJson = new JsonObject();

                JsonObject jsonAtributos = new JsonObject();
                jsonAtributos.addProperty("categoriaId", value.getCategoriaId());
                jsonAtributos.addProperty("categoriaDescripcion", value.getCategoriaDesc());
                jsonAtributos.addProperty("tipo", value.getTipo());
                if (value.getTipo() == NovusConstante.PRODUCTO_DESPACHO_COMBUSTIBLE_VENTA_MIXTA) {
                    jsonAtributos.addProperty("unidad", value.getUnidades_medida());
                }
                detalleJson.add("atributos", jsonAtributos);

                detalleJson.addProperty("idTransaccionVentaDetalle", value.getRemotoId());
                detalleJson.addProperty("idTransaccionDetalleVenta", value.getRemotoId());
                detalleJson.addProperty("identificadorBodega", movimiento.getBodega().getId());

                detalleJson.addProperty("identificadorProducto", value.getProductoId());
                detalleJson.addProperty("nombreProducto", value.getDescripcion());
                detalleJson.addProperty("identificacionProducto", value.getPlu());

                detalleJson.addProperty("fechaTransaccion", sdf.format(movimiento.getFecha().getTime()));
                detalleJson.addProperty("cantidadVenta", value.getCantidadUnidad());
                detalleJson.addProperty("identificadorUnidad", value.getUnidades_medida_id());
                detalleJson.addProperty("costoProducto", value.getCosto() == 0 ? 1 : value.getCosto());
                detalleJson.addProperty("precioProducto", value.getPrecio());
                detalleJson.addProperty("identificadorDescuento", value.getDescuentoId());
                detalleJson.addProperty("descuentoTotal", value.getDescuentoProducto());
                detalleJson.addProperty("subTotalVenta", value.getSubtotal());

                JsonArray ingredientesArray = new JsonArray();
                for (ProductoBean ingrediente : value.getIngredientes()) {
                    JsonObject ing = new JsonObject();
                    ing.addProperty("identificadorProducto", ingrediente.getId());
                    ing.addProperty("cantidadVenta",
                            ingrediente.getProducto_compuesto_cantidad() * value.getCantidadUnidad());
                    ing.addProperty("costo", ingrediente.getProducto_compuesto_costo() * value.getCantidadUnidad()
                            * ingrediente.getProducto_compuesto_cantidad());
                    ingredientesArray.add(ing);
                }
                detalleJson.add("ingredientesAplicados", ingredientesArray);

                JsonArray impuestosArray = new JsonArray();
                if (!value.getImpuestos().isEmpty()) {
                    float impuestoComsumoPropio = 0;
                    for (ImpuestosBean impuesto : value.getImpuestos()) {
                        JsonObject imp = new JsonObject();
                        imp.addProperty("identificadorImpuesto", impuesto.getId());
                        imp.addProperty("nombreImpuesto", impuesto.getDescripcion());
                        imp.addProperty("tipoImpuesto", impuesto.getPorcentaje_valor());
                        imp.addProperty("valorImpAplicado", impuesto.getValor());
                        imp.addProperty("valorImpuestoAplicado", impuesto.getCalculado());
                        if (impuesto.getPorcentaje_valor().equals("%")) {
                            impuestoComsumoPropio = impuestoComsumoPropio + impuesto.getCalculado();
                        }
                        if (!consumoPropio) {
                            impuestosArray.add(imp);
                        }

                    }
                    if (consumoPropio) {
                        detalleJson.addProperty("subTotalVenta", impuestoComsumoPropio);
                    }
                } else {
                    if (consumoPropio) {
                        detalleJson.addProperty("subTotalVenta", 0);
                    }
                }

                detalleJson.add("impuestosAplicados", impuestosArray);
                detallesVentasArray.add(detalleJson);
            }

            JsonArray MediosPagosArray = new JsonArray();

            for (Map.Entry<Long, MediosPagosBean> entry : movimiento.getMediosPagos().entrySet()) {
                Long key = entry.getKey();
                MediosPagosBean mediopago = entry.getValue();
                JsonObject objMedioPago = new JsonObject();

                objMedioPago.addProperty("id", mediopago.getIdRegistro());
                objMedioPago.addProperty("descripcionMedio", mediopago.getDescripcion());
                objMedioPago.addProperty("identificacionMediosPagos", mediopago.getId());
                if (consumoPropio) {
                    objMedioPago.addProperty("recibidoMedioPago", movimiento.getImpuestoTotal());
                    objMedioPago.addProperty("totalMedioPago", movimiento.getImpuestoTotal());
                } else {
                    objMedioPago.addProperty("recibidoMedioPago", mediopago.getRecibido());
                    objMedioPago.addProperty("totalMedioPago", mediopago.getValor());
                }
                objMedioPago.addProperty("vueltoMedioPago", mediopago.getCambio());
                objMedioPago.addProperty("identificacionComprobante",
                        mediopago.getVoucher() != null ? mediopago.getVoucher() : "");
                objMedioPago.addProperty("monedaLocal", "S");
                objMedioPago.addProperty("trm", 0);
                MediosPagosArray.add(objMedioPago);
            }

            json.add("transaccion", movimientoJson);
            json.add("detallesVenta", detallesVentasArray);
            json.add("mediosPagos", MediosPagosArray);

            if (transmitir) {
                JsonObject respuesta = mdao.procesarVentasKiosco(json, NovusConstante.TIPO_VENTA_KCO, NovusConstante.ESTADO_MOVIMIENTO_KCO);
                int codigo = respuesta.get("codigo").getAsInt();

                if (codigo == 0 || codigo != 200) {
                    EquipoDao e = new EquipoDao();
                    try {
                        e.guardarTransmisionKioscoCanastilla(Main.credencial, json.toString(), url, NovusConstante.POST);
                    } catch (DAOException ex) {
                        Logger.getLogger(PedidoFacade.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }

        } catch (Exception e) {
            Logger.getLogger(PedidoFacade.class.getName()).log(Level.SEVERE, null, e);
            NovusUtils.printLn(e.getMessage());
        }
        if (transmitir) {
            return response;
        } else {
            return json;
        }
    }

    public JsonObject sendKioscoFE(JsonObject json) {
        NovusUtils.printLn("sendKioscoFE");
        String url = NovusConstante.SECURE_CENTRAL_POINT_SUBIR_VENTA_KIOSCO;

        MovimientosDao mdao = new MovimientosDao();
        JsonObject respuesta = mdao.procesarVentasKiosco(json, NovusConstante.TIPO_VENTA_KCO, NovusConstante.ESTADO_MOVIMIENTO_KCO);
        int codigo = respuesta.get("codigo").getAsInt();

        if (codigo == 0 || codigo != 200) {
            EquipoDao e = new EquipoDao();
            try {
                e.guardarTransmisionKioscoCanastilla(Main.credencial, json.toString(), url, NovusConstante.POST);
            } catch (DAOException ex) {
                Logger.getLogger(PedidoFacade.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return respuesta;
    }

    public JsonObject sendCanFE(JsonObject json) {
        NovusUtils.printLn("sendCanFE");
        String url = NovusConstante.SECURE_CENTRAL_POINT_SUBIR_VENTA;
        MovimientosDao mdao = new MovimientosDao();
        JsonObject respuesta = mdao.procesarVentasKiosco(json, NovusConstante.TIPO_VENTA_CAN, NovusConstante.ESTADO_MOVIMIENTO_CAN);
        int codigo = respuesta.get("codigo").getAsInt();

        if (codigo == 0 || codigo != 200) {
            EquipoDao e = new EquipoDao();
            try {
                e.guardarTransmisionKioscoCanastilla(Main.credencial, json.toString(), url, NovusConstante.POST);
            } catch (DAOException ex) {
                Logger.getLogger(PedidoFacade.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return respuesta;
    }

    public JsonObject sendCanCortecia(JsonObject json, boolean cortesia) {
        NovusUtils.printLn("sendCanCortecia");
        String url = NovusConstante.SECURE_CENTRAL_POINT_SUBIR_VENTA;
        JsonObject venta = new JsonObject();
        JsonObject objVenta = json.get("venta").getAsJsonObject();
        venta.addProperty("identificadorNegocio", Integer.parseInt(objVenta.get("identificadorNegocio").getAsString()));
        venta.addProperty("identificacionEstacion", objVenta.get("identificacionEstacion").getAsInt());
        venta.addProperty("codigoEstacion", objVenta.get("codigoEstacion").getAsString());
        venta.addProperty("prefijo", objVenta.get("prefijo").getAsString());
        venta.addProperty("nombreEstacion", objVenta.get("nombreEstacion").getAsString());
        venta.addProperty("aliasEstacion", objVenta.get("aliasEstacion").getAsString());
        venta.addProperty("identificadorEstacion", objVenta.get("identificadorEstacion").getAsString());
        venta.addProperty("identificadorTicketVenta", objVenta.get("consecutivoActual").getAsLong());
        venta.addProperty("idTransaccionVenta", objVenta.get("idTransaccionVenta").getAsLong());
        venta.addProperty("identificadorTicket", objVenta.get("consecutivo_id").getAsLong());
        venta.addProperty("fechaTransaccion", objVenta.get("fechaTransaccion").getAsString());
        venta.addProperty("identificadorPromotor", objVenta.get("persona_id").getAsInt());
        venta.addProperty("nombresPromotor", objVenta.get("nombresPromotor").getAsString());
        venta.addProperty("apellidosPromotor", objVenta.get("apellidosPromotor").getAsString());
        venta.addProperty("identificacionPromotor", objVenta.get("identificacionPromotor").getAsString());
        venta.addProperty("identificadorPersona", objVenta.get("identificadorPersona").getAsInt());
        venta.addProperty("nombresPersona", objVenta.get("nombresPersona").getAsString());
        venta.addProperty("apellidosPersona", objVenta.get("apellidosPersona").getAsString());
        venta.addProperty("identificadorProveedor", objVenta.get("identificadorProveedor").getAsInt());
        venta.addProperty("identificadorBodega", objVenta.get("identificadorBodega").getAsInt());
        venta.addProperty("nombresBodega", objVenta.get("nombresBodega").getAsString());
        venta.addProperty("codigoBodega", objVenta.get("codigoBodega").getAsString());
        venta.addProperty("costoTotal", objVenta.get("costoTotal").getAsFloat());
        venta.addProperty("ventaTotal", objVenta.get("ventaTotal").getAsFloat());
        venta.addProperty("descuentoTotal", objVenta.get("descuentoTotal").getAsFloat());
        venta.addProperty("impuestoTotal", objVenta.get("impuestoTotal").getAsFloat());
        venta.addProperty("identificadorEquipo", objVenta.get("identificadorEquipo").getAsInt());
        venta.addProperty("impresoTiquete", objVenta.get("impresoTiquete").getAsString());
        venta.addProperty("usoDolar", objVenta.get("usoDolar").getAsFloat());
        venta.addProperty("identificadorJornada", objVenta.get("identificadorJornada").getAsLong());
        venta.addProperty("identificadorOrigen", objVenta.get("identificadorOrigen").getAsLong());
        JsonArray arr = json.get("detallesVenta").getAsJsonArray();
        JsonArray ventaDetalles = new JsonArray();
        for (JsonElement elemt : arr) {
            JsonObject objVentadetalle = elemt.getAsJsonObject();
            JsonObject detalleVenta = new JsonObject();
            detalleVenta.addProperty("idTransaccionVentaDetalle", objVentadetalle.get("idTransaccionVentaDetalle").getAsInt());
            detalleVenta.addProperty("idTransaccionDetalleVenta", objVentadetalle.get("idTransaccionDetalleVenta").getAsInt());
            detalleVenta.addProperty("identificadorProducto", objVentadetalle.get("identificadorProducto").getAsInt());
            detalleVenta.addProperty("nombreProducto", objVentadetalle.get("nombreProducto").getAsString());
            detalleVenta.addProperty("identificacionProducto", objVentadetalle.get("identificacionProducto").getAsString());
            detalleVenta.addProperty("fechaTransaccion", objVentadetalle.get("fechaTransaccion").getAsString());
            detalleVenta.addProperty("cantidadVenta", objVentadetalle.get("cantidadVenta").getAsInt());
            detalleVenta.addProperty("identificadorUnidad", objVentadetalle.get("identificadorUnidad").getAsInt());
            detalleVenta.addProperty("costoProducto", objVentadetalle.get("costoProducto").getAsFloat());
            detalleVenta.addProperty("precioProducto", objVentadetalle.get("precioProducto").getAsFloat());
            detalleVenta.addProperty("identificadorDescuento", objVentadetalle.get("identificadorDescuento").getAsInt());
            detalleVenta.addProperty("descuentoTotal", objVentadetalle.get("descuentoTotal").getAsFloat());
            detalleVenta.addProperty("subTotalVenta", objVentadetalle.get("subTotalVenta").getAsFloat());
            JsonObject atributos = new JsonObject();
            JsonObject objAtrib = objVentadetalle.get("atributos").getAsJsonObject();
            atributos.addProperty("categoriaId", objAtrib.get("categoriaId").getAsInt());
            atributos.addProperty("categoriaDescripcion", objAtrib.get("categoriaDescripcion").getAsString());
            atributos.addProperty("tipo", objAtrib.get("tipo").getAsInt());

            JsonArray ingredientes = new JsonArray();
            detalleVenta.add("ingredientesAplicados", ingredientes);
            JsonArray impuestosAplicados = new JsonArray();
            JsonArray imparr = objVentadetalle.get("impuestosAplicados").getAsJsonArray();
            float totalImp = 0;
            for (JsonElement elemp : imparr) {
                JsonObject objImp = elemp.getAsJsonObject();
                JsonObject impuesto = new JsonObject();
                impuesto.addProperty("identificadorImpuesto", objImp.get("identificadorImpuesto").getAsInt());
                impuesto.addProperty("nombreImpuesto", objImp.get("nombreImpuesto").getAsString());
                impuesto.addProperty("tipoImpuesto", objImp.get("tipoImpuesto").getAsString());
                impuesto.addProperty("valorImpAplicado", objImp.get("valorImpAplicado").getAsFloat());
                impuesto.addProperty("valorImpuestoAplicado", objImp.get("valorImpuestoAplicado").getAsFloat());
                totalImp += objImp.get("valorImpuestoAplicado").getAsFloat();
                impuestosAplicados.add(impuesto);
            }
            atributos.addProperty("cortecia", objVentadetalle.get("cortesia").getAsBoolean());
            atributos.addProperty("base", totalImp);
            atributos.addProperty("total", totalImp);
            atributos.addProperty("precio_unitario", detalleVenta.get("precioProducto").getAsFloat());
            atributos.addProperty("impuesto", totalImp);
            atributos.addProperty("precioProducto", detalleVenta.get("precioProducto").getAsFloat());
            atributos.addProperty("impuesto", totalImp);
            detalleVenta.add("atributos", atributos);
            detalleVenta.add("impuestosAplicados", impuestosAplicados);
            detalleVenta.add("atributos", atributos);
            detalleVenta.add("impuestosAplicados", impuestosAplicados);

            ventaDetalles.add(detalleVenta);
        }
        JsonArray mediosPagos = new JsonArray();
        JsonArray medioArr = json.get("pagos").getAsJsonArray();
        for (JsonElement jsonElement : medioArr) {
            JsonObject objPagos = jsonElement.getAsJsonObject();
            JsonObject medioPago = new JsonObject();
            medioPago.addProperty("descripcionMedio", objPagos.get("descripcionMedio").getAsString());
            medioPago.addProperty("identificacionMediosPagos", objPagos.get("identificacionMediosPagos").getAsInt());
            medioPago.addProperty("recibidoMedioPago", objPagos.get("recibidoMedioPago").getAsFloat());
            medioPago.addProperty("totalMedioPago", objPagos.get("totalMedioPago").getAsFloat());
            medioPago.addProperty("vueltoMedioPago", objPagos.get("vueltoMedioPago").getAsFloat());
            medioPago.addProperty("identificacionComprobante", objPagos.get("identificacionComprobante").getAsString());
            medioPago.addProperty("monedaLocal", objPagos.get("monedaLocal").getAsString());
            medioPago.addProperty("trm", objPagos.get("trm").getAsLong());
            mediosPagos.add(medioPago);
        }
        JsonObject dataVena = new JsonObject();
        dataVena.add("transaccion", venta);
        dataVena.add("detallesVenta", ventaDetalles);
        dataVena.add("mediosPagos", mediosPagos);

        NovusUtils.printLn(dataVena.toString());
        TreeMap<String, String> header = new TreeMap<>();
        InetAddress ip = null;
        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            Logger.getLogger(PedidoFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        SimpleDateFormat sdfISO = new SimpleDateFormat(NovusConstante.FORMAT_FULL_DATE_ISO);

        MovimientosDao mdao = new MovimientosDao();
        JsonObject respuesta = mdao.procesarVentasKiosco(json, NovusConstante.TIPO_VENTA_CAN, NovusConstante.ESTADO_MOVIMIENTO_CAN);
        int codigo = respuesta.get("codigo").getAsInt();

        if (codigo == 0 || codigo != 200) {
            EquipoDao e = new EquipoDao();
            try {
                e.guardarTransmisionKioscoCanastilla(Main.credencial, json.toString(), url, NovusConstante.POST);
            } catch (DAOException ex) {
                Logger.getLogger(PedidoFacade.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return json;
    }

    public JsonObject sendKcoCortecia(JsonObject json, boolean cortesia) {
        NovusUtils.printLn("sendKcoCortecia");
        String url = NovusConstante.SECURE_CENTRAL_POINT_SUBIR_VENTA_KIOSCO;
        JsonObject venta = new JsonObject();
        JsonObject objVenta = json.get("venta").getAsJsonObject();
        venta.addProperty("identificadorNegocio", Integer.parseInt(objVenta.get("identificadorNegocio").getAsString()));
        venta.addProperty("identificacionEstacion", objVenta.get("identificacionEstacion").getAsInt());
        venta.addProperty("codigoEstacion", objVenta.get("codigoEstacion").getAsString());
        venta.addProperty("prefijo", objVenta.get("prefijo").getAsString());
        venta.addProperty("nombreEstacion", objVenta.get("nombreEstacion").getAsString());
        venta.addProperty("aliasEstacion", objVenta.get("aliasEstacion").getAsString());
        venta.addProperty("identificadorEstacion", objVenta.get("identificadorEstacion").getAsString());
        venta.addProperty("identificadorTicketVenta", objVenta.get("consecutivoActual").getAsLong());
        venta.addProperty("idTransaccionVenta", objVenta.get("idTransaccionVenta").getAsLong());
        venta.addProperty("identificadorTicket", objVenta.get("consecutivo_id").getAsLong());
        venta.addProperty("fechaTransaccion", objVenta.get("fechaTransaccion").getAsString());
        venta.addProperty("identificadorPromotor", objVenta.get("persona_id").getAsInt());
        venta.addProperty("nombresPromotor", objVenta.get("nombresPromotor").getAsString());
        venta.addProperty("apellidosPromotor", objVenta.get("apellidosPromotor").getAsString());
        venta.addProperty("identificacionPromotor", objVenta.get("identificacionPromotor").getAsString());
        venta.addProperty("identificadorPersona", objVenta.get("identificadorPersona").getAsInt());
        venta.addProperty("nombresPersona", objVenta.get("nombresPersona").getAsString());
        venta.addProperty("apellidosPersona", objVenta.get("apellidosPersona").getAsString());
        venta.addProperty("identificadorProveedor", objVenta.get("identificadorProveedor").getAsInt());
        venta.addProperty("identificadorBodega", objVenta.get("identificadorBodega").getAsInt());
        venta.addProperty("nombresBodega", objVenta.get("nombresBodega").getAsString());
        venta.addProperty("codigoBodega", objVenta.get("codigoBodega").getAsString());
        venta.addProperty("costoTotal", objVenta.get("costoTotal").getAsFloat());
        venta.addProperty("ventaTotal", objVenta.get("ventaTotal").getAsFloat());
        venta.addProperty("descuentoTotal", objVenta.get("descuentoTotal").getAsFloat());
        venta.addProperty("impuestoTotal", objVenta.get("impuestoTotal").getAsFloat());
        venta.addProperty("identificadorEquipo", objVenta.get("identificadorEquipo").getAsInt());
        venta.addProperty("impresoTiquete", objVenta.get("impresoTiquete").getAsString());
        venta.addProperty("usoDolar", objVenta.get("usoDolar").getAsFloat());
        venta.addProperty("identificadorJornada", objVenta.get("identificadorJornada").getAsLong());
        venta.addProperty("identificadorOrigen", objVenta.get("identificadorOrigen").getAsLong());
        JsonArray arr = json.get("detallesVenta").getAsJsonArray();
        JsonArray ventaDetalles = new JsonArray();
        for (JsonElement elemt : arr) {
            JsonObject objVentadetalle = elemt.getAsJsonObject();
            JsonObject detalleVenta = new JsonObject();
            detalleVenta.addProperty("idTransaccionVentaDetalle", objVentadetalle.get("idTransaccionVentaDetalle").getAsInt());
            detalleVenta.addProperty("idTransaccionDetalleVenta", objVentadetalle.get("idTransaccionDetalleVenta").getAsInt());
            detalleVenta.addProperty("identificadorProducto", objVentadetalle.get("identificadorProducto").getAsInt());
            detalleVenta.addProperty("nombreProducto", objVentadetalle.get("nombreProducto").getAsString());
            detalleVenta.addProperty("identificacionProducto", objVentadetalle.get("identificacionProducto").getAsString());
            detalleVenta.addProperty("fechaTransaccion", objVentadetalle.get("fechaTransaccion").getAsString());
            detalleVenta.addProperty("cantidadVenta", objVentadetalle.get("cantidadVenta").getAsInt());
            detalleVenta.addProperty("identificadorUnidad", objVentadetalle.get("identificadorUnidad").getAsInt());
            detalleVenta.addProperty("costoProducto", objVentadetalle.get("costoProducto").getAsFloat());
            detalleVenta.addProperty("precioProducto", objVentadetalle.get("precioProducto").getAsFloat());
            detalleVenta.addProperty("identificadorDescuento", objVentadetalle.get("identificadorDescuento").getAsInt());
            detalleVenta.addProperty("descuentoTotal", objVentadetalle.get("descuentoTotal").getAsFloat());
            detalleVenta.addProperty("subTotalVenta", objVentadetalle.get("subTotalVenta").getAsFloat());
            JsonObject atributos = new JsonObject();
            JsonObject objAtrib = objVentadetalle.get("atributos").getAsJsonObject();
            atributos.addProperty("categoriaId", objAtrib.get("categoriaId").getAsInt());
            atributos.addProperty("categoriaDescripcion", objAtrib.get("categoriaDescripcion").getAsString());
            atributos.addProperty("tipo", objAtrib.get("tipo").getAsInt());
            JsonArray ingredientes = new JsonArray();

            detalleVenta.add("ingredientesAplicados", ingredientes);
            JsonArray impuestosAplicados = new JsonArray();
            JsonArray imparr = objVentadetalle.get("impuestosAplicados").getAsJsonArray();
            float totalImp = 0;
            for (JsonElement elemp : imparr) {
                JsonObject objImp = elemp.getAsJsonObject();
                JsonObject impuesto = new JsonObject();
                impuesto.addProperty("identificadorImpuesto", objImp.get("identificadorImpuesto").getAsInt());
                impuesto.addProperty("nombreImpuesto", objImp.get("nombreImpuesto").getAsString());
                impuesto.addProperty("tipoImpuesto", objImp.get("tipoImpuesto").getAsString());
                impuesto.addProperty("valorImpAplicado", objImp.get("valorImpAplicado").getAsFloat());
                impuesto.addProperty("valorImpuestoAplicado", objImp.get("valorImpuestoAplicado").getAsFloat());
                totalImp += objImp.get("valorImpuestoAplicado").getAsFloat();
                detalleVenta.addProperty("precioProducto", detalleVenta.get("precioProducto").getAsFloat() + objImp.get("valorImpuestoAplicado").getAsFloat());
                impuestosAplicados.add(impuesto);
            }
            atributos.addProperty("cortecia", objVentadetalle.get("cortesia").getAsBoolean());
            atributos.addProperty("base", totalImp);
            atributos.addProperty("total", totalImp);
            atributos.addProperty("precio_unitario", detalleVenta.get("precioProducto").getAsFloat());
            atributos.addProperty("impuesto", totalImp);
            atributos.addProperty("precioProducto", detalleVenta.get("precioProducto").getAsFloat());
            atributos.addProperty("impuesto", totalImp);
            detalleVenta.add("atributos", atributos);
            detalleVenta.add("impuestosAplicados", impuestosAplicados);

            ventaDetalles.add(detalleVenta);
        }

        JsonArray mediosPagos = new JsonArray();
        JsonArray medioArr = json.get("pagos").getAsJsonArray();
        for (JsonElement jsonElement : medioArr) {
            JsonObject objPagos = jsonElement.getAsJsonObject();
            JsonObject medioPago = new JsonObject();
            medioPago.addProperty("descripcionMedio", objPagos.get("descripcionMedio").getAsString());
            medioPago.addProperty("identificacionMediosPagos", objPagos.get("identificacionMediosPagos").getAsInt());
            medioPago.addProperty("recibidoMedioPago", objPagos.get("recibidoMedioPago").getAsFloat());
            medioPago.addProperty("totalMedioPago", objPagos.get("totalMedioPago").getAsFloat());
            medioPago.addProperty("vueltoMedioPago", objPagos.get("vueltoMedioPago").getAsFloat());
            medioPago.addProperty("identificacionComprobante", objPagos.get("identificacionComprobante").getAsString());
            medioPago.addProperty("monedaLocal", objPagos.get("monedaLocal").getAsString());
            medioPago.addProperty("trm", objPagos.get("trm").getAsLong());
            mediosPagos.add(medioPago);
        }
        JsonObject dataVena = new JsonObject();
        dataVena.add("transaccion", venta);
        dataVena.add("detallesVenta", ventaDetalles);
        dataVena.add("mediosPagos", mediosPagos);

        NovusUtils.printLn(dataVena.toString());
        TreeMap<String, String> header = new TreeMap<>();
        InetAddress ip = null;
        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            Logger.getLogger(PedidoFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        SimpleDateFormat sdfISO = new SimpleDateFormat(NovusConstante.FORMAT_FULL_DATE_ISO);

        MovimientosDao mdao = new MovimientosDao();
        JsonObject respuesta = mdao.procesarVentasKiosco(json, NovusConstante.TIPO_VENTA_KCO, NovusConstante.ESTADO_MOVIMIENTO_KCO);
        int codigo = respuesta.get("codigo").getAsInt();

        if (codigo == 0 || codigo != 200) {
            EquipoDao e = new EquipoDao();
            try {
                e.guardarTransmisionKioscoCanastilla(Main.credencial, json.toString(), url, NovusConstante.POST);
            } catch (DAOException ex) {
                Logger.getLogger(PedidoFacade.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return json;
    }


    public JsonObject sendVenta(MovimientosBean movimiento, boolean cortesia) throws DAOException {
        NovusUtils.printLn("sendVenta");
        SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
        SimpleDateFormat sdfISO = new SimpleDateFormat(NovusConstante.FORMAT_FULL_DATE_ISO);
        JsonObject response = null;

        MovimientosDao mdao = new MovimientosDao();
        EquipoDao dao = new EquipoDao();
        try {
            EmpresaBean empresa = dao.findEmpresa(Main.credencial);
            movimiento.setEmpresa(empresa);
        } catch (DAOException ex) {
            Logger.getLogger(PedidoFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

        String url = NovusConstante.SECURE_CENTRAL_POINT_SUBIR_VENTA;
        try {

            JsonObject movimientoJson = new JsonObject();
        
            boolean isCombustible = false;
            BodegaBean bodega = null;
            String finalidad = "C";
            for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
                MovimientosDetallesBean value = entry.getValue();
                if (value.getIngredientes() != null && !value.getIngredientes().isEmpty()) {
                    for (ProductoBean ingrediente : value.getIngredientes()) {
                        bodega = dao.findBodegaByProductoId(finalidad, ingrediente.getId());
                    }
                } else {
                    bodega = dao.findBodegaByProductoId(finalidad, value.getProductoId());
                }
            }
            movimiento.setBodega(bodega);

            if (!isCombustible) {
                movimientoJson.addProperty("identificadorNegocio", movimiento.getEmpresa().getNegocioId());
                movimientoJson.addProperty("identificacionEstacion", movimiento.getEmpresasId());
                movimientoJson.addProperty("codigoEstacion", movimiento.getEmpresa().getCodigo());
                movimientoJson.addProperty("prefijo", movimiento.getConsecutivo().getPrefijo());
                movimientoJson.addProperty("nombreEstacion", movimiento.getEmpresa().getRazonSocial());
                movimientoJson.addProperty("aliasEstacion", movimiento.getEmpresa().getAlias());
                movimientoJson.addProperty("identificadorEstacion", movimiento.getEmpresa().getNit());
                movimientoJson.addProperty("identificadorTicketVenta",
                        movimiento.getConsecutivo().getConsecutivo_actual());
                movimientoJson.addProperty("identificadorTicket", movimiento.getConsecutivo().getId());
                movimientoJson.addProperty("idTransaccionVenta", movimiento.getId());
                movimientoJson.addProperty("fechaTransaccion", sdf.format(movimiento.getFecha().getTime()));
                movimientoJson.addProperty("identificadorPromotor", movimiento.getPersonaId());
                movimientoJson.addProperty("nombresPromotor", movimiento.getPersonaNombre());
                movimientoJson.addProperty("apellidosPromotor",
                        movimiento.getPersonaApellidos() != null && !movimiento.getPersonaApellidos().trim().equals("")
                        ? movimiento.getPersonaApellidos()
                        : movimiento.getPersonaNombre());
                movimientoJson.addProperty("identificacionPromotor", movimiento.getPersonaNit());
                movimientoJson.addProperty("identificadorPersona", movimiento.getClienteId());
                movimientoJson.addProperty("identificacionPersona", movimiento.getClienteNit());
                movimientoJson.addProperty("nombresPersona", movimiento.getClienteNombre());
                movimientoJson.addProperty("apellidosPersona", " ");
                movimientoJson.addProperty("identificadorProveedor", 0);
                movimientoJson.addProperty("identificadorBodega", movimiento.getBodega().getId());
                movimientoJson.addProperty("nombresBodega", movimiento.getBodega().getDescripcion());
                movimientoJson.addProperty("codigoBodega", movimiento.getBodega().getCodigo());
                movimientoJson.addProperty("costoTotal", movimiento.getCostoTotal());
                movimientoJson.addProperty("ventaTotal", movimiento.getVentaTotal());
                movimientoJson.addProperty("descuentoTotal", movimiento.getDescuentoTotal());
                movimientoJson.addProperty("impuestoTotal", movimiento.getImpuestoTotal());
                if (consumoPropio) {
                    movimientoJson.addProperty("ventaTotal", movimiento.getImpuestoTotal());
                }
                movimientoJson.addProperty("identificadorEquipo", Main.credencial.getEquipos_id());
                movimientoJson.addProperty("impresoTiquete", "S");
                movimientoJson.addProperty("usoDolar", 0);
                movimientoJson.addProperty("identificadorJornada", movimiento.getGrupoJornadaId());
                movimientoJson.addProperty("identificadorOrigen", 0);

                JsonObject atributos = new JsonObject();
                atributos.addProperty("fidelizada", 'N');
                atributos.addProperty("tipo_negocio", NovusUtils.idTipoNegocio(NovusConstante.PARAMETER_CAN));
                movimientoJson.add("atributos", atributos);

                JsonArray detallesVentasArray = new JsonArray();
                float descuento = 0;
                for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
                    Long key = entry.getKey();
                    MovimientosDetallesBean value = entry.getValue();
                    JsonObject detalleJson = new JsonObject();
                    if (value.getTipo() == NovusConstante.PRODUCTO_DESPACHO_COMBUSTIBLE_VENTA_MIXTA) {
                        JsonObject movimientoAtributosJson = new JsonObject();
                        movimientoAtributosJson.addProperty("isMixta", true);
                        movimientoJson.add("atributos", movimientoAtributosJson);
                        UpadteByEstateMovimientoUseCase updateUseCase =
                        new UpadteByEstateMovimientoUseCase(value.getId(), "034002");
                        boolean actualizado = updateUseCase.execute();
                        if (!actualizado) {
                            NovusUtils.printLn("⚠️ No se pudo actualizar estado del movimiento: " + value.getId());
                        }   
                    }
                    detalleJson.addProperty("idTransaccionVentaDetalle", value.getRemotoId());
                    detalleJson.addProperty("idTransaccionDetalleVenta", 0);
                    detalleJson.addProperty("identificadorProducto", value.getProductoId());
                    detalleJson.addProperty("nombreProducto", value.getDescripcion()); // CENTRALIZADOR
                    detalleJson.addProperty("identificacionProducto", value.getPlu());
                    detalleJson.addProperty("fechaTransaccion", sdf.format(movimiento.getFecha().getTime()));
                    detalleJson.addProperty("cantidadVenta", value.getCantidadUnidad());
                    detalleJson.addProperty("identificadorUnidad", value.getUnidades_medida_id());// DERRUMBAR
                    detalleJson.addProperty("costoProducto", value.getCosto());
                    detalleJson.addProperty("precioProducto", value.getPrecio());
                    detalleJson.addProperty("identificadorDescuento", value.getDescuentoId());
                    detalleJson.addProperty("descuentoTotal", value.getDescuentoProducto());
                    detalleJson.addProperty("subTotalVenta", value.getSubtotal());
                    JsonObject jsonAtributos = new JsonObject();
                    jsonAtributos.addProperty("categoriaId", value.getCategoriaId());
                    jsonAtributos.addProperty("categoriaDescripcion", value.getCategoriaDesc());
                    jsonAtributos.addProperty("tipo", value.getTipo());
                    if (value.getTipo() == NovusConstante.PRODUCTO_DESPACHO_COMBUSTIBLE_VENTA_MIXTA) {
                        jsonAtributos.addProperty("unidad", value.getUnidades_medida());
                    }
                    detalleJson.add("atributos", jsonAtributos);
                    JsonArray ingredientesArray = new JsonArray();
                    for (ProductoBean ingrediente : value.getIngredientes()) {
                        JsonObject ing = new JsonObject();
                        ing.addProperty("identificadorProducto", ingrediente.getId());
                        ing.addProperty("cantidadVenta",
                                ingrediente.getProducto_compuesto_cantidad() * value.getCantidadUnidad());
                        ing.addProperty("costo", ingrediente.getProducto_compuesto_costo() * value.getCantidadUnidad()
                                * ingrediente.getProducto_compuesto_cantidad());
                        ingredientesArray.add(ing);
                    }
                    detalleJson.add("ingredientesAplicados", ingredientesArray);
                    JsonArray impuestosArray = new JsonArray();
                    if (!value.getImpuestos().isEmpty()) {
                        float impuestoConsumoPropio = 0;
                        for (ImpuestosBean impuesto : value.getImpuestos()) {
                            JsonObject imp = new JsonObject();
                            if (impuesto.getPorcentaje_valor().equals("$")) {
                                descuento += impuesto.getCalculado();
                            }
                            imp.addProperty("identificadorImpuesto", impuesto.getId());
                            imp.addProperty("nombreImpuesto", impuesto.getDescripcion());
                            imp.addProperty("tipoImpuesto", impuesto.getPorcentaje_valor());
                            imp.addProperty("valorImpAplicado", impuesto.getValor());
                            imp.addProperty("valorImpuestoAplicado", impuesto.getCalculado());
                            if (impuesto.getPorcentaje_valor().equals("%")) {
                                impuestoConsumoPropio = impuestoConsumoPropio + impuesto.getCalculado();
                            }
                            if (cortesia) {
                                descuento = descuento + impuesto.getCalculado();
                            }
                            if (!consumoPropio) {
                                impuestosArray.add(imp);
                            }
                        }
                        if (consumoPropio) {
                            detalleJson.addProperty("subTotalVenta", impuestoConsumoPropio);
                        }
                    } else {
                        if (consumoPropio) {
                            detalleJson.addProperty("subTotalVenta", value.getSubtotal());
                        }
                    }

                    detalleJson.add("impuestosAplicados", impuestosArray);
                    detallesVentasArray.add(detalleJson);
                }

                JsonArray MediosPagosArray = new JsonArray();

                for (Map.Entry<Long, MediosPagosBean> entry : movimiento.getMediosPagos().entrySet()) {
                    Long key = entry.getKey();
                    MediosPagosBean mediopago = entry.getValue();
                    JsonObject objMedioPago = new JsonObject();

                    objMedioPago.addProperty("descripcionMedio", mediopago.getDescripcion());
                    objMedioPago.addProperty("identificacionMediosPagos", mediopago.getId());
                    if (consumoPropio) {
                        objMedioPago.addProperty("recibidoMedioPago", movimiento.getImpuestoTotal());
                        objMedioPago.addProperty("totalMedioPago", movimiento.getImpuestoTotal());
                    } else {
                        objMedioPago.addProperty("recibidoMedioPago", mediopago.getRecibido());
                        objMedioPago.addProperty("totalMedioPago", mediopago.getValor());
                    }
                    objMedioPago.addProperty("vueltoMedioPago", mediopago.getCambio());
                    objMedioPago.addProperty("identificacionComprobante",
                            mediopago.getVoucher() != null ? mediopago.getVoucher() : "");
                    objMedioPago.addProperty("monedaLocal", "S");
                    objMedioPago.addProperty("trm", 0);
                    MediosPagosArray.add(objMedioPago);
                }

                // Bodega de donde se genera el movimiento
                JsonObject json = new JsonObject();
//                float venta = movimientoJson.get("ventaTotal").getAsFloat();
//                venta = venta - descuento;
//                movimientoJson.remove("ventaTotal");
//                movimientoJson.addProperty("ventaTotal", venta);
                json.add("transaccion", movimientoJson);
                json.add("detallesVenta", detallesVentasArray);
                json.add("mediosPagos", MediosPagosArray);

                NovusUtils.printLn(json.toString());
                TreeMap<String, String> header = new TreeMap<>();
                NovusUtils.printLn(" Procesando Movimiento Canastilla ");
                JsonObject respuesta = mdao.procesarVentasKiosco(json, NovusConstante.TIPO_VENTA_CAN, NovusConstante.ESTADO_MOVIMIENTO_CAN);
                int codigo = respuesta.get("codigo").getAsInt();

                if (codigo == 0 || codigo != 200) {
                    EquipoDao e = new EquipoDao();
                    try {
                        e.guardarTransmisionKioscoCanastilla(Main.credencial, json.toString(), url, NovusConstante.POST);
                    } catch (DAOException ex) {
                        Logger.getLogger(PedidoFacade.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            } else {
                NovusUtils.printLn("PROCESAMIENTO VENTA");
                mdao = new MovimientosDao();
                mdao.createVentasCombustible(movimiento);
                response = new JsonObject();
            }

        } catch (Exception ex) {
            NovusUtils.printLn(ex.getMessage());
        }
        return response;
    }

//     public JsonObject sendVenta(MovimientosBean movimiento, boolean cortesia) throws DAOException {
//         NovusUtils.printLn("sendVenta");
//         SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
//         SimpleDateFormat sdfISO = new SimpleDateFormat(NovusConstante.FORMAT_FULL_DATE_ISO);
//         JsonObject response = null;

//         MovimientosDao mdao = new MovimientosDao();
//         EquipoDao dao = new EquipoDao();
//         try {
//             EmpresaBean empresa = dao.findEmpresa(Main.credencial);
//             movimiento.setEmpresa(empresa);
//         } catch (DAOException ex) {
//             Logger.getLogger(PedidoFacade.class.getName()).log(Level.SEVERE, null, ex);
//         }

//         String url = NovusConstante.SECURE_CENTRAL_POINT_SUBIR_VENTA;
//         try {

//             JsonObject movimientoJson = new JsonObject();
        
//             boolean isCombustible = false;
//             BodegaBean bodega = null;
//             String finalidad = "C";
//             for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
//                 MovimientosDetallesBean value = entry.getValue();
//                 if (value.getIngredientes() != null && !value.getIngredientes().isEmpty()) {
//                     for (ProductoBean ingrediente : value.getIngredientes()) {
//                         bodega = dao.findBodegaByProductoId(finalidad, ingrediente.getId());
//                     }
//                 } else {
//                     bodega = dao.findBodegaByProductoId(finalidad, value.getProductoId());
//                 }
//             }
//             movimiento.setBodega(bodega);

//             if (!isCombustible) {
//                 movimientoJson.addProperty("identificadorNegocio", movimiento.getEmpresa().getNegocioId());
//                 movimientoJson.addProperty("identificacionEstacion", movimiento.getEmpresasId());
//                 movimientoJson.addProperty("codigoEstacion", movimiento.getEmpresa().getCodigo());
//                 movimientoJson.addProperty("prefijo", movimiento.getConsecutivo().getPrefijo());
//                 movimientoJson.addProperty("nombreEstacion", movimiento.getEmpresa().getRazonSocial());
//                 movimientoJson.addProperty("aliasEstacion", movimiento.getEmpresa().getAlias());
//                 movimientoJson.addProperty("identificadorEstacion", movimiento.getEmpresa().getNit());
//                 movimientoJson.addProperty("identificadorTicketVenta",
//                         movimiento.getConsecutivo().getConsecutivo_actual());
//                 movimientoJson.addProperty("identificadorTicket", movimiento.getConsecutivo().getId());
//                 movimientoJson.addProperty("idTransaccionVenta", movimiento.getId());
//                 movimientoJson.addProperty("fechaTransaccion", sdf.format(movimiento.getFecha().getTime()));
//                 movimientoJson.addProperty("identificadorPromotor", movimiento.getPersonaId());
//                 movimientoJson.addProperty("nombresPromotor", movimiento.getPersonaNombre());
//                 movimientoJson.addProperty("apellidosPromotor",
//                         movimiento.getPersonaApellidos() != null && !movimiento.getPersonaApellidos().trim().equals("")
//                         ? movimiento.getPersonaApellidos()
//                         : movimiento.getPersonaNombre());
//                 movimientoJson.addProperty("identificacionPromotor", movimiento.getPersonaNit());
//                 movimientoJson.addProperty("identificadorPersona", movimiento.getClienteId());
//                 movimientoJson.addProperty("identificacionPersona", movimiento.getClienteNit());
//                 movimientoJson.addProperty("nombresPersona", movimiento.getClienteNombre());
//                 movimientoJson.addProperty("apellidosPersona", " ");
//                 movimientoJson.addProperty("identificadorProveedor", 0);
//                 movimientoJson.addProperty("identificadorBodega", movimiento.getBodega().getId());
//                 movimientoJson.addProperty("nombresBodega", movimiento.getBodega().getDescripcion());
//                 movimientoJson.addProperty("codigoBodega", movimiento.getBodega().getCodigo());
//                 movimientoJson.addProperty("costoTotal", movimiento.getCostoTotal());
//                 movimientoJson.addProperty("ventaTotal", movimiento.getVentaTotal());
//                 movimientoJson.addProperty("descuentoTotal", movimiento.getDescuentoTotal());
//                 movimientoJson.addProperty("impuestoTotal", movimiento.getImpuestoTotal());
//                 if (consumoPropio) {
//                     movimientoJson.addProperty("ventaTotal", movimiento.getImpuestoTotal());
//                 }
//                 movimientoJson.addProperty("identificadorEquipo", Main.credencial.getEquipos_id());
//                 movimientoJson.addProperty("impresoTiquete", "S");
//                 movimientoJson.addProperty("usoDolar", 0);
//                 movimientoJson.addProperty("identificadorJornada", movimiento.getGrupoJornadaId());
//                 movimientoJson.addProperty("identificadorOrigen", 0);

//                 JsonObject atributos = new JsonObject();
//                 atributos.addProperty("fidelizada", 'N');
//                 atributos.addProperty("tipo_negocio", NovusUtils.idTipoNegocio(NovusConstante.PARAMETER_CAN));
//                 movimientoJson.add("atributos", atributos);

//                 JsonArray detallesVentasArray = new JsonArray();
//                 float descuento = 0;
//                 for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
//                     Long key = entry.getKey();
//                     MovimientosDetallesBean value = entry.getValue();
//                     JsonObject detalleJson = new JsonObject();
//                     if (value.getTipo() == NovusConstante.PRODUCTO_DESPACHO_COMBUSTIBLE_VENTA_MIXTA) {
//                         JsonObject movimientoAtributosJson = new JsonObject();
//                         movimientoAtributosJson.addProperty("isMixta", true);
//                         movimientoJson.add("atributos", movimientoAtributosJson);
//                         mdao.destruirVentaDespachadaCombustible(value.getId());
//                     }
//                     detalleJson.addProperty("idTransaccionVentaDetalle", value.getRemotoId());
//                     detalleJson.addProperty("idTransaccionDetalleVenta", 0);
//                     detalleJson.addProperty("identificadorProducto", value.getProductoId());
//                     detalleJson.addProperty("nombreProducto", value.getDescripcion()); // CENTRALIZADOR
//                     detalleJson.addProperty("identificacionProducto", value.getPlu());
//                     detalleJson.addProperty("fechaTransaccion", sdf.format(movimiento.getFecha().getTime()));
//                     detalleJson.addProperty("cantidadVenta", value.getCantidadUnidad());
//                     detalleJson.addProperty("identificadorUnidad", value.getUnidades_medida_id());// DERRUMBAR
//                     detalleJson.addProperty("costoProducto", value.getCosto());
//                     detalleJson.addProperty("precioProducto", value.getPrecio());
//                     detalleJson.addProperty("identificadorDescuento", value.getDescuentoId());
//                     detalleJson.addProperty("descuentoTotal", value.getDescuentoProducto());
//                     detalleJson.addProperty("subTotalVenta", value.getSubtotal());
//                     JsonObject jsonAtributos = new JsonObject();
//                     jsonAtributos.addProperty("categoriaId", value.getCategoriaId());
//                     jsonAtributos.addProperty("categoriaDescripcion", value.getCategoriaDesc());
//                     jsonAtributos.addProperty("tipo", value.getTipo());
//                     if (value.getTipo() == NovusConstante.PRODUCTO_DESPACHO_COMBUSTIBLE_VENTA_MIXTA) {
//                         jsonAtributos.addProperty("unidad", value.getUnidades_medida());
//                     }
//                     detalleJson.add("atributos", jsonAtributos);
//                     JsonArray ingredientesArray = new JsonArray();
//                     for (ProductoBean ingrediente : value.getIngredientes()) {
//                         JsonObject ing = new JsonObject();
//                         ing.addProperty("identificadorProducto", ingrediente.getId());
//                         ing.addProperty("cantidadVenta",
//                                 ingrediente.getProducto_compuesto_cantidad() * value.getCantidadUnidad());
//                         ing.addProperty("costo", ingrediente.getProducto_compuesto_costo() * value.getCantidadUnidad()
//                                 * ingrediente.getProducto_compuesto_cantidad());
//                         ingredientesArray.add(ing);
//                     }
//                     detalleJson.add("ingredientesAplicados", ingredientesArray);
//                     JsonArray impuestosArray = new JsonArray();
//                     if (!value.getImpuestos().isEmpty()) {
//                         float impuestoConsumoPropio = 0;
//                         for (ImpuestosBean impuesto : value.getImpuestos()) {
//                             JsonObject imp = new JsonObject();
//                             if (impuesto.getPorcentaje_valor().equals("$")) {
//                                 descuento += impuesto.getCalculado();
//                             }
//                             imp.addProperty("identificadorImpuesto", impuesto.getId());
//                             imp.addProperty("nombreImpuesto", impuesto.getDescripcion());
//                             imp.addProperty("tipoImpuesto", impuesto.getPorcentaje_valor());
//                             imp.addProperty("valorImpAplicado", impuesto.getValor());
//                             imp.addProperty("valorImpuestoAplicado", impuesto.getCalculado());
//                             if (impuesto.getPorcentaje_valor().equals("%")) {
//                                 impuestoConsumoPropio = impuestoConsumoPropio + impuesto.getCalculado();
//                             }
//                             if (cortesia) {
//                                 descuento = descuento + impuesto.getCalculado();
//                             }
//                             if (!consumoPropio) {
//                                 impuestosArray.add(imp);
//                             }
//                         }
//                         if (consumoPropio) {
//                             detalleJson.addProperty("subTotalVenta", impuestoConsumoPropio);
//                         }
//                     } else {
//                         if (consumoPropio) {
//                             detalleJson.addProperty("subTotalVenta", value.getSubtotal());
//                         }
//                     }

//                     detalleJson.add("impuestosAplicados", impuestosArray);
//                     detallesVentasArray.add(detalleJson);
//                 }

//                 JsonArray MediosPagosArray = new JsonArray();

//                 for (Map.Entry<Long, MediosPagosBean> entry : movimiento.getMediosPagos().entrySet()) {
//                     Long key = entry.getKey();
//                     MediosPagosBean mediopago = entry.getValue();
//                     JsonObject objMedioPago = new JsonObject();

//                     objMedioPago.addProperty("descripcionMedio", mediopago.getDescripcion());
//                     objMedioPago.addProperty("identificacionMediosPagos", mediopago.getId());
//                     if (consumoPropio) {
//                         objMedioPago.addProperty("recibidoMedioPago", movimiento.getImpuestoTotal());
//                         objMedioPago.addProperty("totalMedioPago", movimiento.getImpuestoTotal());
//                     } else {
//                         objMedioPago.addProperty("recibidoMedioPago", mediopago.getRecibido());
//                         objMedioPago.addProperty("totalMedioPago", mediopago.getValor());
//                     }
//                     objMedioPago.addProperty("vueltoMedioPago", mediopago.getCambio());
//                     objMedioPago.addProperty("identificacionComprobante",
//                             mediopago.getVoucher() != null ? mediopago.getVoucher() : "");
//                     objMedioPago.addProperty("monedaLocal", "S");
//                     objMedioPago.addProperty("trm", 0);
//                     MediosPagosArray.add(objMedioPago);
//                 }

//                 // Bodega de donde se genera el movimiento
//                 JsonObject json = new JsonObject();
// //                float venta = movimientoJson.get("ventaTotal").getAsFloat();
// //                venta = venta - descuento;
// //                movimientoJson.remove("ventaTotal");
// //                movimientoJson.addProperty("ventaTotal", venta);
//                 json.add("transaccion", movimientoJson);
//                 json.add("detallesVenta", detallesVentasArray);
//                 json.add("mediosPagos", MediosPagosArray);

//                 NovusUtils.printLn(json.toString());
//                 TreeMap<String, String> header = new TreeMap<>();
//                 NovusUtils.printLn(" Procesando Movimiento Canastilla ");
//                 JsonObject respuesta = mdao.procesarVentasKiosco(json, NovusConstante.TIPO_VENTA_CAN, NovusConstante.ESTADO_MOVIMIENTO_CAN);
//                 int codigo = respuesta.get("codigo").getAsInt();

//                 if (codigo == 0 || codigo != 200) {
//                     EquipoDao e = new EquipoDao();
//                     try {
//                         e.guardarTransmisionKioscoCanastilla(Main.credencial, json.toString(), url, NovusConstante.POST);
//                     } catch (DAOException ex) {
//                         Logger.getLogger(PedidoFacade.class.getName()).log(Level.SEVERE, null, ex);
//                     }
//                 }

//             } else {
//                 NovusUtils.printLn("PROCESAMIENTO VENTA");
//                 mdao = new MovimientosDao();
//                 mdao.createVentasCombustible(movimiento);
//                 response = new JsonObject();
//             }

//         } catch (SQLException s) {
//             NovusUtils.printLn(s.getMessage());
//         } catch (Exception ex) {
//             NovusUtils.printLn(ex.getMessage());
//         }
//         return response;
//     }

}
