package com.facade;

import com.bean.CTmovimientoMediosPagoBean;
import com.bean.CTmovimientosDetallesAtributosBean;
import com.bean.CTmovimientosDetallesBean;
import com.controllers.NovusConstante;
import com.firefuel.Main;
import com.google.gson.JsonObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import com.application.useCases.productos.ObtenerPrecioUreaUseCase;
import com.application.useCases.productos.ObtenerIdProductoUreaUseCase;
import com.application.useCases.bodegas.ObtenerIdBodegaUreaUseCase;
import com.application.useCases.jornadas.ObtenerJornadaIdUseCase;

public class VentasCombustibleFacade {

    static Calendar calendar = Calendar.getInstance();

    public static String obtenerFormatoFechaActual() {
        String fecha = "";
        LocalDateTime fechaHoraActual = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(NovusConstante.FORMAT_DATE_ISO);
        fecha = fechaHoraActual.format(formatter);
        return fecha.trim();
    }

    public static JsonObject buildObjectCtMovimientos(JsonObject data, JsonObject request, boolean isCredito, long movimientoId, String medioAutorizacion) {

        LocalDateTime fechaHoraActual = LocalDateTime.now();
        JsonObject info = data.get("data").getAsJsonObject();
        JsonObject InfoMovimiento = new JsonObject();

        String nulo = null;
        long movId = isCredito ? 1 : movimientoId;

        InfoMovimiento.addProperty("empresas_id", Main.credencial.getEmpresas_id());

        InfoMovimiento.addProperty("tipo", !isCredito
                ? NovusConstante.TIPO_VENTA_COMBUSTIBLE
                : NovusConstante.TIPO_VENTA_COMBUSTIBLE_CREDITO);

        InfoMovimiento.addProperty("estado_movimiento", !isCredito
                ? NovusConstante.MOV_VENTA_COMBUSTIBLE
                : NovusConstante.MOV_VENTA_COMBUSTIBLE_CREDITO);

        InfoMovimiento.addProperty("estado", NovusConstante.ESTADO_ESPECIAL_VENTA);
        InfoMovimiento.addProperty("fecha", obtenerFormatoFechaActual());
        InfoMovimiento.addProperty("consecutivo", movId);
        InfoMovimiento.addProperty("responsables_id", Main.persona.getId());
        InfoMovimiento.addProperty("personas_id", isCredito ? 3 : 2);
        InfoMovimiento.addProperty("terceros_id", nulo);
        InfoMovimiento.addProperty("costo_total", 0);
        InfoMovimiento.addProperty("venta_total", 0);
        InfoMovimiento.addProperty("impuesto_total", 0);
        InfoMovimiento.addProperty("descuento_total", 0);
        InfoMovimiento.addProperty("sincronizado", 0);
        InfoMovimiento.addProperty("equipos_id", Main.credencial.getEquipos_id());
        InfoMovimiento.addProperty("remoto_id", movId);

        JsonObject atributos = new JsonObject();
        atributos.addProperty("tipo_negocio", 5);
        atributos.addProperty("responsables_nombre", Main.persona.getNombre());
        atributos.addProperty("responsables_identificacion", Main.persona.getIdentificacion());
        atributos.addProperty("personas_nombre", info.get("nombreCliente").getAsString());
        atributos.addProperty("personas_identificacion", info.get("documentoIdentificacionCliente").getAsString());
        atributos.addProperty("tercero_nombre", "");
        atributos.addProperty("tercero_identificacion", "");

        JsonObject precioDiferencial = new JsonObject();
        atributos.add("precioDiferencial", precioDiferencial);

        atributos.addProperty("surtidor", 1);
        atributos.addProperty("cara", 1);
        atributos.addProperty("manguera", 1);
        atributos.addProperty("grado", 0);
        atributos.addProperty("islas", Main.credencial.getIsla());
        atributos.addProperty("familiaDesc", "ADBLUE");
        atributos.addProperty("familiaId", 0);
        atributos.addProperty("consecutivo", nulo);
        atributos.addProperty("isElectronica", Boolean.FALSE);
        atributos.addProperty("suspendido", Boolean.FALSE);

        JsonObject cliente = new JsonObject();
        atributos.add("cliente", cliente);

        JsonObject extraData = new JsonObject();

        JsonObject header = new JsonObject();
        extraData.add("header", header);

        extraData.add("request", request);
        extraData.add("response", data);

        atributos.add("extraData", extraData);

        atributos.addProperty("fidelizada", "N");
        atributos.addProperty("vehiculo_placa", info.get("placaVehiculo").getAsString());
        atributos.addProperty("vehiculo_numero", " ");
        atributos.addProperty("vehiculo_odometro", info.get("valorOdometro").getAsString());

        JsonObject rumbo = new JsonObject();
        rumbo.addProperty("programaCliente", info.get("programaCliente").getAsString());
        rumbo.addProperty("identificadorTipoDocumentoCliente", info.get("identificadorTipoDocumentoCliente").getAsInt());
        rumbo.addProperty("documentoIdentificacionCliente", info.get("documentoIdentificacionCliente").getAsString());
        rumbo.addProperty("nombreCliente", info.get("nombreCliente").getAsString());
        rumbo.addProperty("identificadorFormaPago", info.get("identificadorFormaPago").getAsInt());
        rumbo.addProperty("codigoEstacion", info.get("codigoEstacion").getAsString());
        rumbo.addProperty("placaVehiculo", info.get("placaVehiculo").getAsString());
        rumbo.addProperty("identificadorAutorizacion", info.get("identificadorAprobacion").getAsString());
        rumbo.addProperty("numeroRemisionLazo", info.get("identificadorAutorizacionEDS").getAsString());
        rumbo.addProperty("numeroTicketeVenta", info.get("identificadorAutorizacionEDS").getAsString());
        rumbo.addProperty("medio_autorizacion", medioAutorizacion);
        rumbo.addProperty("tipoDescuentoCliente", 0);
        rumbo.addProperty("valorDescuentoCliente", 0);
        rumbo.addProperty("porcentajeDescuentoCliente", 0);
        atributos.add("rumbo", rumbo);

        atributos.addProperty("CuentaLocal", nulo);
        atributos.addProperty("isCuentaLocal", Boolean.FALSE);
        atributos.addProperty("identificadorCupo", 0);
        atributos.addProperty("tipoCupo", nulo);
        atributos.addProperty("isCredito", Boolean.TRUE);
        atributos.addProperty("recuperada", Boolean.FALSE);
        atributos.addProperty("editarFidelizacion", Boolean.TRUE);
        atributos.addProperty("tipoVenta", isCredito ? 10 : 4);
        atributos.addProperty("isContingencia", Boolean.FALSE);

        if (isCredito) {
            atributos.addProperty("motivoAnulacion", "CIENTES CREDITOS");
            atributos.addProperty("tipoAnulacion", 1);
        }

        InfoMovimiento.add("atributos", atributos);

        InfoMovimiento.addProperty("impreso", "N");
        InfoMovimiento.addProperty("movimientos_id", (movimientoId != 0) ? movimientoId : null);
        InfoMovimiento.addProperty("uso_dolar", 0);
        InfoMovimiento.addProperty("ano", fechaHoraActual.getYear());
        InfoMovimiento.addProperty("mes", fechaHoraActual.getMonth().getValue());
        InfoMovimiento.addProperty("dia", fechaHoraActual.getDayOfWeek().getValue());
        InfoMovimiento.addProperty("jornadas_id", new ObtenerJornadaIdUseCase().execute());
        InfoMovimiento.addProperty("origen_id", nulo);
        InfoMovimiento.addProperty("prefijo", !isCredito ? nulo : "CREDITO");
        InfoMovimiento.addProperty("id_tipo_venta", nulo);
        InfoMovimiento.addProperty("json_data", nulo);

        return InfoMovimiento;
    }

    public static JsonObject buildObjectCtMovimientoDetalles() {

        LocalDateTime fechaHoraActual = LocalDateTime.now();

        CTmovimientosDetallesBean movimientoDetalles = new CTmovimientosDetallesBean();
        CTmovimientosDetallesAtributosBean movimientoDetallesAtributos = new CTmovimientosDetallesAtributosBean();

        movimientoDetalles.setMovimientosId(0);
        movimientoDetalles.setBodegasId(new ObtenerIdBodegaUreaUseCase().execute());
        movimientoDetalles.setCantidad(0);
        movimientoDetalles.setCostoProducto(0);
        movimientoDetalles.setPrecio(new ObtenerPrecioUreaUseCase().execute());
        movimientoDetalles.setDescuentosId(0);
        movimientoDetalles.setDescuentoCalculado(0);
        movimientoDetalles.setFecha(obtenerFormatoFechaActual());
        movimientoDetalles.setAno(fechaHoraActual.getYear());
        movimientoDetalles.setMes(fechaHoraActual.getMonth().getValue());
        movimientoDetalles.setDia(fechaHoraActual.getDayOfWeek().getValue());
        movimientoDetalles.setRemotoId(0);
        movimientoDetalles.setSincronizado("1");
        movimientoDetalles.setSubTotal(0);
        movimientoDetalles.setSubMovimientosDetallesId(0);
        movimientoDetalles.setUnidadesId(1);
        movimientoDetalles.setProductosId(new ObtenerIdProductoUreaUseCase().execute());
        movimientoDetalles.setAtributos(movimientoDetallesAtributos);

        JsonObject infoMovimientoDetalles = Main.gson.toJsonTree(movimientoDetalles).getAsJsonObject();

        return infoMovimientoDetalles;
    }

    public static JsonObject buildObjectCtMediosPagos() {

        CTmovimientoMediosPagoBean movimientoMediosPago = new CTmovimientoMediosPagoBean();

        movimientoMediosPago.setCtMediosPagosId(NovusConstante.ID_MEDIO_PAGO_CREDITO);
        movimientoMediosPago.setCtMovimientosId(0);
        movimientoMediosPago.setValorRecibido(0);
        movimientoMediosPago.setValorCambio(0);
        movimientoMediosPago.setValorTotal(0);
        movimientoMediosPago.setNumeroComprobante("");
        movimientoMediosPago.setMonedaLocal("S");
        movimientoMediosPago.setTrm(0);
        movimientoMediosPago.setIngPagoDatafono(false);

        JsonObject infoMovimientoMediosPago = Main.gson.toJsonTree(movimientoMediosPago).getAsJsonObject();

        return infoMovimientoMediosPago;
    }

}
