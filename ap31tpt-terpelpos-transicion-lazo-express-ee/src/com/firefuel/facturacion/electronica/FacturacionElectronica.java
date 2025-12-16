/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.facturacion.electronica;

import com.application.useCases.consecutivos.GetResolucionesUseCase;
import com.application.useCases.facturacion.GetMontoMinimoFacturaElectronicaUseCase;
import com.application.useCases.movimientos.UpadteByEstateMovimientoUseCase;
import com.application.useCases.remision.GetMontoMinimoRemisionUseCase;
import com.application.useCases.unidades.ObtenerDescripcionUnidadUseCase;
import com.bean.BodegaBean;
import com.bean.ConsecutivoBean;
import com.bean.ImpuestosBean;
import com.bean.MediosPagosBean;
import com.bean.MovimientosBean;
import com.bean.MovimientosDetallesBean;
import com.bean.Notificador;
import com.bean.ProductoBean;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.EquipoDao;
import com.dao.FacturacionElectronicaDao;
import com.dao.MovimientosDao;
import com.firefuel.Main;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.application.useCases.wacherparametros.FindAllWacherParametrosUseCase;
import com.application.useCases.wacherparametros.FindByParameterUseCase;
import com.application.commons.CtWacherParametrosEnum;
/**
 *
 * @author Devitech
 */
public class FacturacionElectronica {

    int TIPO_DOCUMENTO = 0;
    String RESOLUCION = "";
    MovimientosDao mdao = new MovimientosDao();
    EquipoDao edao = new EquipoDao();
    MovimientosBean movimiento;
    JsonObject data = new JsonObject();
    ConfiguracionFE config = new ConfiguracionFE();
    boolean alertas;
    FindByParameterUseCase findByParameterUseCase = new FindByParameterUseCase(CtWacherParametrosEnum.CODIGO.getColumnName(), "REMISION");
    public void setMensaje(String mensaje, String icono, Notificador notificadorView) {
        JsonObject errorJson = new JsonObject();
        errorJson.addProperty("mensajeError", mensaje);
        if (icono.length() == 0) {
            errorJson.addProperty("icono", "/com/firefuel/resources/btBad.png");
        }
        errorJson.addProperty("icono", icono);
        errorJson.addProperty("habilitar", true);
        errorJson.addProperty("autoclose", true);
        errorJson.addProperty("alertas", alertas);
        notificadorView.send(errorJson);
    }

    public void setLoader(String mensaje, Notificador notificadorView) {
        JsonObject errorJson = new JsonObject();
        errorJson.addProperty("mensajeError", mensaje);
        errorJson.addProperty("icono", "/com/firefuel/resources/loader_fac.gif");
        errorJson.addProperty("habilitar", false);
        errorJson.addProperty("autoclose", false);
        notificadorView.send(errorJson);
    }

    // buscar consecutivos de FE y las bodegas de los producto
    // public boolean enviarFacturacion(MovimientosBean movimiento, Notificador notificadorView, boolean canastilla, int destino, boolean manual) {
    //     this.movimiento = movimiento;
    //     boolean existeConsecutivo = true;
    //     JsonObject data = null;
    //     if (manual) {
    //         TIPO_DOCUMENTO = 18;
    //     } else {
    //         TIPO_DOCUMENTO = 31;
    //     }

    //     if (canastilla) {
    //         RESOLUCION = "CAN";
    //     } else if (Main.SIN_SURTIDOR) {
    //         RESOLUCION = "CDL";
    //     } else {
    //         RESOLUCION = "KSC";
    //     }
    //     String finalidad = "";

    //     Long id = null;
    //     for (Map.Entry<Long, MovimientosDetallesBean> entry : this.movimiento.getDetalles().entrySet()) {
    //         MovimientosDetallesBean value = entry.getValue();
    //         if (value.getIngredientes() != null && !value.getIngredientes().isEmpty()) {
    //             for (ProductoBean ingrediente : value.getIngredientes()) {
    //                 id = ingrediente.getId();
    //                 break;
    //             }
    //         } else {
    //             id = value.getProductoId();
    //             break;
    //         }
    //     }
    //     try {

    //         MovimientosDao movi = new MovimientosDao();

    //         //Consulta de consecutivos FE
    //         ConsecutivoBean consecutivo = movi.getPrefijo(TIPO_DOCUMENTO, RESOLUCION);

    //         //busqueda de la bodegas
    //         switch (RESOLUCION) {
    //             case "KSC":
    //             case "CDL":
    //                 finalidad = "K";
    //                 break;
    //             case "CAN":
    //                 finalidad = "C";
    //                 break;
    //             default:
    //                 NovusUtils.printLn("no se encontró esta bodega");
    //         }
    //         BodegaBean bedegas = edao.findBodegaByProductoId(finalidad, id);
    //         this.movimiento.setBodega(bedegas);
    //         if (!mdao.remisionActiva()) {
    //             if (consecutivo != null) {
    //                 this.movimiento.setConsecutivo_fe(consecutivo);
    //             } else {
    //                 alertas = true;
    //                 setMensaje("NO HAY CONSECUTIVOS DE FACTURACION ELECTRONICA", "/com/firefuel/resources/btBad.png", notificadorView);
    //                 existeConsecutivo = false;

    //             }
    //         }
    //     } catch (DAOException e) {
    //         NovusUtils.printLn("error bd  -> " + e);
    //         //
    //     }

    //     return existeConsecutivo;
    // }

       // buscar consecutivos de FE y las bodegas de los producto
    public boolean enviarFacturacion(MovimientosBean movimiento, Notificador notificadorView, boolean canastilla, int destino, boolean manual) {
        this.movimiento = movimiento;
        boolean existeConsecutivo = true;
        JsonObject data = null;
        if (manual) {
            TIPO_DOCUMENTO = 18;
        } else {
            TIPO_DOCUMENTO = 31;
        }

        if (canastilla) {
            RESOLUCION = "CAN";
        } else if (Main.SIN_SURTIDOR) {
            RESOLUCION = "CDL";
        } else {
            RESOLUCION = "KSC";
        }
        String finalidad = "";

        Long id = null;
        for (Map.Entry<Long, MovimientosDetallesBean> entry : this.movimiento.getDetalles().entrySet()) {
            MovimientosDetallesBean value = entry.getValue();
            if (value.getIngredientes() != null && !value.getIngredientes().isEmpty()) {
                for (ProductoBean ingrediente : value.getIngredientes()) {
                    id = ingrediente.getId();
                    break;
                }
            } else {
                id = value.getProductoId();
                break;
            }
        }
        try {

            MovimientosDao movi = new MovimientosDao();

            //Consulta de consecutivos FE
            ConsecutivoBean consecutivo = movi.getPrefijo(TIPO_DOCUMENTO, RESOLUCION);

            //busqueda de la bodegas
            switch (RESOLUCION) {
                case "KSC":
                case "CDL":
                    finalidad = "K";
                    break;
                case "CAN":
                    finalidad = "C";
                    break;
                default:
                    NovusUtils.printLn("no se encontró esta bodega");
            }
            BodegaBean bedegas = edao.findBodegaByProductoId(finalidad, id);
            this.movimiento.setBodega(bedegas);
            if (!findByParameterUseCase.execute()) {
                if (consecutivo != null) {
                    this.movimiento.setConsecutivo_fe(consecutivo);
                } else {
                    alertas = true;
                    setMensaje("NO HAY CONSECUTIVOS DE FACTURACION ELECTRONICA", "/com/firefuel/resources/btBad.png", notificadorView);
                    existeConsecutivo = false;

                }
            }
        } catch (DAOException e) {
            NovusUtils.printLn("error bd  -> " + e);
            //
        }

        return existeConsecutivo;
    }


    private boolean validarTiempo(int hora, int minuto) {
        boolean activo = true;
        if (hora == 0 && minuto == 0) {
            activo = false;
        }
        return activo;
    }

    public JsonObject generarJsonFE(boolean consumopropio) {
        double totalConsumoPropio = 0;

        SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
        FacturacionElectronicaDao facturacionElectronicaDao = new FacturacionElectronicaDao();

        JsonObject movimientoJson = new JsonObject();
        double totalImpuestoSinImpoconsumo = 0;
        movimientoJson.addProperty("identificadorNegocio", movimiento.getEmpresa().getNegocioId());
        movimientoJson.addProperty("identificacionEstacion", movimiento.getEmpresasId());
        movimientoJson.addProperty("codigoEstacion", movimiento.getEmpresa().getCodigo());
        movimientoJson.addProperty("prefijo", findByParameterUseCase.execute() ? "" : movimiento.getConsecutivo_fe().getPrefijo_fe());
        movimientoJson.addProperty("consecutivoActual", findByParameterUseCase.execute() ? mdao.numeroRemision() + 1 : movimiento.getConsecutivo_fe().getConsecutivo_actual_fe());
        movimientoJson.addProperty("consecutivoInicial", findByParameterUseCase.execute() ? mdao.numeroRemision() + 1 : movimiento.getConsecutivo_fe().getConsecutivo_inicial_fe());
        movimientoJson.addProperty("consecutivoFinal", findByParameterUseCase.execute() ? mdao.numeroRemision() + 1 : movimiento.getConsecutivo_fe().getConsecutivo_final_fe());
        movimientoJson.addProperty("operacion", movimiento.getOperacionId());
        movimientoJson.addProperty("consecutivo_id", findByParameterUseCase.execute() ? mdao.numeroRemision() : movimiento.getConsecutivo_fe().getId_fe());
        movimientoJson.addProperty("persona_id", movimiento.getPersonaId());
        movimientoJson.addProperty("persona_nit", movimiento.getPersonaNit());
        movimientoJson.addProperty("nombreEstacion", movimiento.getEmpresa().getRazonSocial());
        movimientoJson.addProperty("aliasEstacion", movimiento.getEmpresa().getAlias());
        movimientoJson.addProperty("identificadorEstacion", movimiento.getEmpresa().getNit());
        movimientoJson.addProperty("identificadorTicketVenta", findByParameterUseCase.execute() ? mdao.numeroRemision() + 1 : movimiento.getConsecutivo_fe().getConsecutivo_actual_fe());
        movimientoJson.addProperty("identificadorTicket", findByParameterUseCase.execute() ? mdao.numeroRemision() : movimiento.getConsecutivo_fe().getId_fe());
        movimientoJson.addProperty("idTransaccionVenta", movimiento.getId());
        movimientoJson.addProperty("fechaTransaccion", sdf.format(movimiento.getFecha().getTime()));
        movimientoJson.addProperty("identificadorPromotor", movimiento.getPersonaId());
        movimientoJson.addProperty("nombresPromotor", movimiento.getPersonaNombre());
        movimientoJson.addProperty("apellidosPromotor", movimiento.getPersonaApellidos() != null && !movimiento.getPersonaApellidos().trim().equals("") ? movimiento.getPersonaApellidos() : movimiento.getPersonaNombre());
        movimientoJson.addProperty("identificacionPromotor", movimiento.getPersonaNit());
        movimientoJson.addProperty("identificadorPersona", movimiento.getClienteId());
        movimientoJson.addProperty("identificacionPersona", movimiento.getClienteNit());
        movimientoJson.addProperty("nombresPersona", movimiento.getClienteNombre());
        movimientoJson.addProperty("apellidosPersona", " ");
        movimientoJson.addProperty("identificadorProveedor", 0);
        movimientoJson.addProperty("consumo_propio", consumopropio);
        movimientoJson.addProperty("identificadorBodega", movimiento.getBodega().getId());
        movimientoJson.addProperty("nombresBodega", movimiento.getBodega().getDescripcion());
        movimientoJson.addProperty("codigoBodega", movimiento.getBodega().getCodigo());

        movimientoJson.addProperty("costoTotal", movimiento.getCostoTotal());
        movimientoJson.addProperty("ventaTotal", movimiento.getVentaTotal());
        movimientoJson.addProperty("descuentoTotal", movimiento.getDescuentoTotal());

        movimientoJson.addProperty("impuestoTotal", movimiento.getImpuestoTotal());
        movimientoJson.addProperty("identificadorEquipo", Main.credencial.getEquipos_id());
        movimientoJson.addProperty("impresoTiquete", "S");
        movimientoJson.addProperty("usoDolar", 0);
        movimientoJson.addProperty("identificadorJornada", movimiento.getGrupoJornadaId());
        movimientoJson.addProperty("identificadorOrigen", 0);
        movimientoJson.addProperty("cajero", movimiento.getPersonaNombre());

        JsonArray detallesVentasArray = new JsonArray();
        double totalBaseImponible = 0;
        double totalBruto = 0;
        double totalImpoconsumo = 0;
        for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
            MovimientosDetallesBean value = entry.getValue();
            JsonObject detalleJson = new JsonObject();
            if (value.getTipo() == NovusConstante.PRODUCTO_DESPACHO_COMBUSTIBLE_VENTA_MIXTA) {
                JsonObject movimientoAtributosJson = new JsonObject();
                movimientoAtributosJson.addProperty("isMixta", true);
                movimientoJson.add("atributos", movimientoAtributosJson);
                try {
                    UpadteByEstateMovimientoUseCase updateUseCase =
                    new UpadteByEstateMovimientoUseCase(value.getId(), "034002");
                    boolean actualizado = updateUseCase.execute();
                    if (!actualizado) {
                        NovusUtils.printLn("⚠️ No se pudo actualizar estado del movimiento: " + value.getId());
                    }   
                } catch (RuntimeException ex) {
                    Logger.getLogger(FacturacionElectronica.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }

            // LOG para depuración de costo antes de armar el JSON
            System.out.println("[DEBUG COSTO] Producto: ID=" + value.getProductoId() + ", Nombre=" + value.getDescripcion() + ", Costo=" + value.getCosto() + ", Comp_Costo=" + value.getProducto_compuesto_costo() + ", CantidadUnidad=" + value.getCantidadUnidad() + ", Saldo=" + value.getSaldo() + ", Precio=" + value.getPrecio());

            detalleJson.addProperty("idTransaccionVentaDetalle", value.getRemotoId());
            detalleJson.addProperty("idTransaccionDetalleVenta", 0);
            detalleJson.addProperty("identificadorProducto", value.getProductoId());
            detalleJson.addProperty("nombreProducto", value.getDescripcion()); // CENTRALIZADOR
            detalleJson.addProperty("identificacionProducto", value.getPlu());
            detalleJson.addProperty("fechaTransaccion", sdf.format(movimiento.getFecha().getTime()));
            detalleJson.addProperty("cantidadVenta", value.getCantidadUnidad());
            detalleJson.addProperty("identificadorUnidad", value.getUnidades_medida_id());// DERRUMBAR
            detalleJson.addProperty("costoProducto", value.getCosto());
            detalleJson.addProperty("unidad", mdao.unidadProducto(value.getUnidades_medida_id()));
            //detalleJson.addProperty("unidad_descripcion", mdao.unidadProductoDescripcion(value.getUnidades_medida_id()));
            detalleJson.addProperty("unidad_descripcion", new ObtenerDescripcionUnidadUseCase(value.getUnidades_medida_id()).execute());
            detalleJson.addProperty("precioProducto", value.getPrecio());
            detalleJson.addProperty("identificadorDescuento", value.getDescuentoId());
            detalleJson.addProperty("descuentoTotal", value.getDescuentoProducto());
            detalleJson.addProperty("subTotalVenta", (double) value.getSubtotal());
            detalleJson.addProperty("total_cantidad", value.getCantidadUnidad());
            detalleJson.addProperty("codigoBarra", value.getCodigoBarra());
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
                ing.addProperty("id", ingrediente.getId());
                ing.addProperty("productos_id", ingrediente.getProducto_compuesto_id());
                ing.addProperty("productos_plu", ingrediente.getPlu() != null ? ingrediente.getPlu() : "");
                ing.addProperty("productos_descripcion", ingrediente.getDescripcion());
                ing.addProperty("productos_precio", ingrediente.getPrecio());
                ing.addProperty("identificadorProducto", ingrediente.getId());
                ing.addProperty("productos_precio_especial", 0);
                ing.addProperty("productos_descuento_porcentaje", 0);
                ing.addProperty("descuento_base", 0);
                ing.addProperty("productos_descuento_valor", 0);
                ing.addProperty("cantidad", ingrediente.getCantidad());
                ing.addProperty("compuesto_cantidad", ingrediente.getProducto_compuesto_cantidad());
                ing.addProperty("costo", ingrediente.getProducto_compuesto_costo() * value.getCantidadUnidad()
                        * ingrediente.getProducto_compuesto_cantidad());
                ingredientesArray.add(ing);
            }
            detalleJson.add("ingredientesAplicados", ingredientesArray);

            JsonArray impuestosArray = new JsonArray();
            double subtotal = 0;
            double impoconsumo = 0;
            double base = 0;
            boolean tieneImpoconsumo = false;
            if (!value.getImpuestos().isEmpty()) {
                for (ImpuestosBean impuesto : value.getImpuestos()) {
                    JsonObject imp = new JsonObject();
                    imp.addProperty("identificadorImpuesto", impuesto.getId());
                    imp.addProperty("nombreImpuesto", impuesto.getDescripcion());
                    imp.addProperty("tipoImpuesto", impuesto.getPorcentaje_valor());
                    imp.addProperty("valorImpAplicado", impuesto.getValor());
                    if (!impuesto.getPorcentaje_valor().equals("$")) {
                        base = value.getPrecio();
                        base = base - obtenerImpoconsumo(value);
                        double valorIva = (impuesto.getValor() / 100d) + 1d;
                        base = base / valorIva;
                        double impuestoCalculado = base * (impuesto.getValor() / 100d);
                        impuestoCalculado = impuestoCalculado * value.getCantidadUnidad();
                        totalImpuestoSinImpoconsumo += impuestoCalculado;
                        detalleJson.addProperty("precioProducto", base);
                        imp.addProperty("valorImpuestoAplicado", impuestoCalculado);
                        totalConsumoPropio += impuesto.getCalculado();
                    } else {
                        tieneImpoconsumo = true;
                        imp.addProperty("valorImpuestoAplicado", impuesto.getValor() * value.getCantidadUnidad());
                        subtotal = (obtenerBase(value) * value.getCantidadUnidad()) + (impuesto.getValor() * value.getCantidadUnidad());
                        impoconsumo = impuesto.getValor() * value.getCantidadUnidad();
                        if (consumopropio) {
                            detalleJson.addProperty("subTotalVenta", 0);
                        } else {
                            detalleJson.addProperty("subTotalVenta", subtotal);
                        }
                    }
                    impuestosArray.add(imp);
                }
            } else {
                base = value.getSubtotal() / value.getCantidadUnidad();
            }
            if (subtotal > 0) {
                detalleJson.addProperty("precioProducto", subtotal);
            }
            if (base == 0) {
                base = obtenerBase(value);
            }
            if (consumopropio) {
                detalleJson.addProperty("descuentoTotal", 0);
                detalleJson.addProperty("subTotalVenta", 0);
                detalleJson.addProperty("base", base);
            } else {
                detalleJson.addProperty("subTotalVenta", tieneImpoconsumo ? subtotal : base * value.getCantidadUnidad());
                detalleJson.addProperty("base", base);
            }
            detalleJson.addProperty("impoconsumo", impoconsumo);
            detalleJson.addProperty("precioVentaFeWeb", value.getPrecio());
            detalleJson.add("impuestosAplicados", impuestosArray);
            detallesVentasArray.add(detalleJson);
            totalImpoconsumo = totalImpoconsumo + impoconsumo;
            totalBruto = totalBruto + detalleJson.get("subTotalVenta").getAsDouble();
            totalBaseImponible = totalBaseImponible + (base * value.getCantidadUnidad());
        }
        movimientoJson.addProperty("total_base_imponible", totalBaseImponible);
        movimientoJson.addProperty("total_bruto", totalBruto);
        JsonArray mediosPagos = new JsonArray();

        for (Map.Entry<Long, MediosPagosBean> entry : movimiento.getMediosPagos().entrySet()) {
            MediosPagosBean mediopago = entry.getValue();
            JsonObject objMedioPago = new JsonObject();
            if (mediopago.getId() == NovusConstante.ID_MEDIO_BONO_TERPEL
                    && mediopago.getBonos_Vive_Terpel() != null) {
                movimientoJson.add("detallesBono", Main.gson.toJsonTree(mediopago.getBonos_Vive_Terpel()));
            }
            objMedioPago.addProperty("descripcionMedio", mediopago.getDescripcion());
            objMedioPago.addProperty("identificacionMediosPagos", mediopago.getId());
            objMedioPago.addProperty("recibidoMedioPago", mediopago.getRecibido());
            objMedioPago.addProperty("totalMedioPago", mediopago.getValor());
            objMedioPago.addProperty("vueltoMedioPago", mediopago.getCambio());
            objMedioPago.addProperty("formaDePago", facturacionElectronicaDao.formaDePago(mediopago.getId()));
            objMedioPago.addProperty("identificacionComprobante", mediopago.getVoucher() != null ? mediopago.getVoucher() : "");
            objMedioPago.addProperty("monedaLocal", "S");
            objMedioPago.addProperty("trm", 0);
            mediosPagos.add(objMedioPago);
        }
        JsonObject respuestaFactura = new JsonObject();
        if (consumopropio) {
            movimientoJson.addProperty("ventaTotal", totalConsumoPropio);
        }
        movimientoJson.addProperty("impuestoTotal", totalImpuestoSinImpoconsumo);
        respuestaFactura.add("venta", movimientoJson);
        respuestaFactura.add("detallesVenta", detallesVentasArray);
        respuestaFactura.add("pagos", mediosPagos);
        respuestaFactura.addProperty("resoluciones", new GetResolucionesUseCase(movimientoJson.get("consecutivo_id").getAsLong()).execute());
        respuestaFactura.add("observaciones", facturacionElectronicaDao.observaciones());
        respuestaFactura.addProperty("TipoEmpresa", facturacionElectronicaDao.tipoEmpresa());
        data.add("datos_FE", respuestaFactura);
        return data;
    }


//Remplaza el metodo destruirVentaDespachadaCombustible con el caso de uso UpadteByEstateMovimientoUseCase
    // public JsonObject generarJsonFE(boolean consumopropio) {
    //     double totalConsumoPropio = 0;

    //     SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
    //     FacturacionElectronicaDao facturacionElectronicaDao = new FacturacionElectronicaDao();

    //     JsonObject movimientoJson = new JsonObject();
    //     double totalImpuestoSinImpoconsumo = 0;
    //     movimientoJson.addProperty("identificadorNegocio", movimiento.getEmpresa().getNegocioId());
    //     movimientoJson.addProperty("identificacionEstacion", movimiento.getEmpresasId());
    //     movimientoJson.addProperty("codigoEstacion", movimiento.getEmpresa().getCodigo());
    //     movimientoJson.addProperty("prefijo", findByParameterUseCase.execute() ? "" : movimiento.getConsecutivo_fe().getPrefijo_fe());
    //     movimientoJson.addProperty("consecutivoActual", findByParameterUseCase.execute() ? mdao.numeroRemision() + 1 : movimiento.getConsecutivo_fe().getConsecutivo_actual_fe());
    //     movimientoJson.addProperty("consecutivoInicial", findByParameterUseCase.execute() ? mdao.numeroRemision() + 1 : movimiento.getConsecutivo_fe().getConsecutivo_inicial_fe());
    //     movimientoJson.addProperty("consecutivoFinal", findByParameterUseCase.execute() ? mdao.numeroRemision() + 1 : movimiento.getConsecutivo_fe().getConsecutivo_final_fe());
    //     movimientoJson.addProperty("operacion", movimiento.getOperacionId());
    //     movimientoJson.addProperty("consecutivo_id", findByParameterUseCase.execute() ? mdao.numeroRemision() : movimiento.getConsecutivo_fe().getId_fe());
    //     movimientoJson.addProperty("persona_id", movimiento.getPersonaId());
    //     movimientoJson.addProperty("persona_nit", movimiento.getPersonaNit());
    //     movimientoJson.addProperty("nombreEstacion", movimiento.getEmpresa().getRazonSocial());
    //     movimientoJson.addProperty("aliasEstacion", movimiento.getEmpresa().getAlias());
    //     movimientoJson.addProperty("identificadorEstacion", movimiento.getEmpresa().getNit());
    //     movimientoJson.addProperty("identificadorTicketVenta", findByParameterUseCase.execute() ? mdao.numeroRemision() + 1 : movimiento.getConsecutivo_fe().getConsecutivo_actual_fe());
    //     movimientoJson.addProperty("identificadorTicket", findByParameterUseCase.execute() ? mdao.numeroRemision() : movimiento.getConsecutivo_fe().getId_fe());
    //     movimientoJson.addProperty("idTransaccionVenta", movimiento.getId());
    //     movimientoJson.addProperty("fechaTransaccion", sdf.format(movimiento.getFecha().getTime()));
    //     movimientoJson.addProperty("identificadorPromotor", movimiento.getPersonaId());
    //     movimientoJson.addProperty("nombresPromotor", movimiento.getPersonaNombre());
    //     movimientoJson.addProperty("apellidosPromotor", movimiento.getPersonaApellidos() != null && !movimiento.getPersonaApellidos().trim().equals("") ? movimiento.getPersonaApellidos() : movimiento.getPersonaNombre());
    //     movimientoJson.addProperty("identificacionPromotor", movimiento.getPersonaNit());
    //     movimientoJson.addProperty("identificadorPersona", movimiento.getClienteId());
    //     movimientoJson.addProperty("identificacionPersona", movimiento.getClienteNit());
    //     movimientoJson.addProperty("nombresPersona", movimiento.getClienteNombre());
    //     movimientoJson.addProperty("apellidosPersona", " ");
    //     movimientoJson.addProperty("identificadorProveedor", 0);
    //     movimientoJson.addProperty("consumo_propio", consumopropio);
    //     movimientoJson.addProperty("identificadorBodega", movimiento.getBodega().getId());
    //     movimientoJson.addProperty("nombresBodega", movimiento.getBodega().getDescripcion());
    //     movimientoJson.addProperty("codigoBodega", movimiento.getBodega().getCodigo());

    //     movimientoJson.addProperty("costoTotal", movimiento.getCostoTotal());
    //     movimientoJson.addProperty("ventaTotal", movimiento.getVentaTotal());
    //     movimientoJson.addProperty("descuentoTotal", movimiento.getDescuentoTotal());

    //     movimientoJson.addProperty("impuestoTotal", movimiento.getImpuestoTotal());
    //     movimientoJson.addProperty("identificadorEquipo", Main.credencial.getEquipos_id());
    //     movimientoJson.addProperty("impresoTiquete", "S");
    //     movimientoJson.addProperty("usoDolar", 0);
    //     movimientoJson.addProperty("identificadorJornada", movimiento.getGrupoJornadaId());
    //     movimientoJson.addProperty("identificadorOrigen", 0);
    //     movimientoJson.addProperty("cajero", movimiento.getPersonaNombre());

    //     JsonArray detallesVentasArray = new JsonArray();
    //     double totalBaseImponible = 0;
    //     double totalBruto = 0;
    //     double totalImpoconsumo = 0;
    //     for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
    //         MovimientosDetallesBean value = entry.getValue();
    //         JsonObject detalleJson = new JsonObject();
    //         if (value.getTipo() == NovusConstante.PRODUCTO_DESPACHO_COMBUSTIBLE_VENTA_MIXTA) {
    //             JsonObject movimientoAtributosJson = new JsonObject();
    //             movimientoAtributosJson.addProperty("isMixta", true);
    //             movimientoJson.add("atributos", movimientoAtributosJson);
    //             try {
    //                 mdao.destruirVentaDespachadaCombustible(value.getId());
    //             } catch (SQLException ex) {
    //                 Logger.getLogger(FacturacionElectronica.class
    //                         .getName()).log(Level.SEVERE, null, ex);
    //             }
    //         }

    //         detalleJson.addProperty("idTransaccionVentaDetalle", value.getRemotoId());
    //         detalleJson.addProperty("idTransaccionDetalleVenta", 0);
    //         detalleJson.addProperty("identificadorProducto", value.getProductoId());
    //         detalleJson.addProperty("nombreProducto", value.getDescripcion()); // CENTRALIZADOR
    //         detalleJson.addProperty("identificacionProducto", value.getPlu());
    //         detalleJson.addProperty("fechaTransaccion", sdf.format(movimiento.getFecha().getTime()));
    //         detalleJson.addProperty("cantidadVenta", value.getCantidadUnidad());
    //         detalleJson.addProperty("identificadorUnidad", value.getUnidades_medida_id());// DERRUMBAR
    //         detalleJson.addProperty("costoProducto", value.getCosto());
    //         detalleJson.addProperty("unidad", mdao.unidadProducto(value.getUnidades_medida_id()));
    //         detalleJson.addProperty("unidad_descripcion", mdao.unidadProductoDescripcion(value.getUnidades_medida_id()));
    //         detalleJson.addProperty("precioProducto", value.getPrecio());
    //         detalleJson.addProperty("identificadorDescuento", value.getDescuentoId());
    //         detalleJson.addProperty("descuentoTotal", value.getDescuentoProducto());
    //         detalleJson.addProperty("subTotalVenta", (double) value.getSubtotal());
    //         detalleJson.addProperty("total_cantidad", value.getCantidadUnidad());
    //         detalleJson.addProperty("codigoBarra", value.getCodigoBarra());
    //         JsonObject jsonAtributos = new JsonObject();
    //         jsonAtributos.addProperty("categoriaId", value.getCategoriaId());
    //         jsonAtributos.addProperty("categoriaDescripcion", value.getCategoriaDesc());
    //         jsonAtributos.addProperty("tipo", value.getTipo());

    //         if (value.getTipo() == NovusConstante.PRODUCTO_DESPACHO_COMBUSTIBLE_VENTA_MIXTA) {
    //             jsonAtributos.addProperty("unidad", value.getUnidades_medida());
    //         }

    //         detalleJson.add("atributos", jsonAtributos);

    //         JsonArray ingredientesArray = new JsonArray();
    //         for (ProductoBean ingrediente : value.getIngredientes()) {
    //             JsonObject ing = new JsonObject();
    //             ing.addProperty("id", ingrediente.getId());
    //             ing.addProperty("productos_id", ingrediente.getProducto_compuesto_id());
    //             ing.addProperty("productos_plu", ingrediente.getPlu() != null ? ingrediente.getPlu() : "");
    //             ing.addProperty("productos_descripcion", ingrediente.getDescripcion());
    //             ing.addProperty("productos_precio", ingrediente.getPrecio());
    //             ing.addProperty("identificadorProducto", ingrediente.getId());
    //             ing.addProperty("productos_precio_especial", 0);
    //             ing.addProperty("productos_descuento_porcentaje", 0);
    //             ing.addProperty("descuento_base", 0);
    //             ing.addProperty("productos_descuento_valor", 0);
    //             ing.addProperty("cantidad", ingrediente.getCantidad());
    //             ing.addProperty("compuesto_cantidad", ingrediente.getProducto_compuesto_cantidad());
    //             ing.addProperty("costo", ingrediente.getProducto_compuesto_costo() * value.getCantidadUnidad()
    //                     * ingrediente.getProducto_compuesto_cantidad());
    //             ingredientesArray.add(ing);
    //         }
    //         detalleJson.add("ingredientesAplicados", ingredientesArray);

    //         JsonArray impuestosArray = new JsonArray();
    //         double subtotal = 0;
    //         double impoconsumo = 0;
    //         double base = 0;
    //         boolean tieneImpoconsumo = false;
    //         if (!value.getImpuestos().isEmpty()) {
    //             for (ImpuestosBean impuesto : value.getImpuestos()) {
    //                 JsonObject imp = new JsonObject();
    //                 imp.addProperty("identificadorImpuesto", impuesto.getId());
    //                 imp.addProperty("nombreImpuesto", impuesto.getDescripcion());
    //                 imp.addProperty("tipoImpuesto", impuesto.getPorcentaje_valor());
    //                 imp.addProperty("valorImpAplicado", impuesto.getValor());
    //                 if (!impuesto.getPorcentaje_valor().equals("$")) {
    //                     base = value.getPrecio();
    //                     base = base - obtenerImpoconsumo(value);
    //                     double valorIva = (impuesto.getValor() / 100d) + 1d;
    //                     base = base / valorIva;
    //                     double impuestoCalculado = base * (impuesto.getValor() / 100d);
    //                     impuestoCalculado = impuestoCalculado * value.getCantidadUnidad();
    //                     totalImpuestoSinImpoconsumo += impuestoCalculado;
    //                     detalleJson.addProperty("precioProducto", base);
    //                     imp.addProperty("valorImpuestoAplicado", impuestoCalculado);
    //                     totalConsumoPropio += impuesto.getCalculado();
    //                 } else {
    //                     tieneImpoconsumo = true;
    //                     imp.addProperty("valorImpuestoAplicado", impuesto.getValor() * value.getCantidadUnidad());
    //                     subtotal = (obtenerBase(value) * value.getCantidadUnidad()) + (impuesto.getValor() * value.getCantidadUnidad());
    //                     impoconsumo = impuesto.getValor() * value.getCantidadUnidad();
    //                     if (consumopropio) {
    //                         detalleJson.addProperty("subTotalVenta", 0);
    //                     } else {
    //                         detalleJson.addProperty("subTotalVenta", subtotal);
    //                     }
    //                 }
    //                 impuestosArray.add(imp);
    //             }
    //         } else {
    //             base = value.getSubtotal() / value.getCantidadUnidad();
    //         }
    //         if (subtotal > 0) {
    //             detalleJson.addProperty("precioProducto", subtotal);
    //         }
    //         if (base == 0) {
    //             base = obtenerBase(value);
    //         }
    //         if (consumopropio) {
    //             detalleJson.addProperty("descuentoTotal", 0);
    //             detalleJson.addProperty("subTotalVenta", 0);
    //             detalleJson.addProperty("base", base);
    //         } else {
    //             detalleJson.addProperty("subTotalVenta", tieneImpoconsumo ? subtotal : base * value.getCantidadUnidad());
    //             detalleJson.addProperty("base", base);
    //         }
    //         detalleJson.addProperty("impoconsumo", impoconsumo);
    //         detalleJson.addProperty("precioVentaFeWeb", value.getPrecio());
    //         detalleJson.add("impuestosAplicados", impuestosArray);
    //         detallesVentasArray.add(detalleJson);
    //         totalImpoconsumo = totalImpoconsumo + impoconsumo;
    //         totalBruto = totalBruto + detalleJson.get("subTotalVenta").getAsDouble();
    //         totalBaseImponible = totalBaseImponible + (base * value.getCantidadUnidad());
    //     }
    //     movimientoJson.addProperty("total_base_imponible", totalBaseImponible);
    //     movimientoJson.addProperty("total_bruto", totalBruto);
    //     JsonArray mediosPagos = new JsonArray();

    //     for (Map.Entry<Long, MediosPagosBean> entry : movimiento.getMediosPagos().entrySet()) {
    //         MediosPagosBean mediopago = entry.getValue();
    //         JsonObject objMedioPago = new JsonObject();
    //         if (mediopago.getId() == NovusConstante.ID_MEDIO_BONO_TERPEL
    //                 && mediopago.getBonos_Vive_Terpel() != null) {
    //             movimientoJson.add("detallesBono", Main.gson.toJsonTree(mediopago.getBonos_Vive_Terpel()));
    //         }
    //         objMedioPago.addProperty("descripcionMedio", mediopago.getDescripcion());
    //         objMedioPago.addProperty("identificacionMediosPagos", mediopago.getId());
    //         objMedioPago.addProperty("recibidoMedioPago", mediopago.getRecibido());
    //         objMedioPago.addProperty("totalMedioPago", mediopago.getValor());
    //         objMedioPago.addProperty("vueltoMedioPago", mediopago.getCambio());
    //         objMedioPago.addProperty("formaDePago", facturacionElectronicaDao.formaDePago(mediopago.getId()));
    //         objMedioPago.addProperty("identificacionComprobante", mediopago.getVoucher() != null ? mediopago.getVoucher() : "");
    //         objMedioPago.addProperty("monedaLocal", "S");
    //         objMedioPago.addProperty("trm", 0);
    //         mediosPagos.add(objMedioPago);
    //     }
    //     JsonObject respuestaFactura = new JsonObject();
    //     if (consumopropio) {
    //         movimientoJson.addProperty("ventaTotal", totalConsumoPropio);
    //     }
    //     movimientoJson.addProperty("impuestoTotal", totalImpuestoSinImpoconsumo);
    //     respuestaFactura.add("venta", movimientoJson);
    //     respuestaFactura.add("detallesVenta", detallesVentasArray);
    //     respuestaFactura.add("pagos", mediosPagos);
    //     respuestaFactura.addProperty("resoluciones", facturacionElectronicaDao.resoluciones(movimientoJson.get("consecutivo_id").getAsInt()));
    //     respuestaFactura.add("observaciones", facturacionElectronicaDao.observaciones());
    //     respuestaFactura.addProperty("TipoEmpresa", facturacionElectronicaDao.tipoEmpresa());
    //     data.add("datos_FE", respuestaFactura);
    //     return data;
    // }



    //remplaza el metodo remsionActiva con el caso de uso FinByParameterUseCase de movimientos detalles
    // public JsonObject generarJsonFE(boolean consumopropio) {
    //     double totalConsumoPropio = 0;

    //     SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
    //     FacturacionElectronicaDao facturacionElectronicaDao = new FacturacionElectronicaDao();

    //     JsonObject movimientoJson = new JsonObject();
    //     double totalImpuestoSinImpoconsumo = 0;
    //     movimientoJson.addProperty("identificadorNegocio", movimiento.getEmpresa().getNegocioId());
    //     movimientoJson.addProperty("identificacionEstacion", movimiento.getEmpresasId());
    //     movimientoJson.addProperty("codigoEstacion", movimiento.getEmpresa().getCodigo());
    //     movimientoJson.addProperty("prefijo", mdao.remisionActiva() ? "" : movimiento.getConsecutivo_fe().getPrefijo_fe());
    //     movimientoJson.addProperty("consecutivoActual", mdao.remisionActiva() ? mdao.numeroRemision() + 1 : movimiento.getConsecutivo_fe().getConsecutivo_actual_fe());
    //     movimientoJson.addProperty("consecutivoInicial", mdao.remisionActiva() ? mdao.numeroRemision() + 1 : movimiento.getConsecutivo_fe().getConsecutivo_inicial_fe());
    //     movimientoJson.addProperty("consecutivoFinal", mdao.remisionActiva() ? mdao.numeroRemision() + 1 : movimiento.getConsecutivo_fe().getConsecutivo_final_fe());
    //     movimientoJson.addProperty("operacion", movimiento.getOperacionId());
    //     movimientoJson.addProperty("consecutivo_id", mdao.remisionActiva() ? mdao.numeroRemision() : movimiento.getConsecutivo_fe().getId_fe());
    //     movimientoJson.addProperty("persona_id", movimiento.getPersonaId());
    //     movimientoJson.addProperty("persona_nit", movimiento.getPersonaNit());
    //     movimientoJson.addProperty("nombreEstacion", movimiento.getEmpresa().getRazonSocial());
    //     movimientoJson.addProperty("aliasEstacion", movimiento.getEmpresa().getAlias());
    //     movimientoJson.addProperty("identificadorEstacion", movimiento.getEmpresa().getNit());
    //     movimientoJson.addProperty("identificadorTicketVenta", mdao.remisionActiva() ? mdao.numeroRemision() + 1 : movimiento.getConsecutivo_fe().getConsecutivo_actual_fe());
    //     movimientoJson.addProperty("identificadorTicket", mdao.remisionActiva() ? mdao.numeroRemision() : movimiento.getConsecutivo_fe().getId_fe());
    //     movimientoJson.addProperty("idTransaccionVenta", movimiento.getId());
    //     movimientoJson.addProperty("fechaTransaccion", sdf.format(movimiento.getFecha().getTime()));
    //     movimientoJson.addProperty("identificadorPromotor", movimiento.getPersonaId());
    //     movimientoJson.addProperty("nombresPromotor", movimiento.getPersonaNombre());
    //     movimientoJson.addProperty("apellidosPromotor", movimiento.getPersonaApellidos() != null && !movimiento.getPersonaApellidos().trim().equals("") ? movimiento.getPersonaApellidos() : movimiento.getPersonaNombre());
    //     movimientoJson.addProperty("identificacionPromotor", movimiento.getPersonaNit());
    //     movimientoJson.addProperty("identificadorPersona", movimiento.getClienteId());
    //     movimientoJson.addProperty("identificacionPersona", movimiento.getClienteNit());
    //     movimientoJson.addProperty("nombresPersona", movimiento.getClienteNombre());
    //     movimientoJson.addProperty("apellidosPersona", " ");
    //     movimientoJson.addProperty("identificadorProveedor", 0);
    //     movimientoJson.addProperty("consumo_propio", consumopropio);
    //     movimientoJson.addProperty("identificadorBodega", movimiento.getBodega().getId());
    //     movimientoJson.addProperty("nombresBodega", movimiento.getBodega().getDescripcion());
    //     movimientoJson.addProperty("codigoBodega", movimiento.getBodega().getCodigo());

    //     movimientoJson.addProperty("costoTotal", movimiento.getCostoTotal());
    //     movimientoJson.addProperty("ventaTotal", movimiento.getVentaTotal());
    //     movimientoJson.addProperty("descuentoTotal", movimiento.getDescuentoTotal());

    //     movimientoJson.addProperty("impuestoTotal", movimiento.getImpuestoTotal());
    //     movimientoJson.addProperty("identificadorEquipo", Main.credencial.getEquipos_id());
    //     movimientoJson.addProperty("impresoTiquete", "S");
    //     movimientoJson.addProperty("usoDolar", 0);
    //     movimientoJson.addProperty("identificadorJornada", movimiento.getGrupoJornadaId());
    //     movimientoJson.addProperty("identificadorOrigen", 0);
    //     movimientoJson.addProperty("cajero", movimiento.getPersonaNombre());

    //     JsonArray detallesVentasArray = new JsonArray();
    //     double totalBaseImponible = 0;
    //     double totalBruto = 0;
    //     double totalImpoconsumo = 0;
    //     for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
    //         MovimientosDetallesBean value = entry.getValue();
    //         JsonObject detalleJson = new JsonObject();
    //         if (value.getTipo() == NovusConstante.PRODUCTO_DESPACHO_COMBUSTIBLE_VENTA_MIXTA) {
    //             JsonObject movimientoAtributosJson = new JsonObject();
    //             movimientoAtributosJson.addProperty("isMixta", true);
    //             movimientoJson.add("atributos", movimientoAtributosJson);
    //             try {
    //                 mdao.destruirVentaDespachadaCombustible(value.getId());
    //             } catch (SQLException ex) {
    //                 Logger.getLogger(FacturacionElectronica.class
    //                         .getName()).log(Level.SEVERE, null, ex);
    //             }
    //         }

    //         detalleJson.addProperty("idTransaccionVentaDetalle", value.getRemotoId());
    //         detalleJson.addProperty("idTransaccionDetalleVenta", 0);
    //         detalleJson.addProperty("identificadorProducto", value.getProductoId());
    //         detalleJson.addProperty("nombreProducto", value.getDescripcion()); // CENTRALIZADOR
    //         detalleJson.addProperty("identificacionProducto", value.getPlu());
    //         detalleJson.addProperty("fechaTransaccion", sdf.format(movimiento.getFecha().getTime()));
    //         detalleJson.addProperty("cantidadVenta", value.getCantidadUnidad());
    //         detalleJson.addProperty("identificadorUnidad", value.getUnidades_medida_id());// DERRUMBAR
    //         detalleJson.addProperty("costoProducto", value.getCosto());
    //         detalleJson.addProperty("unidad", mdao.unidadProducto(value.getUnidades_medida_id()));
    //         detalleJson.addProperty("unidad_descripcion", mdao.unidadProductoDescripcion(value.getUnidades_medida_id()));
    //         detalleJson.addProperty("precioProducto", value.getPrecio());
    //         detalleJson.addProperty("identificadorDescuento", value.getDescuentoId());
    //         detalleJson.addProperty("descuentoTotal", value.getDescuentoProducto());
    //         detalleJson.addProperty("subTotalVenta", (double) value.getSubtotal());
    //         detalleJson.addProperty("total_cantidad", value.getCantidadUnidad());
    //         detalleJson.addProperty("codigoBarra", value.getCodigoBarra());
    //         JsonObject jsonAtributos = new JsonObject();
    //         jsonAtributos.addProperty("categoriaId", value.getCategoriaId());
    //         jsonAtributos.addProperty("categoriaDescripcion", value.getCategoriaDesc());
    //         jsonAtributos.addProperty("tipo", value.getTipo());

    //         if (value.getTipo() == NovusConstante.PRODUCTO_DESPACHO_COMBUSTIBLE_VENTA_MIXTA) {
    //             jsonAtributos.addProperty("unidad", value.getUnidades_medida());
    //         }

    //         detalleJson.add("atributos", jsonAtributos);

    //         JsonArray ingredientesArray = new JsonArray();
    //         for (ProductoBean ingrediente : value.getIngredientes()) {
    //             JsonObject ing = new JsonObject();
    //             ing.addProperty("id", ingrediente.getId());
    //             ing.addProperty("productos_id", ingrediente.getProducto_compuesto_id());
    //             ing.addProperty("productos_plu", ingrediente.getPlu() != null ? ingrediente.getPlu() : "");
    //             ing.addProperty("productos_descripcion", ingrediente.getDescripcion());
    //             ing.addProperty("productos_precio", ingrediente.getPrecio());
    //             ing.addProperty("identificadorProducto", ingrediente.getId());
    //             ing.addProperty("productos_precio_especial", 0);
    //             ing.addProperty("productos_descuento_porcentaje", 0);
    //             ing.addProperty("descuento_base", 0);
    //             ing.addProperty("productos_descuento_valor", 0);
    //             ing.addProperty("cantidad", ingrediente.getCantidad());
    //             ing.addProperty("compuesto_cantidad", ingrediente.getProducto_compuesto_cantidad());
    //             ing.addProperty("costo", ingrediente.getProducto_compuesto_costo() * value.getCantidadUnidad()
    //                     * ingrediente.getProducto_compuesto_cantidad());
    //             ingredientesArray.add(ing);
    //         }
    //         detalleJson.add("ingredientesAplicados", ingredientesArray);

    //         JsonArray impuestosArray = new JsonArray();
    //         double subtotal = 0;
    //         double impoconsumo = 0;
    //         double base = 0;
    //         boolean tieneImpoconsumo = false;
    //         if (!value.getImpuestos().isEmpty()) {
    //             for (ImpuestosBean impuesto : value.getImpuestos()) {
    //                 JsonObject imp = new JsonObject();
    //                 imp.addProperty("identificadorImpuesto", impuesto.getId());
    //                 imp.addProperty("nombreImpuesto", impuesto.getDescripcion());
    //                 imp.addProperty("tipoImpuesto", impuesto.getPorcentaje_valor());
    //                 imp.addProperty("valorImpAplicado", impuesto.getValor());
    //                 if (!impuesto.getPorcentaje_valor().equals("$")) {
    //                     base = value.getPrecio();
    //                     base = base - obtenerImpoconsumo(value);
    //                     double valorIva = (impuesto.getValor() / 100d) + 1d;
    //                     base = base / valorIva;
    //                     double impuestoCalculado = base * (impuesto.getValor() / 100d);
    //                     impuestoCalculado = impuestoCalculado * value.getCantidadUnidad();
    //                     totalImpuestoSinImpoconsumo += impuestoCalculado;
    //                     detalleJson.addProperty("precioProducto", base);
    //                     imp.addProperty("valorImpuestoAplicado", impuestoCalculado);
    //                     totalConsumoPropio += impuesto.getCalculado();
    //                 } else {
    //                     tieneImpoconsumo = true;
    //                     imp.addProperty("valorImpuestoAplicado", impuesto.getValor() * value.getCantidadUnidad());
    //                     subtotal = (obtenerBase(value) * value.getCantidadUnidad()) + (impuesto.getValor() * value.getCantidadUnidad());
    //                     impoconsumo = impuesto.getValor() * value.getCantidadUnidad();
    //                     if (consumopropio) {
    //                         detalleJson.addProperty("subTotalVenta", 0);
    //                     } else {
    //                         detalleJson.addProperty("subTotalVenta", subtotal);
    //                     }
    //                 }
    //                 impuestosArray.add(imp);
    //             }
    //         } else {
    //             base = value.getSubtotal() / value.getCantidadUnidad();
    //         }
    //         if (subtotal > 0) {
    //             detalleJson.addProperty("precioProducto", subtotal);
    //         }
    //         if (base == 0) {
    //             base = obtenerBase(value);
    //         }
    //         if (consumopropio) {
    //             detalleJson.addProperty("descuentoTotal", 0);
    //             detalleJson.addProperty("subTotalVenta", 0);
    //             detalleJson.addProperty("base", base);
    //         } else {
    //             detalleJson.addProperty("subTotalVenta", tieneImpoconsumo ? subtotal : base * value.getCantidadUnidad());
    //             detalleJson.addProperty("base", base);
    //         }
    //         detalleJson.addProperty("impoconsumo", impoconsumo);
    //         detalleJson.addProperty("precioVentaFeWeb", value.getPrecio());
    //         detalleJson.add("impuestosAplicados", impuestosArray);
    //         detallesVentasArray.add(detalleJson);
    //         totalImpoconsumo = totalImpoconsumo + impoconsumo;
    //         totalBruto = totalBruto + detalleJson.get("subTotalVenta").getAsDouble();
    //         totalBaseImponible = totalBaseImponible + (base * value.getCantidadUnidad());
    //     }
    //     movimientoJson.addProperty("total_base_imponible", totalBaseImponible);
    //     movimientoJson.addProperty("total_bruto", totalBruto);
    //     JsonArray mediosPagos = new JsonArray();

    //     for (Map.Entry<Long, MediosPagosBean> entry : movimiento.getMediosPagos().entrySet()) {
    //         MediosPagosBean mediopago = entry.getValue();
    //         JsonObject objMedioPago = new JsonObject();
    //         if (mediopago.getId() == NovusConstante.ID_MEDIO_BONO_TERPEL
    //                 && mediopago.getBonos_Vive_Terpel() != null) {
    //             movimientoJson.add("detallesBono", Main.gson.toJsonTree(mediopago.getBonos_Vive_Terpel()));
    //         }
    //         objMedioPago.addProperty("descripcionMedio", mediopago.getDescripcion());
    //         objMedioPago.addProperty("identificacionMediosPagos", mediopago.getId());
    //         objMedioPago.addProperty("recibidoMedioPago", mediopago.getRecibido());
    //         objMedioPago.addProperty("totalMedioPago", mediopago.getValor());
    //         objMedioPago.addProperty("vueltoMedioPago", mediopago.getCambio());
    //         objMedioPago.addProperty("formaDePago", facturacionElectronicaDao.formaDePago(mediopago.getId()));
    //         objMedioPago.addProperty("identificacionComprobante", mediopago.getVoucher() != null ? mediopago.getVoucher() : "");
    //         objMedioPago.addProperty("monedaLocal", "S");
    //         objMedioPago.addProperty("trm", 0);
    //         mediosPagos.add(objMedioPago);
    //     }
    //     JsonObject respuestaFactura = new JsonObject();
    //     if (consumopropio) {
    //         movimientoJson.addProperty("ventaTotal", totalConsumoPropio);
    //     }
    //     movimientoJson.addProperty("impuestoTotal", totalImpuestoSinImpoconsumo);
    //     respuestaFactura.add("venta", movimientoJson);
    //     respuestaFactura.add("detallesVenta", detallesVentasArray);
    //     respuestaFactura.add("pagos", mediosPagos);
    //     respuestaFactura.addProperty("resoluciones", facturacionElectronicaDao.resoluciones(movimientoJson.get("consecutivo_id").getAsInt()));
    //     respuestaFactura.add("observaciones", facturacionElectronicaDao.observaciones());
    //     respuestaFactura.addProperty("TipoEmpresa", facturacionElectronicaDao.tipoEmpresa());
    //     data.add("datos_FE", respuestaFactura);
    //     return data;
    // }



    //remplaza el metodo remsionActiva con el caso de uso FinByParameterUseCase de movimientos detalles
    // public JsonObject generarJsonFE(boolean consumopropio) {
    //     double totalConsumoPropio = 0;

    //     SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
    //     FacturacionElectronicaDao facturacionElectronicaDao = new FacturacionElectronicaDao();

    //     JsonObject movimientoJson = new JsonObject();
    //     double totalImpuestoSinImpoconsumo = 0;
    //     movimientoJson.addProperty("identificadorNegocio", movimiento.getEmpresa().getNegocioId());
    //     movimientoJson.addProperty("identificacionEstacion", movimiento.getEmpresasId());
    //     movimientoJson.addProperty("codigoEstacion", movimiento.getEmpresa().getCodigo());
    //     movimientoJson.addProperty("prefijo", mdao.remisionActiva() ? "" : movimiento.getConsecutivo_fe().getPrefijo_fe());
    //     movimientoJson.addProperty("consecutivoActual", mdao.remisionActiva() ? mdao.numeroRemision() + 1 : movimiento.getConsecutivo_fe().getConsecutivo_actual_fe());
    //     movimientoJson.addProperty("consecutivoInicial", mdao.remisionActiva() ? mdao.numeroRemision() + 1 : movimiento.getConsecutivo_fe().getConsecutivo_inicial_fe());
    //     movimientoJson.addProperty("consecutivoFinal", mdao.remisionActiva() ? mdao.numeroRemision() + 1 : movimiento.getConsecutivo_fe().getConsecutivo_final_fe());
    //     movimientoJson.addProperty("operacion", movimiento.getOperacionId());
    //     movimientoJson.addProperty("consecutivo_id", mdao.remisionActiva() ? mdao.numeroRemision() : movimiento.getConsecutivo_fe().getId_fe());
    //     movimientoJson.addProperty("persona_id", movimiento.getPersonaId());
    //     movimientoJson.addProperty("persona_nit", movimiento.getPersonaNit());
    //     movimientoJson.addProperty("nombreEstacion", movimiento.getEmpresa().getRazonSocial());
    //     movimientoJson.addProperty("aliasEstacion", movimiento.getEmpresa().getAlias());
    //     movimientoJson.addProperty("identificadorEstacion", movimiento.getEmpresa().getNit());
    //     movimientoJson.addProperty("identificadorTicketVenta", mdao.remisionActiva() ? mdao.numeroRemision() + 1 : movimiento.getConsecutivo_fe().getConsecutivo_actual_fe());
    //     movimientoJson.addProperty("identificadorTicket", mdao.remisionActiva() ? mdao.numeroRemision() : movimiento.getConsecutivo_fe().getId_fe());
    //     movimientoJson.addProperty("idTransaccionVenta", movimiento.getId());
    //     movimientoJson.addProperty("fechaTransaccion", sdf.format(movimiento.getFecha().getTime()));
    //     movimientoJson.addProperty("identificadorPromotor", movimiento.getPersonaId());
    //     movimientoJson.addProperty("nombresPromotor", movimiento.getPersonaNombre());
    //     movimientoJson.addProperty("apellidosPromotor", movimiento.getPersonaApellidos() != null && !movimiento.getPersonaApellidos().trim().equals("") ? movimiento.getPersonaApellidos() : movimiento.getPersonaNombre());
    //     movimientoJson.addProperty("identificacionPromotor", movimiento.getPersonaNit());
    //     movimientoJson.addProperty("identificadorPersona", movimiento.getClienteId());
    //     movimientoJson.addProperty("identificacionPersona", movimiento.getClienteNit());
    //     movimientoJson.addProperty("nombresPersona", movimiento.getClienteNombre());
    //     movimientoJson.addProperty("apellidosPersona", " ");
    //     movimientoJson.addProperty("identificadorProveedor", 0);
    //     movimientoJson.addProperty("consumo_propio", consumopropio);
    //     movimientoJson.addProperty("identificadorBodega", movimiento.getBodega().getId());
    //     movimientoJson.addProperty("nombresBodega", movimiento.getBodega().getDescripcion());
    //     movimientoJson.addProperty("codigoBodega", movimiento.getBodega().getCodigo());

    //     movimientoJson.addProperty("costoTotal", movimiento.getCostoTotal());
    //     movimientoJson.addProperty("ventaTotal", movimiento.getVentaTotal());
    //     movimientoJson.addProperty("descuentoTotal", movimiento.getDescuentoTotal());

    //     movimientoJson.addProperty("impuestoTotal", movimiento.getImpuestoTotal());
    //     movimientoJson.addProperty("identificadorEquipo", Main.credencial.getEquipos_id());
    //     movimientoJson.addProperty("impresoTiquete", "S");
    //     movimientoJson.addProperty("usoDolar", 0);
    //     movimientoJson.addProperty("identificadorJornada", movimiento.getGrupoJornadaId());
    //     movimientoJson.addProperty("identificadorOrigen", 0);
    //     movimientoJson.addProperty("cajero", movimiento.getPersonaNombre());

    //     JsonArray detallesVentasArray = new JsonArray();
    //     double totalBaseImponible = 0;
    //     double totalBruto = 0;
    //     double totalImpoconsumo = 0;
    //     for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
    //         MovimientosDetallesBean value = entry.getValue();
    //         JsonObject detalleJson = new JsonObject();
    //         if (value.getTipo() == NovusConstante.PRODUCTO_DESPACHO_COMBUSTIBLE_VENTA_MIXTA) {
    //             JsonObject movimientoAtributosJson = new JsonObject();
    //             movimientoAtributosJson.addProperty("isMixta", true);
    //             movimientoJson.add("atributos", movimientoAtributosJson);
    //             try {
    //                 mdao.destruirVentaDespachadaCombustible(value.getId());
    //             } catch (SQLException ex) {
    //                 Logger.getLogger(FacturacionElectronica.class
    //                         .getName()).log(Level.SEVERE, null, ex);
    //             }
    //         }

    //         detalleJson.addProperty("idTransaccionVentaDetalle", value.getRemotoId());
    //         detalleJson.addProperty("idTransaccionDetalleVenta", 0);
    //         detalleJson.addProperty("identificadorProducto", value.getProductoId());
    //         detalleJson.addProperty("nombreProducto", value.getDescripcion()); // CENTRALIZADOR
    //         detalleJson.addProperty("identificacionProducto", value.getPlu());
    //         detalleJson.addProperty("fechaTransaccion", sdf.format(movimiento.getFecha().getTime()));
    //         detalleJson.addProperty("cantidadVenta", value.getCantidadUnidad());
    //         detalleJson.addProperty("identificadorUnidad", value.getUnidades_medida_id());// DERRUMBAR
    //         detalleJson.addProperty("costoProducto", value.getCosto());
    //         detalleJson.addProperty("unidad", mdao.unidadProducto(value.getUnidades_medida_id()));
    //         detalleJson.addProperty("unidad_descripcion", mdao.unidadProductoDescripcion(value.getUnidades_medida_id()));
    //         detalleJson.addProperty("precioProducto", value.getPrecio());
    //         detalleJson.addProperty("identificadorDescuento", value.getDescuentoId());
    //         detalleJson.addProperty("descuentoTotal", value.getDescuentoProducto());
    //         detalleJson.addProperty("subTotalVenta", (double) value.getSubtotal());
    //         detalleJson.addProperty("total_cantidad", value.getCantidadUnidad());
    //         detalleJson.addProperty("codigoBarra", value.getCodigoBarra());
    //         JsonObject jsonAtributos = new JsonObject();
    //         jsonAtributos.addProperty("categoriaId", value.getCategoriaId());
    //         jsonAtributos.addProperty("categoriaDescripcion", value.getCategoriaDesc());
    //         jsonAtributos.addProperty("tipo", value.getTipo());

    //         if (value.getTipo() == NovusConstante.PRODUCTO_DESPACHO_COMBUSTIBLE_VENTA_MIXTA) {
    //             jsonAtributos.addProperty("unidad", value.getUnidades_medida());
    //         }

    //         detalleJson.add("atributos", jsonAtributos);

    //         JsonArray ingredientesArray = new JsonArray();
    //         for (ProductoBean ingrediente : value.getIngredientes()) {
    //             JsonObject ing = new JsonObject();
    //             ing.addProperty("id", ingrediente.getId());
    //             ing.addProperty("productos_id", ingrediente.getProducto_compuesto_id());
    //             ing.addProperty("productos_plu", ingrediente.getPlu() != null ? ingrediente.getPlu() : "");
    //             ing.addProperty("productos_descripcion", ingrediente.getDescripcion());
    //             ing.addProperty("productos_precio", ingrediente.getPrecio());
    //             ing.addProperty("identificadorProducto", ingrediente.getId());
    //             ing.addProperty("productos_precio_especial", 0);
    //             ing.addProperty("productos_descuento_porcentaje", 0);
    //             ing.addProperty("descuento_base", 0);
    //             ing.addProperty("productos_descuento_valor", 0);
    //             ing.addProperty("cantidad", ingrediente.getCantidad());
    //             ing.addProperty("compuesto_cantidad", ingrediente.getProducto_compuesto_cantidad());
    //             ing.addProperty("costo", ingrediente.getProducto_compuesto_costo() * value.getCantidadUnidad()
    //                     * ingrediente.getProducto_compuesto_cantidad());
    //             ingredientesArray.add(ing);
    //         }
    //         detalleJson.add("ingredientesAplicados", ingredientesArray);

    //         JsonArray impuestosArray = new JsonArray();
    //         double subtotal = 0;
    //         double impoconsumo = 0;
    //         double base = 0;
    //         boolean tieneImpoconsumo = false;
    //         if (!value.getImpuestos().isEmpty()) {
    //             for (ImpuestosBean impuesto : value.getImpuestos()) {
    //                 JsonObject imp = new JsonObject();
    //                 imp.addProperty("identificadorImpuesto", impuesto.getId());
    //                 imp.addProperty("nombreImpuesto", impuesto.getDescripcion());
    //                 imp.addProperty("tipoImpuesto", impuesto.getPorcentaje_valor());
    //                 imp.addProperty("valorImpAplicado", impuesto.getValor());
    //                 if (!impuesto.getPorcentaje_valor().equals("$")) {
    //                     base = value.getPrecio();
    //                     base = base - obtenerImpoconsumo(value);
    //                     double valorIva = (impuesto.getValor() / 100d) + 1d;
    //                     base = base / valorIva;
    //                     double impuestoCalculado = base * (impuesto.getValor() / 100d);
    //                     impuestoCalculado = impuestoCalculado * value.getCantidadUnidad();
    //                     totalImpuestoSinImpoconsumo += impuestoCalculado;
    //                     detalleJson.addProperty("precioProducto", base);
    //                     imp.addProperty("valorImpuestoAplicado", impuestoCalculado);
    //                     totalConsumoPropio += impuesto.getCalculado();
    //                 } else {
    //                     tieneImpoconsumo = true;
    //                     imp.addProperty("valorImpuestoAplicado", impuesto.getValor() * value.getCantidadUnidad());
    //                     subtotal = (obtenerBase(value) * value.getCantidadUnidad()) + (impuesto.getValor() * value.getCantidadUnidad());
    //                     impoconsumo = impuesto.getValor() * value.getCantidadUnidad();
    //                     if (consumopropio) {
    //                         detalleJson.addProperty("subTotalVenta", 0);
    //                     } else {
    //                         detalleJson.addProperty("subTotalVenta", subtotal);
    //                     }
    //                 }
    //                 impuestosArray.add(imp);
    //             }
    //         } else {
    //             base = value.getSubtotal() / value.getCantidadUnidad();
    //         }
    //         if (subtotal > 0) {
    //             detalleJson.addProperty("precioProducto", subtotal);
    //         }
    //         if (base == 0) {
    //             base = obtenerBase(value);
    //         }
    //         if (consumopropio) {
    //             detalleJson.addProperty("descuentoTotal", 0);
    //             detalleJson.addProperty("subTotalVenta", 0);
    //             detalleJson.addProperty("base", base);
    //         } else {
    //             detalleJson.addProperty("subTotalVenta", tieneImpoconsumo ? subtotal : base * value.getCantidadUnidad());
    //             detalleJson.addProperty("base", base);
    //         }
    //         detalleJson.addProperty("impoconsumo", impoconsumo);
    //         detalleJson.addProperty("precioVentaFeWeb", value.getPrecio());
    //         detalleJson.add("impuestosAplicados", impuestosArray);
    //         detallesVentasArray.add(detalleJson);
    //         totalImpoconsumo = totalImpoconsumo + impoconsumo;
    //         totalBruto = totalBruto + detalleJson.get("subTotalVenta").getAsDouble();
    //         totalBaseImponible = totalBaseImponible + (base * value.getCantidadUnidad());
    //     }
    //     movimientoJson.addProperty("total_base_imponible", totalBaseImponible);
    //     movimientoJson.addProperty("total_bruto", totalBruto);
    //     JsonArray mediosPagos = new JsonArray();

    //     for (Map.Entry<Long, MediosPagosBean> entry : movimiento.getMediosPagos().entrySet()) {
    //         MediosPagosBean mediopago = entry.getValue();
    //         JsonObject objMedioPago = new JsonObject();
    //         if (mediopago.getId() == NovusConstante.ID_MEDIO_BONO_TERPEL
    //                 && mediopago.getBonos_Vive_Terpel() != null) {
    //             movimientoJson.add("detallesBono", Main.gson.toJsonTree(mediopago.getBonos_Vive_Terpel()));
    //         }
    //         objMedioPago.addProperty("descripcionMedio", mediopago.getDescripcion());
    //         objMedioPago.addProperty("identificacionMediosPagos", mediopago.getId());
    //         objMedioPago.addProperty("recibidoMedioPago", mediopago.getRecibido());
    //         objMedioPago.addProperty("totalMedioPago", mediopago.getValor());
    //         objMedioPago.addProperty("vueltoMedioPago", mediopago.getCambio());
    //         objMedioPago.addProperty("formaDePago", facturacionElectronicaDao.formaDePago(mediopago.getId()));
    //         objMedioPago.addProperty("identificacionComprobante", mediopago.getVoucher() != null ? mediopago.getVoucher() : "");
    //         objMedioPago.addProperty("monedaLocal", "S");
    //         objMedioPago.addProperty("trm", 0);
    //         mediosPagos.add(objMedioPago);
    //     }
    //     JsonObject respuestaFactura = new JsonObject();
    //     if (consumopropio) {
    //         movimientoJson.addProperty("ventaTotal", totalConsumoPropio);
    //     }
    //     movimientoJson.addProperty("impuestoTotal", totalImpuestoSinImpoconsumo);
    //     respuestaFactura.add("venta", movimientoJson);
    //     respuestaFactura.add("detallesVenta", detallesVentasArray);
    //     respuestaFactura.add("pagos", mediosPagos);
    //     respuestaFactura.addProperty("resoluciones", facturacionElectronicaDao.resoluciones(movimientoJson.get("consecutivo_id").getAsInt()));
    //     respuestaFactura.add("observaciones", facturacionElectronicaDao.observaciones());
    //     respuestaFactura.addProperty("TipoEmpresa", facturacionElectronicaDao.tipoEmpresa());
    //     data.add("datos_FE", respuestaFactura);
    //     return data;
    // }

    private double obtenerImpoconsumo(MovimientosDetallesBean detalle) {
        double impoconsumo = 0;
        for (ImpuestosBean detalleImpuesto : detalle.getImpuestos()) {
            if (detalleImpuesto.getPorcentaje_valor().equals("$")) {
                impoconsumo = detalleImpuesto.getValor();
            }
        }
        return impoconsumo;
    }

    private double obtenerBase(MovimientosDetallesBean detalle) {
        double base = detalle.getPrecio();
        for (ImpuestosBean detalleImpuesto : detalle.getImpuestos()) {
            if (detalleImpuesto.getPorcentaje_valor().equals("%")) {
                base = base - obtenerImpoconsumo(detalle);
                double valorIva = (detalleImpuesto.getValor() / 100d) + 1d;
                base = base / valorIva;
            }
        }
        return base;
    }
   public JsonObject montoMinimoobligatorioFE(float montoMinimoFE) {
        boolean isObligatorio = false;
        MovimientosDao dao = new MovimientosDao();
        float monto;
        boolean error;
        String obligatoriedad;
        if (!findByParameterUseCase.execute()) {
            //JsonObject montoFE = dao.montoMinimoFE();
            JsonObject montoFE = new GetMontoMinimoFacturaElectronicaUseCase().execute();
            monto = montoFE.get("monto_minimo").getAsFloat();
            obligatoriedad = montoFE.get("OBLIGATORIO_FE").getAsString();
            error = montoFE.get("error").getAsBoolean();
        } else {
            //JsonObject montoFE = dao.montoMinimoRemision();
            JsonObject montoFE = new GetMontoMinimoRemisionUseCase().execute();
            monto = montoFE.get("monto_minimo").getAsFloat();
            obligatoriedad = montoFE.get("OBLIGATORIO_FE").getAsString();
            error = montoFE.get("error").getAsBoolean();
        }
        JsonObject respuestaObligatorio = new JsonObject();
        respuestaObligatorio.addProperty("obligatorio", obligatoriedad.equals("S") && montoMinimoFE >= monto);
        respuestaObligatorio.addProperty("error", error);

        return respuestaObligatorio;
    }

    //buscar monto minimo de FE
    // public JsonObject montoMinimoobligatorioFE(float montoMinimoFE) {
    //     boolean isObligatorio = false;
    //     MovimientosDao dao = new MovimientosDao();
    //     float monto;
    //     boolean error;
    //     String obligatoriedad;
    //     if (!dao.remisionActiva()) {
    //         JsonObject montoFE = dao.montoMinimoFE();
    //         monto = montoFE.get("monto_minimo").getAsFloat();
    //         obligatoriedad = montoFE.get("OBLIGATORIO_FE").getAsString();
    //         error = montoFE.get("error").getAsBoolean();
    //     } else {
    //         JsonObject montoFE = dao.montoMinimoRemision();
    //         monto = montoFE.get("monto_minimo").getAsFloat();
    //         obligatoriedad = montoFE.get("OBLIGATORIO_FE").getAsString();
    //         error = montoFE.get("error").getAsBoolean();
    //     }
    //     JsonObject respuestaObligatorio = new JsonObject();
    //     respuestaObligatorio.addProperty("obligatorio", obligatoriedad.equals("S") && montoMinimoFE >= monto);
    //     respuestaObligatorio.addProperty("error", error);

    //     return respuestaObligatorio;
    // }

    // public boolean remisionActiva() {
    //     MovimientosDao dao = new MovimientosDao();
    //     return dao.remisionActiva();
    // }

     public boolean remisionActiva() {
       return findByParameterUseCase.execute();
    }

    public boolean isDefaultFe() {
        boolean isDefault = false;
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select valor from wacher_parametros wp where "
                + "codigo = 'DEFAULT_FE'";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String valor = rs.getString("valor");
                isDefault = valor.equals("S");
            }
        } catch (SQLException ex) {
            Logger.getLogger(FacturacionElectronica.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        return isDefault;
    }

    public boolean isDefaultRemision() {
        boolean isDefault = false;
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select valor from wacher_parametros wp where "
                + "codigo = 'DEFAULT_REMISION'";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String valor = rs.getString("valor");
                isDefault = valor.equals("S");
            }
        } catch (SQLException ex) {
            Logger.getLogger(FacturacionElectronica.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        return isDefault;
    }
    public boolean aplicaFE() {
        try {
            FindAllWacherParametrosUseCase findAllWacherParametrosUseCase = new FindAllWacherParametrosUseCase();
            return findAllWacherParametrosUseCase.execute().stream()
                .filter(parametro -> "MODULO_FACTURACION_ELECTRONICA".equals(parametro.getCodigo()))
                .findFirst()
                .map(parametro -> "S".equals(parametro.getValor()))
                .orElse(false);
        } catch (Exception ex) {
            NovusUtils.printLn("Error al obtener parámetros: " + ex.getMessage());
            return false;
        }
    }


    // public boolean aplicaFE() {
    //     Connection conexion = Main.obtenerConexion("lazoexpresscore");
    //     String sql = "select * from wacher_parametros where codigo = 'MODULO_FACTURACION_ELECTRONICA'";
    //     try (Statement ps = conexion.createStatement()) {
    //         ResultSet rs = ps.executeQuery(sql);
    //         if (rs.next()) {
    //             String habilitar = rs.getString("valor");
    //             return habilitar.equals("S");
    //         }
    //     } catch (SQLException ex) {
    //         NovusUtils.printLn("error");
    //     }
    //     return false;
    // }

    // public boolean obligatoriedad() {
    //     MovimientosDao dao = new MovimientosDao();
    //     return dao.remisionActiva() || aplicaFE();
    // }

    public JsonObject imprimirFE(JsonArray dataNota, int numeroVenta) {
        JsonObject data = new JsonObject();
        for (JsonElement jsonElement : dataNota) {
            JsonObject dataP = jsonElement.getAsJsonObject();
            if (dataP.get("id").getAsLong() == numeroVenta) {
                boolean imprimirRemision = dataP.get("atributos").getAsJsonObject().has("tipoVenta") && dataP.get("atributos").getAsJsonObject().get("tipoVenta").getAsLong() == 100;
                boolean facturacionElectronica = dataP.get("atributos").getAsJsonObject().has("isElectronica") && dataP.get("atributos").getAsJsonObject().get("isElectronica").getAsBoolean();
                if (facturacionElectronica || imprimirRemision) {
                    data = dataP;
                    data.addProperty("comprobante_fe", true);
                } else {
                    data.addProperty("comprobante_fe", false);
                }
                break;
            }
        }
        return data;
    }

}
