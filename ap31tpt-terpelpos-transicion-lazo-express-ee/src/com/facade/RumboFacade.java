package com.facade;

import com.application.useCases.sutidores.GetCodigoExternoProductoUseCase;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.EquipoDao;
import com.dao.SurtidorDao;
import com.firefuel.Main;
import com.google.gson.JsonObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

public class RumboFacade {

    public static SimpleDateFormat sdfISO = new SimpleDateFormat(NovusConstante.FORMAT_DATE_ISO);
    private static GetCodigoExternoProductoUseCase getCodigoExternoProductoUseCase = new GetCodigoExternoProductoUseCase();

    public static JsonObject fetchAditionalData(JsonObject request) {
        JsonObject response = null;
        TreeMap<String, String> header = new TreeMap<>();
        header.put("Content-type", "application/json");
        header.put("Authorization", "token");
        header.put("aplicacion", "rumbopos");
        header.put("identificadorDispositivo", "localhost");
        header.put("fecha", sdfISO.format(new Date()));
        ClientWSAsync clientWS = new ClientWSAsync("ENVIO DATOS ADICIONALES",
                NovusConstante.SECURE_CENTRAL_POINT_ADITIONAL_DATA,
                NovusConstante.POST,
                request,
                true, false, header);
        try {
            response = clientWS.esperaRespuesta();
            if (response == null) {
                return clientWS.getError();
            }
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
        return response;
    }

    public static JsonObject fetchSaleAuthorization(JsonObject request, boolean isAdblue) {
        JsonObject response = null;
        String ruta = isAdblue
                ? NovusConstante.SECURE_CENTRAL_POINT_NOTIFICAR_AUTORIZACION_AD_BLUE
                : NovusConstante.SECURE_CENTRAL_POINT_SALE_AUTHORIZATION_RUMBO;
        TreeMap<String, String> header = new TreeMap<>();
        header.put("Content-type", "application/json");
        header.put("Authorization", "token");
        header.put("aplicacion", "rumbopos");
        header.put("identificadorDispositivo", "localhost");
        header.put("fecha", sdfISO.format(new Date()));
        ClientWSAsync clientWS = new ClientWSAsync("AUTORIZACION VENTA",
                ruta, NovusConstante.POST, request,
                true, false, header);
        try {
            response = clientWS.esperaRespuesta();
            if (response == null) {
                return clientWS.getError();
            }
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
            return null;
        }
        return response;
    }

    public static JsonObject buildJsonLiberacion(JsonObject infoVenta) {
        JsonObject json = new JsonObject();
        json.addProperty("movimientoId", infoVenta.get("movimientoId").getAsLong());
        json.addProperty("codigoEstacion", Main.edao.getCodigoEstacion());
        json.addProperty("identificadorAutorizacionEDS", infoVenta.get("identificadorAutorizacionEDS").getAsString());
        json.addProperty("identificadorAprobacion", infoVenta.get("identificadorAprobacion").getAsString());

        return json;
    }

    public static JsonObject liberacionVentaAdBlue(JsonObject info) {
        JsonObject response = null;
        String ruta = NovusConstante.SECURE_CENTRAL_POINT_LIBERAR_AUTORIZACION_AD_BLUE;
        TreeMap<String, String> header = new TreeMap<>();
        header.put("Content-type", "application/json");
        header.put("Authorization", "token");
        header.put("aplicacion", "rumbopos");
        header.put("identificadorDispositivo", "localhost");
        header.put("fecha", sdfISO.format(new Date()));

        JsonObject request = buildJsonLiberacion(info);

        ClientWSAsync clientWS = new ClientWSAsync("AUTORIZACION VENTA",
                ruta, NovusConstante.POST, request,
                true, false, header);
        try {
            response = clientWS.esperaRespuesta();
            if (response == null) {
                return clientWS.getError();
            }
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
            return null;
        }
        return response;
    }

    public static JsonObject finalizarVentaAdblue(JsonObject request) {
        JsonObject response = null;
        String ruta = NovusConstante.SECURE_CENTRAL_POINT_FINALIZAR_AD_BLUE;
        TreeMap<String, String> header = new TreeMap<>();
        header.put("Content-type", "application/json");
        header.put("Authorization", "token");
        header.put("aplicacion", "rumbopos");
        header.put("identificadorDispositivo", "localhost");
        header.put("fecha", sdfISO.format(new Date()));

        ClientWSAsync clientWS = new ClientWSAsync("AUTORIZACION VENTA",
                ruta, NovusConstante.POST, request,
                true, false, header);
        try {
            response = clientWS.esperaRespuesta();
            if (response == null) {
                return clientWS.getError();
            }
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
            return null;
        }
        return response;
    }

    public static JsonObject buildJsonAuthorizationAppRumbo(JsonObject infoAutorizacionApp, JsonObject infoVenta, JsonObject infoEds) {
        JsonObject infoAutorizacion = infoAutorizacionApp;

        long idAutorizacion = infoAutorizacionApp.has("identificadorautorizacioneds") ? infoAutorizacionApp.get("identificadorautorizacioneds").getAsLong() : 0;

        JsonObject data = new JsonObject();
        data.addProperty("codigo", idAutorizacion);
        data.addProperty("surtidor", infoVenta.get("surtidor").getAsLong());
        data.addProperty("cara", infoVenta.get("cara").getAsLong());
        data.addProperty("grado", infoVenta.get("identificadorGrado").getAsLong());
        data.addProperty("proveedoresId", 3);
        data.addProperty("preventa", true);
        data.addProperty("estado", "A");
        String documentoIdentificacion = infoAutorizacion.has("documento_identificacioncliente") ? infoAutorizacion.get("documento_identificacioncliente").getAsString() : "";
        data.addProperty("documentoCliente", documentoIdentificacion);
        String placaVehiculo = infoAutorizacion.has("placa_vehiculo") ? infoAutorizacion.get("placa_vehiculo").getAsString() : "";
        data.addProperty("placaVehiculo", placaVehiculo);
        data.addProperty("precioUnidad", infoVenta.get("precioVentaUnidad").getAsString());
        float descuentoCliente = infoAutorizacion.has("porcentaje_descuento_cliente") ? infoAutorizacion.get("porcentaje_descuento_cliente").getAsFloat() : 0;
        data.addProperty("porcentajeDescuentoCliente", descuentoCliente);
        float montoMaximo = infoAutorizacion.has("monto_maximo") ? infoAutorizacion.get("monto_maximo").getAsFloat() : 0;
        data.addProperty("montoMaximo", montoMaximo);
        float cantidadMaxima = infoAutorizacion.has("cantidad_maxima") ? infoAutorizacion.get("cantidad_maxima").getAsFloat() : 0;
        data.addProperty("cantidadMaxima", cantidadMaxima);
        long identificadorFormaPago = infoAutorizacion.has("identificador_forma_pago") ? infoAutorizacion.get("identificador_forma_pago").getAsLong() : null;
        data.addProperty("metodo_pago", identificadorFormaPago);
        data.addProperty("medioAutorizacion", "app rumbo");
        String clienteNombre = infoAutorizacion.has("nombre_cliente") ? infoAutorizacion.get("nombre_cliente").getAsString() : "";
        data.addProperty("clienteNombre", clienteNombre);
        String valorOdometro = infoAutorizacion.has("valor_odometro") ? infoAutorizacion.get("valor_odometro").getAsString() : "";
        data.addProperty("vehiculoOdometro", valorOdometro);

        JsonObject trama = new JsonObject();
        JsonObject header = new JsonObject();
        trama.add("header", header);

        JsonObject request = buildJsonRequestAutorizationApp(infoVenta, idAutorizacion);
        trama.add("request", request);

        JsonObject response = buildJsonResponseAutorizationApp(infoAutorizacionApp, idAutorizacion);
        trama.add("response", response);

        trama.add("infoEDS", infoEds);

        data.add("trama", trama);

        data.addProperty("promotorId", Main.persona.getId());
        return data;
    }

    public static JsonObject buildJsonRequestAutorizationApp(JsonObject info, long identificadorAutorizacion) {
        SurtidorDao surtidorDao = new SurtidorDao();
        EquipoDao edao = new EquipoDao();
        String codigoEstacion = edao.getCodigoEstacion();
        JsonObject request = new JsonObject();
        request.addProperty("surtidor", info.get("surtidor").getAsLong());
        request.addProperty("cantidad", 0);
        request.addProperty("monto", 0);
        request.addProperty("numeroCara", info.get("cara").getAsLong());
        request.addProperty("valorOdometro", info.get("valorOdometro").getAsString());
        request.addProperty("codigoFamiliaProducto", info.get("codigoFamiliaProducto").getAsLong());
        request.addProperty("precioVentaUnidad", info.get("precioVentaUnidad").getAsString());
        request.addProperty("codigoSeguridad", info.get("codigoSeguridad").getAsString());
        request.addProperty("codigoTipoIdentificador", info.get("codigoTipoIdentificador").getAsLong());
        request.addProperty("serialIdentificador", "");
        request.addProperty("identificadorPromotor", Main.persona.getId());
        request.addProperty("identificadorGrado", info.get("identificadorGrado").getAsLong());
        request.addProperty("codigoPais", "CO");
        request.addProperty("identificadorAutorizacionEDS", Long.toString(identificadorAutorizacion));
        request.addProperty("codigoEstacion", codigoEstacion);
        String codigoProducto = getCodigoExternoProductoUseCase.execute(new Long[] { info.get("cara").getAsLong(), info.get("identificadorGrado").getAsLong() });
        request.addProperty("codigoProducto", codigoProducto != null ? codigoProducto : "");
        return request;
    }

    public static JsonObject buildJsonResponseAutorizationApp(JsonObject info, long identificadorAutorizacion) {
        JsonObject response = new JsonObject();
        EquipoDao edao = new EquipoDao();
        String codigoEstacion = edao.getCodigoEstacion();

        float cantidadMaxima = info.has("cantidad_maxima") ? info.get("cantidad_maxima").getAsFloat() : 0;
        response.addProperty("cantidadMaxima", cantidadMaxima);

        String documentoIdentificacion = info.has("documento_identificacioncliente") ? info.get("documento_identificacioncliente").getAsString() : "";
        response.addProperty("documentoIdentificacionCliente", documentoIdentificacion);

        long identificadorFormaPago = info.has("identificador_forma_pago") ? info.get("identificador_forma_pago").getAsLong() : null;
        response.addProperty("identificadorFormaPago", identificadorFormaPago);
        response.addProperty("identificadorTipoDocumentoCliente", 1);//Validar atributo *************

        float montoMaximo = info.has("monto_maximo") ? info.get("monto_maximo").getAsFloat() : 0;
        response.addProperty("montoMaximo", montoMaximo);

        String clienteNombre = info.has("nombre_cliente") ? info.get("nombre_cliente").getAsString() : "";
        response.addProperty("nombreCliente", clienteNombre);
        float descuentoCliente = info.has("porcentaje_descuento_cliente") ? info.get("porcentaje_descuento_cliente").getAsFloat() : 0;
        response.addProperty("porcentajeDescuentoCliente", descuentoCliente);
        response.addProperty("precioUnidadCliente", 0);

        String programaCliente = info.has("programa_cliente") ? info.get("programa_cliente").getAsString() : "";
        response.addProperty("programaCliente", programaCliente);
        String placaVehiculo = info.has("placa_vehiculo") ? info.get("placa_vehiculo").getAsString() : "";
        response.addProperty("placaVehiculo", placaVehiculo);
        long valorOdometro = info.has("valor_odometro") ? info.get("valor_odometro").getAsLong() : 0;
        response.addProperty("valorOdometro", valorOdometro);
        response.addProperty("tipoDescuentoCliente", 0);//Validar atributo *************

        float valorMaximoCliente = info.has("valor_descuento_cliente") ? info.get("valor_descuento_cliente").getAsFloat() : 0;
        response.addProperty("valorDescuentoCliente", valorMaximoCliente);
        String identificadorAprobacion = info.has("identificador_aprobacion") ? info.get("identificador_aprobacion").getAsString() : "";
        response.addProperty("identificadorAprobacion", identificadorAprobacion);
        response.addProperty("identificadorAutorizacionEDS", Long.toString(identificadorAutorizacion));
        response.addProperty("codigoFamiliaProducto", info.get("codigo_familia_producto").getAsInt());
        response.addProperty("codigoEstacion", codigoEstacion);
        response.addProperty("mensaje", "");
        response.addProperty("InformacionAdicional", "");
        response.addProperty("InformacionAdicionalUsed", false);
        response.addProperty("esPorDinero", true);//Validar atributo *************

        JsonObject datosImpresion = new JsonObject();
        String dataNull = null;
        datosImpresion.addProperty("noVisitasDia", 0);
        datosImpresion.addProperty("noVisitasSemana", 0);
        datosImpresion.addProperty("noVisitasQuincena", 0);
        datosImpresion.addProperty("noVisitasMes", 0);
        datosImpresion.addProperty("cantidadDia", 0);
        datosImpresion.addProperty("cantidadSemanal", 0);
        datosImpresion.addProperty("cantidadMes", 0);
        datosImpresion.addProperty("cantidadQuincena", 0);
        datosImpresion.addProperty("mostrarRestriccionesEnTiquete", false);
        datosImpresion.addProperty("codigoContrato", "");
        datosImpresion.addProperty("codigoCentroCostos", dataNull);
        datosImpresion.addProperty("nombreCentroCostos", dataNull);

        response.add("datosDeImpresion", datosImpresion);

        JsonObject datosAdicionales = new JsonObject();
        datosAdicionales.addProperty("requierePlacaVehiculo", false);
        datosAdicionales.addProperty("codigoSeguridad", "");
        datosAdicionales.addProperty("requiereInformacionAdicional", false);
        response.add("datosAdicionales", datosAdicionales);

        JsonObject errores = new JsonObject();
        errores.addProperty("codigoError", "");
        errores.addProperty("mensajeError", "");
        response.add("errores", errores);

        return response;
    }

    public static void notificarAutorizacionAppRumbo(int hose, JsonObject info, JsonObject infoAppRumbo) {
        NovusUtils.printLn("@@@@@@@@@@@@@@@@@@@@@@@@@@");
        NovusUtils.printLn("Notificar Autorizacion Placa Rumbo");
        NovusUtils.printLn("@@@@@@@@@@@@@@@@@@@@@@@@@@");
        JsonObject json = new JsonObject();

        json.addProperty("identificadorAutorizacionEDS", info.get("codigo").getAsString());
        json.addProperty("hose", hose);
        json.addProperty("face", info.get("cara").getAsInt());

        JsonObject infoApp = new JsonObject();
        infoApp.addProperty("placa", info.get("placaVehiculo").getAsString());
        infoApp.addProperty("empresa_id", infoAppRumbo.get("empresa_id").getAsLong());
        infoApp.addProperty("equipo_id", infoAppRumbo.get("equipo_id").getAsLong());

        json.add("appRumbo", infoApp);
        json.add("additionalData", info.get("trama").getAsJsonObject().get("response")
                .getAsJsonObject().get("datosAdicionales").getAsJsonObject());
        JsonObject response = null;
        boolean debug = true;

        String url = NovusConstante.SECURE_CENTRAL_POINT_NOTIFICAR_AUTORIZACION_APP_RUMBO;
        ClientWSAsync ws = new ClientWSAsync("NOTIFICANDO AUTORIZACION VENTA APP RUMBO", url,
                NovusConstante.POST, json, debug, false);
        try {
            response = ws.esperaRespuesta();
            if (response == null) {
                response = ws.getError();
            }
        } catch (Exception ex) {
            NovusUtils.printLn(ex.getMessage());
        }
        NovusUtils.printLn("Response: " + response);
    }

}
