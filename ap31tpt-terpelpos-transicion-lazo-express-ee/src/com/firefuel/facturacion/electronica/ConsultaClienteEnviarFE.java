/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.facturacion.electronica;

import com.WT2.loyalty.domain.entities.beans.FoundClient;
import com.WT2.loyalty.domain.entities.beans.IdentificationClient;
import com.WT2.loyalty.domain.entities.response.RespuestasAcumulacion;
import com.application.useCases.consecutivos.ActualizarConsecutivoUseCase;
import com.application.useCases.consecutivos.ValidarConsecutivoUsadoUseCase;
import com.application.useCases.ventas.VentaEnCursoUseCase;
import com.bean.ConsecutivoBean;
import com.bean.MovimientosBean;
import com.bean.MovimientosDetallesBean;
import com.bean.Notificador;
import com.bean.TipoNegocio;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.MovimientosDao;
import com.facade.ConfigurationFacade;
import com.facade.PedidoFacade;
import com.facade.fidelizacion.AcumulacionFidelizacion;
import com.firefuel.ClienteFacturaElectronica;
import com.firefuel.DialogoConfirmacionMensaje;
import com.firefuel.Main;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.neo.print.services.ReportesFE;
import com.utils.enums.TipoNegociosFidelizacion;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import com.application.useCases.wacherparametros.FindByParameterUseCase;
import com.application.commons.CtWacherParametrosEnum;


/**
 *
 * @author Devitech
 */
public class ConsultaClienteEnviarFE {
    FindByParameterUseCase findByParameterUseCase = new FindByParameterUseCase(CtWacherParametrosEnum.CODIGO.getColumnName(), "REMISION");
    JsonObject comprobante;
    JFrame confir;
    ConfiguracionFE config = new ConfiguracionFE();
    MovimientosDao mdao = new MovimientosDao();
    static final Logger logger = Logger.getLogger(ConsultaClienteEnviarFE.class.getName());
    boolean inicio;
    boolean cerrarLoader;
    JsonObject datosCliente = new JsonObject();
    JsonObject datos = new JsonObject();
    public static MovimientosBean movimientosBean;
    ConfiguracionFE configuracionFE = new ConfiguracionFE();
    private IdentificationClient datosClienteFidelizacion;
    private String identificadorPuntoDeventa;
    private static final String CODIGO_SAP = "codigoSAP";
    private static final String CORREO_ELECTRONICO = "correoElectronico";
    private static final String TIPO_RESPONSABILIDAD = "tipoResponsabilidad";
    private static final String DATOS_TRIBUTQARIOS_ADQUIRIENTES = "datosTributariosAdquirente";
    long idMovimiento = 0L;
    private FoundClient foundClient;

    public void setDatosClientesFidelizacion(IdentificationClient datosClienteFidelizaion) {
        this.datosClienteFidelizacion = datosClienteFidelizaion;
    }

    public void setIdentificadorPuntoDeventa(String indetifecadorPuntodeVenta) {
        this.identificadorPuntoDeventa = indetifecadorPuntodeVenta;
    }

    // Consulta de cliente FE
    public JsonObject consultarCliente(String numeroDocumento, long identificador, Notificador notificadorView) {

        String funcion = "CONSULTA DE CLIENTE FACTURACION";
        if (notificadorView != null) {
            setLoader("ESPERE UN MOMENTO", notificadorView);
        }
        String url = NovusConstante.getServer(NovusConstante.SOURCE_END_POINT_CONSULTA_CLIENTE);
        String method = "POST";
        JsonObject json = new JsonObject();
        boolean debug = true;
        json.addProperty("documentoCliente", numeroDocumento);
        json.addProperty("tipoDocumentoCliente", identificador);

        ClientWSAsync client = new ClientWSAsync(funcion, url, method, json, debug);
        JsonObject response = client.esperaRespuesta();
        //CONSULTA VENTAS Y KIOSCO
        if (notificadorView != null) {
            try {
                if (NovusConstante.HAY_INTERNET) {
                    if (response != null) {
                        ClienteFacturaElectronica.jLabel8.setText(response.get("nombreRazonSocial").isJsonNull() ? "SIN NOMBRE DE CLIENTE" : response.get("nombreRazonSocial").getAsString());
                        cerrarLoader = true;
                        datosCliente = NovusUtils.remplazarCaracteresEspecialesClientesFe(response);
                        datosCliente.addProperty("sinSistema", false);
                        JsonObject responseValidacion = validarDatosSapCliente(response);
                        datosCliente.addProperty("errorFaltaCampos", responseValidacion.get("error").getAsBoolean());
                        if (responseValidacion.get("error").getAsBoolean()) {
                            cerrarLoader = false;
                            int longitudTexto = responseValidacion.get("mensaje").getAsString().length();
                            setMensaje("Cliente con datos incompletos, no tiene "
                                    + responseValidacion.get("mensaje").getAsString().substring(0, longitudTexto - 2) + "."
                                    + " \n<br>Por favor comuniquese con el CES #462. ", "", notificadorView);
                        }
                    } else {
                        if (client.getError() != null && client.getStatus() < NovusConstante.STATUS_500) {
                            datosCliente = client.getError();
                            datosCliente.addProperty("sinSistema", false);
                            datosCliente.addProperty("mensaje", client.getError().get("mensaje").getAsString().toUpperCase());
                            datosCliente.addProperty("error", true);
                            datosCliente.addProperty("consultarCliente", true);
                            datosCliente.addProperty("identificacion_cliente", identificador);
                            datosCliente.addProperty("documentoCliente", numeroDocumento);
                            setMensaje(client.getError().get("mensaje").getAsString().toUpperCase(), "", notificadorView);
                            inicio = false;
                        } else {
                            setLoader("ESPERE UN MOMENTO", notificadorView);
                            cerrarLoader = true;
                            datosCliente.addProperty("consultarCliente", true);
                            datosCliente.addProperty("contingencia", true);
                            datosCliente.addProperty("identificacion_cliente", identificador);
                            datosCliente.addProperty("documentoCliente", numeroDocumento);
                            ClienteFacturaElectronica.jLabel8.setText("CLIENTE");
                            inicio = false;

                        }
                    }
                } else {
                    setLoader("ESPERE UN MOMENTO", notificadorView);
                    cerrarLoader = true;
                    datosCliente.addProperty("consultarCliente", true);
                    datosCliente.addProperty("contingencia", true);
                    datosCliente.addProperty("identificacion_cliente", identificador);
                    datosCliente.addProperty("documentoCliente", numeroDocumento);

                }
                datosCliente.addProperty("descripcionTipoDocumento", NovusUtils.tipoDocumento(identificador));
            } catch (Exception e) {
                NovusUtils.printLn("ha ocurrido un error en el proceso de la consulta del cliente -> " + e.getMessage() + "-> ");
            }
            //STATUS PUM (NUEVA VISTA)pp
        } else {
            if (NovusConstante.HAY_INTERNET) {
                if (response != null) {
                    datosCliente = NovusUtils.remplazarCaracteresEspecialesClientesFe(response);
                    JsonObject responseValidacion = validarDatosSapCliente(response);
                    datosCliente.addProperty("errorFaltaCampos", responseValidacion.get("error").getAsBoolean());
                    if (responseValidacion.get("error").getAsBoolean()) {
                        int longitudTexto = responseValidacion.get("mensaje").getAsString().length();
                        datosCliente.addProperty("errorClienteFE", "Cliente con datos incompletos, no tiene "
                                + responseValidacion.get("mensaje").getAsString().substring(0, longitudTexto - 2) + "."
                                + " \n<br>Por favor comuniquese con el CES #462. ");
                    }
                } else {
                    if (client.getError() != null && client.getStatus() < NovusConstante.STATUS_500) {
                        datosCliente.addProperty("mensaje", client.getError().get("mensaje").getAsString().toUpperCase());
                        datosCliente.addProperty("error", true);
                        datosCliente.addProperty("consultarCliente", true);
                        datosCliente.addProperty("identificacion_cliente", identificador);
                        datosCliente.addProperty("documentoCliente", numeroDocumento);
                    } else {
                        datosCliente.addProperty("consultarCliente", true);
                        datosCliente.addProperty("contingencia", true);
                        datosCliente.addProperty("identificacion_cliente", identificador);
                        datosCliente.addProperty("documentoCliente", numeroDocumento);
                    }
                }
            } else {
                datosCliente.addProperty("consultarCliente", true);
                datosCliente.addProperty("contingencia", true);
                datosCliente.addProperty("identificacion_cliente", identificador);
                datosCliente.addProperty("documentoCliente", numeroDocumento);
            }

        }
        return datosCliente;

    }

    private JsonObject validarDatosSapCliente(JsonObject cliente) {
        JsonObject extradata = cliente.get("extraData").getAsJsonObject();
        extradata = extradata.get("datosIdentificacionCliente").getAsJsonObject();
        JsonObject response = new JsonObject();
        response.addProperty("error", false);
        response.addProperty("mensaje", "");
        if (!extradata.has(CODIGO_SAP) || extradata.get(CODIGO_SAP).isJsonNull()) {
            response.addProperty("error", true);
            response.addProperty("mensaje", "código SAP, ");
        }

        if (!extradata.has("codigoTipoIdentificacion") || extradata.get("codigoTipoIdentificacion").isJsonNull()) {
            response.addProperty("error", true);
            response.addProperty("mensaje", response.get("mensaje").getAsString() + "código tipo identificación, ");
        }

        if (!extradata.has("numeroIdentificacion") || extradata.get("numeroIdentificacion").isJsonNull()) {
            response.addProperty("error", true);
            response.addProperty("mensaje", response.get("mensaje").getAsString() + "número identificación, ");
        }

        if (!extradata.has(CORREO_ELECTRONICO) || extradata.get(CORREO_ELECTRONICO).isJsonNull() || extradata.get(CORREO_ELECTRONICO).getAsString().isEmpty()) {
            response.addProperty("error", true);
            response.addProperty("mensaje", response.get("mensaje").getAsString() + "correo electrónico, ");
        }

        if (!extradata.has("regimenFiscal") || extradata.get("regimenFiscal").isJsonNull()) {
            response.addProperty("error", true);
            response.addProperty("mensaje", response.get("mensaje").getAsString() + "regimen fiscal, ");
        }

        if (!cliente.has(TIPO_RESPONSABILIDAD) || cliente.get(TIPO_RESPONSABILIDAD).isJsonNull() || cliente.get(TIPO_RESPONSABILIDAD).getAsString().isEmpty()) {
            response.addProperty("error", true);
            response.addProperty("mensaje", response.get("mensaje").getAsString() + "Tipo responsabilidad, ");
        }

        if (!extradata.has(DATOS_TRIBUTQARIOS_ADQUIRIENTES) || extradata.get(DATOS_TRIBUTQARIOS_ADQUIRIENTES).isJsonNull() || extradata.get(DATOS_TRIBUTQARIOS_ADQUIRIENTES).getAsJsonArray().size() == 0) {
            response.addProperty("error", true);
            response.addProperty("mensaje", response.get("mensaje").getAsString() + "datos tributarios adquirientes, ");
        }
        return response;
    }

    public void setMensaje(String mensaje, String icono, Notificador notificadorView) {
        System.out.println("Set Mensaje");
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
        if (comprobante != null) {
            if (validarComprobante(comprobante)) {
                errorJson.add("datosImpresion", comprobante);
            } else {
                errorJson.addProperty("principal", true);
            }
        }
        errorJson.addProperty("habilitar", true);
        errorJson.addProperty("autoclose", true);
        errorJson.addProperty("venta_en_vivo", false);
        errorJson.addProperty("inicio", inicio);
        errorJson.addProperty("idMovimiento", idMovimiento);
        notificadorView.send(errorJson);
    }

    public void setLoader(String mensaje, Notificador notificadorView) {
        JsonObject errorJson = new JsonObject();
        errorJson.addProperty("mensajeError", mensaje);
        errorJson.addProperty("icono", "/com/firefuel/resources/loader_fac.gif");
        errorJson.addProperty("loader", true);
        errorJson.addProperty("habilitar", false);
        errorJson.addProperty("autoclose", false);
        errorJson.addProperty("venta_en_vivo", false);
        if (cerrarLoader) {
            errorJson.addProperty("cerrarLoader", true);
        }
        notificadorView.send(errorJson);
    }

    // Enviar la factura electronica
    public void enviarFacturaElectronica(JFrame principal,
            Notificador notificadorView, JsonObject cliente,
            JsonObject ventaProducto, boolean canastilla, boolean placa,
            JsonObject objPlaca, JsonObject objManguera, boolean manual) throws ParseException {
        NovusUtils.printLn("enviarFacturaElectronica XRL 1");
        if (placa) {
            // esta parte es para la venta envivo
            FacturaElectronicaVentaEnVivo facVivo = new FacturaElectronicaVentaEnVivo();
            facVivo.datosCliente(cliente, principal, notificadorView, objPlaca, objManguera);

        } else if (NovusConstante.HAY_INTERNET) {
            // para la venta de kiosco y canastilla
            setLoader("ESPERE UN MOMENTO", notificadorView);
            //Aquí tomamos la data de la facturacion
            JsonObject ventaFacturacionElectronica = jsonFacturaiconElectronica(ventaProducto, cliente, canastilla, manual);
            JsonObject objetoVentaFactuacionElectronica = ventaFacturacionElectronica;
            NovusUtils.printLn(Main.ANSI_BLUE + "Objeto de la venta facturacion electronica -> " + Main.ANSI_RESET + Main.ANSI_PURPLE + objetoVentaFactuacionElectronica + Main.ANSI_RESET);
            comprobante = ventaFacturacionElectronica;
            Runnable guardarFacturacionElectronica = () -> {
                if (!configuracionFE.remisionHabilitada()) {
                    MovimientosDao dao = new MovimientosDao();
                    if (dao.guardarFeTransmision(objetoVentaFactuacionElectronica)) {

                        //sube las ventas al servidor
                        JsonObject respuestaProcesarVenta = subirVentas(canastilla, ventaProducto, cliente);
                        System.out.println("Respuesta Procesar Venta :" + respuestaProcesarVenta);
                        idMovimiento = respuestaProcesarVenta.get("movimiento").getAsJsonObject().get("id_movimiento").getAsLong();

                        //Muestra la respuesta del proceso exitoso
                        String respuesta = config.mensajesFE(200, "ventaKCO-CAN");

                        if (foundClient != null && foundClient.isFidelizarMarket()) {
                            String respuestaFidelizacion = enviarFidelizacion(ventaProducto, canastilla, idMovimiento);
                            respuesta = respuesta.concat(" " + respuestaFidelizacion);
                        }

                        clientResponse(respuesta, "/com/firefuel/resources/btOk.png", notificadorView);

                        actualizarConsecutivos(objetoVentaFactuacionElectronica);
                        new VentaEnCursoUseCase("I", NovusUtils.getTipoNegocioComplementario()).execute();
                        new VentaEnCursoUseCase("I", "CAN").execute();
                    } else {
                        NovusUtils.printLn("Json de facturacion electronica error al guardar Transmision -> " + objetoVentaFactuacionElectronica);
                        inicio = true;
                        String respuesta = config.mensajesFE(400, "ventaKCO-CAN");
                        clientResponse(respuesta, "", notificadorView);
                    }
                } else {
                    JsonObject respuestaProcesarVenta = subirVentas(canastilla, ventaProducto, cliente);
                    idMovimiento = respuestaProcesarVenta.get("movimiento").getAsJsonObject().get("id_movimiento").getAsLong();
                    String respuesta = config.mensajesFE(200, "ventaKCO-CAN");

                    if (foundClient != null && foundClient.isFidelizarMarket()) {
                        String respuestaFidelizacion = enviarFidelizacion(ventaProducto, canastilla, idMovimiento);
                        respuesta = respuesta.concat(respuestaFidelizacion);
                    }

                    clientResponse(respuesta, "/com/firefuel/resources/btOk.png", notificadorView);
                    
                    new VentaEnCursoUseCase("I", NovusUtils.getTipoNegocioComplementario()).execute();
                    new VentaEnCursoUseCase("I", "CAN").execute();
                }
                inicio = false;
            };
            CompletableFuture.runAsync(guardarFacturacionElectronica);
        } else {
            enviarSinConexion(ventaProducto, cliente, canastilla, notificadorView, manual);
        }
    }

    private String enviarFidelizacion(JsonObject objFacturaElectronica, boolean tipoNegocio, long idVenta) {
        if (datosClienteFidelizacion != null) {
            return validarEstadaoFidelizacion(objFacturaElectronica, tipoNegocio, idVenta);
        } else {
            return "";
        }
    }

    private String getTipoNegocioFidelizacion(boolean isCanastilla) {
        String tipoNegocio = TipoNegociosFidelizacion.KIOSCO.getTipoNegocio();
        if (isCanastilla) {
            tipoNegocio = TipoNegociosFidelizacion.CANASTILLA.getTipoNegocio();
        } else if (Main.TIPO_NEGOCIO.equals(NovusConstante.PARAMETER_CDL)) {
            tipoNegocio = TipoNegociosFidelizacion.CDL.getTipoNegocio();
        }
        return tipoNegocio;
    }

    private String validarEstadaoFidelizacion(JsonObject objFacturaElectronica, boolean isCanastilla, long idVenta) {
        String negocio = getTipoNegocioFidelizacion(isCanastilla);
        String consecutivo = validarConsecutivo(objFacturaElectronica);
        AcumulacionFidelizacion acumulacionFidelizacion = new AcumulacionFidelizacion();
        String mensaje = " Y FIDELIZACIÓN ENVIADA CON EXITO";
        movimientosBean.setId(idVenta);
        RespuestasAcumulacion response = acumulacionFidelizacion
                .acumular(datosClienteFidelizacion,
                        movimientosBean,
                        ConfigurationFacade.fetchSalePointIdentificator(),
                        negocio, consecutivo);

        return response.getMensajeRespuesta();
    }

    private String validarConsecutivo(JsonObject objFacturaElectronica) {
        if (configuracionFE.remisionHabilitada()) {
            return (mdao.numeroRemision()) + "";
        } else {
            JsonObject objVenta = objFacturaElectronica.get("datos_FE").getAsJsonObject().get("venta").getAsJsonObject();
            return objVenta.get("consecutivoActual").getAsLong() + "-" + objVenta.get("prefijo").getAsString();
        }
    }

    private void enviarSinConexion(JsonObject ventaProducto, JsonObject cliente, boolean canastilla, Notificador notificadorView, boolean manual) throws ParseException {
        NovusUtils.printLn("enviarSinConexion__");
        Gson gson = new Gson();
        JsonObject dataP = jsonFacturaiconElectronica(ventaProducto, cliente, canastilla, manual);
        dataP.addProperty("identificadorMovimiento", 0);
        if (configuracionFE.remisionHabilitada()) {
            comprobante = jsonFacturaiconElectronica(ventaProducto, gson.fromJson(ClienteConsumidorFinal.cliente, JsonObject.class), canastilla, manual);
        } else {
            comprobante = jsonFacturaiconElectronica(ventaProducto, cliente, canastilla, manual);
        }
        JsonObject dataCliente = gson.fromJson(ClienteConsumidorFinal.cliente, JsonObject.class);
        String respuesta = config.mensajesFE(200, "ventaKCO-CAN");
        JsonObject respuestaProcesarVenta = new JsonObject();
        if (cliente.has("extraData")) {
            respuestaProcesarVenta = subirVentas(canastilla, ventaProducto, cliente);
        } else {
            dataCliente.addProperty("consultarCliente", cliente.get("consultarCliente").getAsBoolean());
            dataCliente.addProperty("documentoCliente", cliente.get("documentoCliente").getAsString());
            dataCliente.addProperty("identificacion_cliente", cliente.get("identificacion_cliente").getAsLong());
            JsonObject clienteFE = (configuracionFE.remisionHabilitada() ? gson.fromJson(dataCliente, JsonObject.class) : cliente);
            respuestaProcesarVenta = subirVentas(canastilla, ventaProducto, clienteFE);
        }
        idMovimiento = respuestaProcesarVenta.get("movimiento").getAsJsonObject().get("id_movimiento").getAsLong();

        if (foundClient.isFidelizarMarket()) {
            String respuestaFidelizacion = enviarFidelizacion(ventaProducto, canastilla, idMovimiento);
            respuesta = respuesta.concat(" ").concat(respuestaFidelizacion);
        }

        clientResponse(respuesta, "/com/firefuel/resources/btOk.png", notificadorView);

        if (!configuracionFE.remisionHabilitada()) {
            MovimientosDao dao = new MovimientosDao();
            dao.guardarFeTransmision(dataP);
            actualizarConsecutivos(dataP);
                new VentaEnCursoUseCase("I", NovusUtils.getTipoNegocioComplementario()).execute();
                new VentaEnCursoUseCase("I", "CAN").execute();
            inicio = true;
        }
        // mdao.VentaEnCurso("I", NovusUtils.getTipoNegocioComplementario());
        // mdao.VentaEnCurso("I", "CAN");
        new VentaEnCursoUseCase("I", NovusUtils.getTipoNegocioComplementario()).execute();
        new VentaEnCursoUseCase("I", "CAN").execute();
    }
// data para armar la facturacion electronica
    private JsonObject jsonFacturaiconElectronica(JsonObject data, JsonObject cliente, boolean canastilla, boolean manual) throws ParseException {
        NovusUtils.printLn("jsonFacturaiconElectronica(JsonObject data, JsonObject cliente, boolean canastilla, boolean manual)");
        double subtotal = 0;
        JsonObject producto = new JsonObject();
        JsonObject venta = new JsonObject();
        JsonObject objVenta = data.get("datos_FE").getAsJsonObject().get("venta").getAsJsonObject();

        venta.addProperty("fecha", objVenta.get("fechaTransaccion").getAsString());

        SimpleDateFormat sdfIso = new SimpleDateFormat(NovusConstante.FORMAT_FULL_DATE_ISO_8601);
        Date fecha = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL).parse(objVenta.get("fechaTransaccion").getAsString());
        String fechaIso = sdfIso.format(fecha);

        venta.addProperty("fechaISO", fechaIso);

        venta.addProperty("prefijo", objVenta.get("prefijo").getAsString());
        ConsecutivoBean consecutivoBean = null;
        if (findByParameterUseCase.execute()) {
            venta.addProperty("identificadorTicket", mdao.numeroRemision());
        } else {
            consecutivoBean = validarConsecutivos(canastilla, manual);
        }
        venta.addProperty("id_venta", objVenta.get("identificadorTicket").getAsLong());
        if (consecutivoBean != null) {
            objVenta.addProperty("consecutivoInicial", consecutivoBean.getConsecutivo_inicial_fe());
            objVenta.addProperty("consecutivoActual", consecutivoBean.getConsecutivo_actual_fe());
            objVenta.addProperty("consecutivoFinal", consecutivoBean.getConsecutivo_final_fe());
        }
        venta.addProperty("consecutivo", objVenta.get("consecutivoActual").getAsLong());
        venta.addProperty("consecutivoInicial", objVenta.get("consecutivoInicial").getAsLong());
        venta.addProperty("consecutivoActual", objVenta.get("consecutivoActual").getAsLong());
        venta.addProperty("consecutivoFinal", objVenta.get("consecutivoFinal").getAsLong());
        venta.addProperty("bodegas_id", objVenta.get("identificadorBodega").getAsLong());
        venta.addProperty("empresas_id", objVenta.get("identificacionEstacion").getAsLong());
        venta.addProperty("operacion", objVenta.get("operacion").getAsLong());
        venta.addProperty("movimiento_estado", "");
        venta.addProperty("consecutivo_id", objVenta.get("consecutivo_id").getAsLong());
        venta.addProperty("persona_id", objVenta.get("persona_id").getAsLong());
        venta.addProperty("persona_nit", objVenta.get("persona_nit").getAsString());
        venta.addProperty("persona_nombre", objVenta.get("nombresPersona").getAsString());
        venta.addProperty("tercero_id", 3);

        venta.addProperty("consumo_propio", objVenta.has("consumo_propio") ? objVenta.get("consumo_propio").getAsBoolean() : false);
        if (NovusConstante.HAY_INTERNET) {
            if (!cliente.has("extraData")) {
                cliente = consultarCliente(cliente.get("documentoCliente").getAsString(), cliente.get("identificacion_cliente").getAsLong(), null);
                if (cliente == null) {
                    Gson gson = new Gson();
                    cliente = gson.fromJson(ClienteConsumidorFinal.cliente, JsonObject.class);
                }
            }
            venta.addProperty("tercero_nit", (cliente.has("numeroDocumento") && !cliente.get("numeroDocumento").isJsonNull()) ? cliente.get("numeroDocumento").getAsLong() : cliente.get("documentoCliente").getAsLong());
            venta.addProperty("tercero_nombre", (cliente.has("nombreComercial") && !cliente.get("nombreComercial").isJsonNull()) ? cliente.get("nombreComercial").getAsString() : "");
            venta.addProperty("tercero_correo", (cliente.has("correoElectronico") && !cliente.get("correoElectronico").isJsonNull()) ? cliente.get("correoElectronico").getAsString() : "");
            venta.addProperty("tercero_tipo_persona", !cliente.has("identificadorTipoPersona") ? 0 : cliente.get("identificadorTipoPersona").getAsLong());
            venta.addProperty("tercero_tipo_documento", cliente.has("tipoDocumento") ? cliente.get("tipoDocumento").getAsLong() : cliente.get("identificacion_cliente").getAsLong());
            venta.addProperty("tercero_responsabilidad_fiscal", cliente.has("tipoResponsabilidad") ? cliente.get("tipoResponsabilidad").getAsString() : "");
            venta.addProperty("tercero_codigo_sap", cliente.has("codigoSAP") ? cliente.get("codigoSAP").getAsString() : "");
            venta.addProperty("nombresPersona", (cliente.has("nombreRazonSocial") && !cliente.get("nombreRazonSocial").isJsonNull()) ? cliente.get("nombreRazonSocial").getAsString() : "");
            venta.addProperty("identificacionPersona", (cliente.has("numeroDocumento") && !cliente.get("numeroDocumento").isJsonNull()) ? cliente.get("numeroDocumento").getAsLong() : cliente.get("documentoCliente").getAsLong());
        } else {
            venta.addProperty("tercero_nit", cliente.get("documentoCliente").getAsLong());
            venta.addProperty("tercero_nombre", "");
            venta.addProperty("tercero_correo", "");
            venta.addProperty("tercero_tipo_persona", 0);
            venta.addProperty("tercero_tipo_documento", cliente.get("identificacion_cliente").getAsLong());
            venta.addProperty("tercero_responsabilidad_fiscal", 0);
            venta.addProperty("tercero_codigo_sap", "");
            venta.addProperty("nombresPersona", "");
            venta.addProperty("identificacionPersona", "");
        }
        venta.addProperty("costo_total", objVenta.get("costoTotal").getAsFloat());
        venta.addProperty("venta_total", objVenta.get("ventaTotal").getAsFloat());
        venta.addProperty("impuesto_total", objVenta.get("impuestoTotal").getAsFloat());
        venta.addProperty("descuento_total", objVenta.get("descuentoTotal").getAsDouble());
        venta.addProperty("origen_id", objVenta.get("identificadorOrigen").getAsString());
        venta.addProperty("impreso", objVenta.get("impresoTiquete").getAsString());
        venta.addProperty("create_date", objVenta.get("fechaTransaccion").getAsString());
        venta.addProperty("cajero", objVenta.get("cajero").getAsString());
        venta.addProperty("total_base_imponible", objVenta.get("total_base_imponible").getAsDouble());
        venta.addProperty("total_bruto", objVenta.get("total_bruto").getAsDouble());

        String negocio = canastilla ? NovusConstante.PARAMETER_CAN : NovusUtils.getTipoNegocioComplementario();

        TipoNegocio tipoNegocio = Main.INFO_TIPO_NEGOCIOS.get(NovusUtils.idTipoNegocio(negocio));
        venta.addProperty("tipoNegocio", NovusUtils.idTipoNegocio(negocio));
        venta.addProperty("tipoNegocioValor", tipoNegocio.getValor());

        JsonArray detalle = new JsonArray();
        JsonArray arrjDetalle = data.get("datos_FE").getAsJsonObject().get("detallesVenta").getAsJsonArray();
        for (JsonElement jsonElement : arrjDetalle) {
            JsonObject obj = jsonElement.getAsJsonObject();

            JsonObject objDetalle = new JsonObject();
            objDetalle.addProperty("productos_id", obj.get("identificadorProducto").getAsLong());
            objDetalle.addProperty("productos_plu", Long.parseLong(obj.get("identificacionProducto").getAsString()));
            objDetalle.addProperty("producto_descripcion", obj.get("nombreProducto").getAsString());
            objDetalle.addProperty("cantidad", obj.get("cantidadVenta").getAsFloat());
            objDetalle.addProperty("costo_unidad", 0);
            objDetalle.addProperty("costo_producto", obj.get("costoProducto").getAsFloat());
            objDetalle.addProperty("precio", obj.get("precioProducto").getAsFloat());
            objDetalle.addProperty("unidad", obj.get("unidad").getAsString());
            objDetalle.addProperty("unidad_descripcion", obj.get("unidad_descripcion").getAsString());
            objDetalle.addProperty("producto_tipo", "");
            objDetalle.addProperty("descuento_id", obj.get("identificadorDescuento").getAsLong());
            objDetalle.addProperty("descuento_producto", obj.get("descuentoTotal").getAsFloat());
            objDetalle.add("descuentos", obj.has("descuentos") ? obj.get("descuentos").getAsJsonArray() : new JsonArray());
            objDetalle.addProperty("remoto_id", 0);
            objDetalle.addProperty("sincronizado", "");
            objDetalle.addProperty("subtotal", obj.get("subTotalVenta").getAsDouble());
            objDetalle.addProperty("cortesia", obj.has("cortesia") ? obj.get("cortesia").getAsBoolean() : false);
            objDetalle.addProperty("producto_tipo", obj.has("producto_tipo") ? obj.get("producto_tipo").getAsString() : "");
            if (obj.get("precioFinal") != null) {
                objDetalle.addProperty("precioFinal", obj.get("precioFinal").getAsFloat());
            }
            objDetalle.addProperty("compuesto", "");
            detalle.add(objDetalle);

            JsonObject jsonAtributos = new JsonObject();
            jsonAtributos.addProperty("categoriaId", obj.get("atributos").getAsJsonObject().get("categoriaId").getAsLong());
            jsonAtributos.addProperty("categoriaDescripcion", obj.get("atributos").getAsJsonObject().get("categoriaDescripcion").getAsString());
            jsonAtributos.addProperty("tipo", obj.get("atributos").getAsJsonObject().get("tipo").getAsLong());
            jsonAtributos.addProperty("isElectronica", "true");
            objDetalle.add("atributos", jsonAtributos);

            JsonArray ingredientes = new JsonArray();
            for (JsonElement jsonElement1 : obj.get("ingredientesAplicados").getAsJsonArray()) {
                JsonObject dataIng = jsonElement1.getAsJsonObject();
                JsonObject objIngredientes = new JsonObject();
                objIngredientes.addProperty("id", dataIng.get("id").getAsLong());
                objIngredientes.addProperty("productos_id", dataIng.get("productos_id").getAsLong());
                objIngredientes.addProperty("productos_plu", dataIng.get("productos_plu").getAsString() != null ? dataIng.get("productos_plu").getAsString() : "");
                objIngredientes.addProperty("productos_descripcion", dataIng.get("productos_descripcion").getAsString() != null ? dataIng.get("productos_descripcion").getAsString() : "");
                objIngredientes.addProperty("productos_precio", dataIng.get("productos_precio").getAsFloat());
                objIngredientes.addProperty("identificadorProducto", dataIng.get("identificadorProducto").getAsLong());
                objIngredientes.addProperty("productos_precio_especial", dataIng.get("productos_precio_especial").getAsFloat());
                objIngredientes.addProperty("productos_descuento_porcentaje", dataIng.get("productos_descuento_porcentaje").getAsFloat());
                objIngredientes.addProperty("descuento_base", dataIng.get("descuento_base").getAsFloat());
                objIngredientes.addProperty("productos_descuento_valor", dataIng.get("productos_descuento_valor").getAsFloat());
                objIngredientes.addProperty("cantidad", dataIng.get("cantidad").getAsFloat());
                objIngredientes.addProperty("compuesto_cantidad", 0);
                objIngredientes.add("impuestos", new JsonArray());
                if (!objDetalle.get("cortesia").getAsBoolean()) {
                    ingredientes.add(objIngredientes);
                }
            }
            objDetalle.add("ingredientes", ingredientes);
            if (obj.has("base")) {
                System.out.println(" esta es la base del producto " + obj.get("base").getAsDouble());
                objDetalle.addProperty("base", obj.get("base").getAsDouble());
            } else {
                objDetalle.addProperty("base", obj.get("subTotalVenta").getAsDouble() / obj.get("cantidadVenta").getAsLong());
            }

            JsonArray impuestos = new JsonArray();
            if (obj.get("impuestosAplicados").getAsJsonArray().size() > 0) {
                for (JsonElement jsonElement1 : obj.get("impuestosAplicados").getAsJsonArray()) {
                    JsonObject objI = jsonElement1.getAsJsonObject();
                    if (objI.get("tipoImpuesto").getAsString().equals("%")) {
                        JsonObject objImpuesto = new JsonObject();
                        objImpuesto.addProperty("impuestos_id", objI.get("identificadorImpuesto").getAsLong());
                        objImpuesto.addProperty("tipo", objI.get("tipoImpuesto").getAsString());
                        objImpuesto.addProperty("valor", objI.get("valorImpAplicado").getAsLong());
                        objImpuesto.addProperty("valor_imp", objI.get("valorImpuestoAplicado").getAsDouble());
                        subtotal += objI.get("valorImpuestoAplicado").getAsDouble();
                        impuestos.add(objImpuesto);
                    }
                }
            }

            objDetalle.add("impuestos", impuestos);
        }
        JsonArray pagos = new JsonArray();
        JsonArray arrPagos = data.get("datos_FE").getAsJsonObject().get("pagos").getAsJsonArray();
        for (JsonElement arrPago : arrPagos) {
            JsonObject objP = arrPago.getAsJsonObject();
            JsonObject objPagos = new JsonObject();
            objPagos.addProperty("medios_pagos_id", objP.get("identificacionMediosPagos").getAsLong());
            objPagos.addProperty("valor", objP.get("totalMedioPago").getAsFloat());
            objPagos.addProperty("recibido", objP.get("recibidoMedioPago").getAsFloat());
            objPagos.addProperty("cambio", objP.get("vueltoMedioPago").getAsFloat());
            objPagos.addProperty("medios_pagos_descripcion", objP.get("descripcionMedio").getAsString());
            objPagos.addProperty("formaDePago", objP.get("formaDePago").getAsString());
            pagos.add(objPagos);
        }

        venta.remove("impuesto_total");
        venta.addProperty("impuesto_total", subtotal);
        venta.addProperty("contingencia", NovusConstante.VENTAS_CONTINGENCIA);
        producto.add("venta", venta);
        producto.addProperty("retener", NovusConstante.VENTAS_CONTINGENCIA);
        producto.add("detalle", detalle);
        producto.add("pagos", pagos);
        producto.add("cliente", cliente);
        producto.addProperty("resoluciones", data.get("datos_FE").getAsJsonObject().get("resoluciones").getAsString());
        producto.add("observaciones", data.get("datos_FE").getAsJsonObject().get("observaciones").getAsJsonObject());
        producto.addProperty("tipoEmpresa", data.get("datos_FE").getAsJsonObject().get("TipoEmpresa").getAsString());

        return producto;
    }

// // data para armar la facturacion electronica
//     private JsonObject jsonFacturaiconElectronica(JsonObject data, JsonObject cliente, boolean canastilla, boolean manual) throws ParseException {
//         NovusUtils.printLn("jsonFacturaiconElectronica(JsonObject data, JsonObject cliente, boolean canastilla, boolean manual)");
//         double subtotal = 0;
//         JsonObject producto = new JsonObject();
//         JsonObject venta = new JsonObject();
//         JsonObject objVenta = data.get("datos_FE").getAsJsonObject().get("venta").getAsJsonObject();

//         venta.addProperty("fecha", objVenta.get("fechaTransaccion").getAsString());

//         SimpleDateFormat sdfIso = new SimpleDateFormat(NovusConstante.FORMAT_FULL_DATE_ISO_8601);
//         Date fecha = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL).parse(objVenta.get("fechaTransaccion").getAsString());
//         String fechaIso = sdfIso.format(fecha);

//         venta.addProperty("fechaISO", fechaIso);

//         venta.addProperty("prefijo", objVenta.get("prefijo").getAsString());
//         ConsecutivoBean consecutivoBean = null;
//         if (mdao.remisionActiva()) {
//             venta.addProperty("identificadorTicket", mdao.numeroRemision());
//         } else {
//             consecutivoBean = validarConsecutivos(canastilla, manual);
//         }
//         venta.addProperty("id_venta", objVenta.get("identificadorTicket").getAsLong());
//         if (consecutivoBean != null) {
//             objVenta.addProperty("consecutivoInicial", consecutivoBean.getConsecutivo_inicial_fe());
//             objVenta.addProperty("consecutivoActual", consecutivoBean.getConsecutivo_actual_fe());
//             objVenta.addProperty("consecutivoFinal", consecutivoBean.getConsecutivo_final_fe());
//         }
//         venta.addProperty("consecutivo", objVenta.get("consecutivoActual").getAsLong());
//         venta.addProperty("consecutivoInicial", objVenta.get("consecutivoInicial").getAsLong());
//         venta.addProperty("consecutivoActual", objVenta.get("consecutivoActual").getAsLong());
//         venta.addProperty("consecutivoFinal", objVenta.get("consecutivoFinal").getAsLong());
//         venta.addProperty("bodegas_id", objVenta.get("identificadorBodega").getAsLong());
//         venta.addProperty("empresas_id", objVenta.get("identificacionEstacion").getAsLong());
//         venta.addProperty("operacion", objVenta.get("operacion").getAsLong());
//         venta.addProperty("movimiento_estado", "");
//         venta.addProperty("consecutivo_id", objVenta.get("consecutivo_id").getAsLong());
//         venta.addProperty("persona_id", objVenta.get("persona_id").getAsLong());
//         venta.addProperty("persona_nit", objVenta.get("persona_nit").getAsString());
//         venta.addProperty("persona_nombre", objVenta.get("nombresPersona").getAsString());
//         venta.addProperty("tercero_id", 3);

//         venta.addProperty("consumo_propio", objVenta.has("consumo_propio") ? objVenta.get("consumo_propio").getAsBoolean() : false);
//         if (NovusConstante.HAY_INTERNET) {
//             if (!cliente.has("extraData")) {
//                 cliente = consultarCliente(cliente.get("documentoCliente").getAsString(), cliente.get("identificacion_cliente").getAsLong(), null);
//                 if (cliente == null) {
//                     Gson gson = new Gson();
//                     cliente = gson.fromJson(ClienteConsumidorFinal.cliente, JsonObject.class);
//                 }
//             }
//             venta.addProperty("tercero_nit", (cliente.has("numeroDocumento") && !cliente.get("numeroDocumento").isJsonNull()) ? cliente.get("numeroDocumento").getAsLong() : cliente.get("documentoCliente").getAsLong());
//             venta.addProperty("tercero_nombre", (cliente.has("nombreComercial") && !cliente.get("nombreComercial").isJsonNull()) ? cliente.get("nombreComercial").getAsString() : "");
//             venta.addProperty("tercero_correo", (cliente.has("correoElectronico") && !cliente.get("correoElectronico").isJsonNull()) ? cliente.get("correoElectronico").getAsString() : "");
//             venta.addProperty("tercero_tipo_persona", !cliente.has("identificadorTipoPersona") ? 0 : cliente.get("identificadorTipoPersona").getAsLong());
//             venta.addProperty("tercero_tipo_documento", cliente.has("tipoDocumento") ? cliente.get("tipoDocumento").getAsLong() : cliente.get("identificacion_cliente").getAsLong());
//             venta.addProperty("tercero_responsabilidad_fiscal", cliente.has("tipoResponsabilidad") ? cliente.get("tipoResponsabilidad").getAsString() : "");
//             venta.addProperty("tercero_codigo_sap", cliente.has("codigoSAP") ? cliente.get("codigoSAP").getAsString() : "");
//             venta.addProperty("nombresPersona", (cliente.has("nombreRazonSocial") && !cliente.get("nombreRazonSocial").isJsonNull()) ? cliente.get("nombreRazonSocial").getAsString() : "");
//             venta.addProperty("identificacionPersona", (cliente.has("numeroDocumento") && !cliente.get("numeroDocumento").isJsonNull()) ? cliente.get("numeroDocumento").getAsLong() : cliente.get("documentoCliente").getAsLong());
//         } else {
//             venta.addProperty("tercero_nit", cliente.get("documentoCliente").getAsLong());
//             venta.addProperty("tercero_nombre", "");
//             venta.addProperty("tercero_correo", "");
//             venta.addProperty("tercero_tipo_persona", 0);
//             venta.addProperty("tercero_tipo_documento", cliente.get("identificacion_cliente").getAsLong());
//             venta.addProperty("tercero_responsabilidad_fiscal", 0);
//             venta.addProperty("tercero_codigo_sap", "");
//             venta.addProperty("nombresPersona", "");
//             venta.addProperty("identificacionPersona", "");
//         }
//         venta.addProperty("costo_total", objVenta.get("costoTotal").getAsFloat());
//         venta.addProperty("venta_total", objVenta.get("ventaTotal").getAsFloat());
//         venta.addProperty("impuesto_total", objVenta.get("impuestoTotal").getAsFloat());
//         venta.addProperty("descuento_total", objVenta.get("descuentoTotal").getAsDouble());
//         venta.addProperty("origen_id", objVenta.get("identificadorOrigen").getAsString());
//         venta.addProperty("impreso", objVenta.get("impresoTiquete").getAsString());
//         venta.addProperty("create_date", objVenta.get("fechaTransaccion").getAsString());
//         venta.addProperty("cajero", objVenta.get("cajero").getAsString());
//         venta.addProperty("total_base_imponible", objVenta.get("total_base_imponible").getAsDouble());
//         venta.addProperty("total_bruto", objVenta.get("total_bruto").getAsDouble());

//         String negocio = canastilla ? NovusConstante.PARAMETER_CAN : NovusUtils.getTipoNegocioComplementario();

//         TipoNegocio tipoNegocio = Main.INFO_TIPO_NEGOCIOS.get(NovusUtils.idTipoNegocio(negocio));
//         venta.addProperty("tipoNegocio", NovusUtils.idTipoNegocio(negocio));
//         venta.addProperty("tipoNegocioValor", tipoNegocio.getValor());

//         JsonArray detalle = new JsonArray();
//         JsonArray arrjDetalle = data.get("datos_FE").getAsJsonObject().get("detallesVenta").getAsJsonArray();
//         for (JsonElement jsonElement : arrjDetalle) {
//             JsonObject obj = jsonElement.getAsJsonObject();

//             JsonObject objDetalle = new JsonObject();
//             objDetalle.addProperty("productos_id", obj.get("identificadorProducto").getAsLong());
//             objDetalle.addProperty("productos_plu", Long.parseLong(obj.get("identificacionProducto").getAsString()));
//             objDetalle.addProperty("producto_descripcion", obj.get("nombreProducto").getAsString());
//             objDetalle.addProperty("cantidad", obj.get("cantidadVenta").getAsFloat());
//             objDetalle.addProperty("costo_unidad", 0);
//             objDetalle.addProperty("costo_producto", obj.get("costoProducto").getAsFloat());
//             objDetalle.addProperty("precio", obj.get("precioProducto").getAsFloat());
//             objDetalle.addProperty("unidad", obj.get("unidad").getAsString());
//             objDetalle.addProperty("unidad_descripcion", obj.get("unidad_descripcion").getAsString());
//             objDetalle.addProperty("producto_tipo", "");
//             objDetalle.addProperty("descuento_id", obj.get("identificadorDescuento").getAsLong());
//             objDetalle.addProperty("descuento_producto", obj.get("descuentoTotal").getAsFloat());
//             objDetalle.add("descuentos", obj.has("descuentos") ? obj.get("descuentos").getAsJsonArray() : new JsonArray());
//             objDetalle.addProperty("remoto_id", 0);
//             objDetalle.addProperty("sincronizado", "");
//             objDetalle.addProperty("subtotal", obj.get("subTotalVenta").getAsDouble());
//             objDetalle.addProperty("cortesia", obj.has("cortesia") ? obj.get("cortesia").getAsBoolean() : false);
//             objDetalle.addProperty("producto_tipo", obj.has("producto_tipo") ? obj.get("producto_tipo").getAsString() : "");
//             if (obj.get("precioFinal") != null) {
//                 objDetalle.addProperty("precioFinal", obj.get("precioFinal").getAsFloat());
//             }
//             objDetalle.addProperty("compuesto", "");
//             detalle.add(objDetalle);

//             JsonObject jsonAtributos = new JsonObject();
//             jsonAtributos.addProperty("categoriaId", obj.get("atributos").getAsJsonObject().get("categoriaId").getAsLong());
//             jsonAtributos.addProperty("categoriaDescripcion", obj.get("atributos").getAsJsonObject().get("categoriaDescripcion").getAsString());
//             jsonAtributos.addProperty("tipo", obj.get("atributos").getAsJsonObject().get("tipo").getAsLong());
//             jsonAtributos.addProperty("isElectronica", "true");
//             objDetalle.add("atributos", jsonAtributos);

//             JsonArray ingredientes = new JsonArray();
//             for (JsonElement jsonElement1 : obj.get("ingredientesAplicados").getAsJsonArray()) {
//                 JsonObject dataIng = jsonElement1.getAsJsonObject();
//                 JsonObject objIngredientes = new JsonObject();
//                 objIngredientes.addProperty("id", dataIng.get("id").getAsLong());
//                 objIngredientes.addProperty("productos_id", dataIng.get("productos_id").getAsLong());
//                 objIngredientes.addProperty("productos_plu", dataIng.get("productos_plu").getAsString() != null ? dataIng.get("productos_plu").getAsString() : "");
//                 objIngredientes.addProperty("productos_descripcion", dataIng.get("productos_descripcion").getAsString() != null ? dataIng.get("productos_descripcion").getAsString() : "");
//                 objIngredientes.addProperty("productos_precio", dataIng.get("productos_precio").getAsFloat());
//                 objIngredientes.addProperty("identificadorProducto", dataIng.get("identificadorProducto").getAsLong());
//                 objIngredientes.addProperty("productos_precio_especial", dataIng.get("productos_precio_especial").getAsFloat());
//                 objIngredientes.addProperty("productos_descuento_porcentaje", dataIng.get("productos_descuento_porcentaje").getAsFloat());
//                 objIngredientes.addProperty("descuento_base", dataIng.get("descuento_base").getAsFloat());
//                 objIngredientes.addProperty("productos_descuento_valor", dataIng.get("productos_descuento_valor").getAsFloat());
//                 objIngredientes.addProperty("cantidad", dataIng.get("cantidad").getAsFloat());
//                 objIngredientes.addProperty("compuesto_cantidad", 0);
//                 objIngredientes.add("impuestos", new JsonArray());
//                 if (!objDetalle.get("cortesia").getAsBoolean()) {
//                     ingredientes.add(objIngredientes);
//                 }
//             }
//             objDetalle.add("ingredientes", ingredientes);
//             if (obj.has("base")) {
//                 System.out.println(" esta es la base del producto " + obj.get("base").getAsDouble());
//                 objDetalle.addProperty("base", obj.get("base").getAsDouble());
//             } else {
//                 objDetalle.addProperty("base", obj.get("subTotalVenta").getAsDouble() / obj.get("cantidadVenta").getAsLong());
//             }

//             JsonArray impuestos = new JsonArray();
//             if (obj.get("impuestosAplicados").getAsJsonArray().size() > 0) {
//                 for (JsonElement jsonElement1 : obj.get("impuestosAplicados").getAsJsonArray()) {
//                     JsonObject objI = jsonElement1.getAsJsonObject();
//                     if (objI.get("tipoImpuesto").getAsString().equals("%")) {
//                         JsonObject objImpuesto = new JsonObject();
//                         objImpuesto.addProperty("impuestos_id", objI.get("identificadorImpuesto").getAsLong());
//                         objImpuesto.addProperty("tipo", objI.get("tipoImpuesto").getAsString());
//                         objImpuesto.addProperty("valor", objI.get("valorImpAplicado").getAsLong());
//                         objImpuesto.addProperty("valor_imp", objI.get("valorImpuestoAplicado").getAsDouble());
//                         subtotal += objI.get("valorImpuestoAplicado").getAsDouble();
//                         impuestos.add(objImpuesto);
//                     }
//                 }
//             }

//             objDetalle.add("impuestos", impuestos);
//         }
//         JsonArray pagos = new JsonArray();
//         JsonArray arrPagos = data.get("datos_FE").getAsJsonObject().get("pagos").getAsJsonArray();
//         for (JsonElement arrPago : arrPagos) {
//             JsonObject objP = arrPago.getAsJsonObject();
//             JsonObject objPagos = new JsonObject();
//             objPagos.addProperty("medios_pagos_id", objP.get("identificacionMediosPagos").getAsLong());
//             objPagos.addProperty("valor", objP.get("totalMedioPago").getAsFloat());
//             objPagos.addProperty("recibido", objP.get("recibidoMedioPago").getAsFloat());
//             objPagos.addProperty("cambio", objP.get("vueltoMedioPago").getAsFloat());
//             objPagos.addProperty("medios_pagos_descripcion", objP.get("descripcionMedio").getAsString());
//             objPagos.addProperty("formaDePago", objP.get("formaDePago").getAsString());
//             pagos.add(objPagos);
//         }

//         venta.remove("impuesto_total");
//         venta.addProperty("impuesto_total", subtotal);
//         venta.addProperty("contingencia", NovusConstante.VENTAS_CONTINGENCIA);
//         producto.add("venta", venta);
//         producto.addProperty("retener", NovusConstante.VENTAS_CONTINGENCIA);
//         producto.add("detalle", detalle);
//         producto.add("pagos", pagos);
//         producto.add("cliente", cliente);
//         producto.addProperty("resoluciones", data.get("datos_FE").getAsJsonObject().get("resoluciones").getAsString());
//         producto.add("observaciones", data.get("datos_FE").getAsJsonObject().get("observaciones").getAsJsonObject());
//         producto.addProperty("tipoEmpresa", data.get("datos_FE").getAsJsonObject().get("TipoEmpresa").getAsString());

//         return producto;
//     }

    private ConsecutivoBean validarConsecutivos(boolean canastilla, boolean manual) {
        try {
            String resolucion;
            int tipoDocumento = 31;
            if (canastilla) {
                resolucion = "CAN";
            } else if (Main.SIN_SURTIDOR) {
                resolucion = "CDL";
            } else {
                resolucion = "KSC";
            }
            if (manual) {
                tipoDocumento = 18;
            }
            ConsecutivoBean consecutivo = mdao.getPrefijo(tipoDocumento, resolucion);
            //if (mdao.consecutivoUsado(consecutivo.getPrefijo_fe(), consecutivo.getConsecutivo_actual_fe())) {
            if (new ValidarConsecutivoUsadoUseCase(consecutivo.getPrefijo_fe(), consecutivo.getConsecutivo_actual_fe()).execute()) {
                NovusUtils.printLn("************************** consecutivo usado "
                        + consecutivo.getConsecutivo_actual_fe()
                        + " prefijo " + consecutivo.getPrefijo_fe()
                        + " *********************************");
                consecutivo = mdao.getPrefijo(tipoDocumento, resolucion);
            }
            return consecutivo;
        } catch (DAOException ex) {
            Logger.getLogger(ConsultaClienteEnviarFE.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    // Actualizar consecutivos Electronicos
    public void actualizarConsecutivos(JsonObject json) {
        //MovimientosDao movi = new MovimientosDao();
        long idConsecutivo = json.get("venta").getAsJsonObject().get("consecutivo_id").getAsInt();
        long consecutivoActual = json.get("venta").getAsJsonObject().get("consecutivoActual").getAsLong();
        consecutivoActual += 1;
        //movi.actualizarConsecutivo(idConsecutivo, consecutivoActual);
        new ActualizarConsecutivoUseCase(idConsecutivo,consecutivoActual).execute();
    }

    // respuesta del proceso (modal)
    private void clientResponse(String response, String imagen, Notificador viewNotificador) {
        setMensaje(response, imagen, viewNotificador);
    }

    public JsonObject imprimirComprobante(JsonObject json) {
        ReportesFE reporte = new ReportesFE();
        FacturacionElectronica facturacionElectronica = new FacturacionElectronica();
        if (facturacionElectronica.remisionActiva()) {
            return reporte.printCanastillaRemision(json);
        } else {
            return reporte.printCanastilla(json);
        }
    }

    //mensaje de confirmacion
    void showConfirm(String message, Runnable handler) {
        DialogoConfirmacionMensaje dialog = new DialogoConfirmacionMensaje(confir, true, handler);
        dialog.setMensajes("INFO", message);
        dialog.setVisible(true);
    }

    // subir ventas al servidor
    public JsonObject subirVentas(boolean canastilla, JsonObject venta, JsonObject cliente) {
        System.out.println("subirVentas(boolean canastilla, JsonObject venta, JsonObject cliente)");
        PedidoFacade pedido = new PedidoFacade();
        JsonObject respuestaProcesarVentas = new JsonObject();
        try {
            if (canastilla) {
                mdao.create(movimientosBean, Main.credencial, true);
            } else {
                MovimientosBean kiosco = mdao.createKIOSCO(movimientosBean, Main.credencial, true);
                movimientosBean.setId(kiosco.getId());
            }
        } catch (DAOException e) {
            Logger.getLogger(ConsultaClienteEnviarFE.class.getName()).log(Level.SEVERE, null, e);
        }
        JsonObject respuesta = ventaSubirServer(venta, cliente, movimientosBean, canastilla);
        if (canastilla) {
            respuestaProcesarVentas = pedido.sendCanFE(respuesta);
        } else {
            respuestaProcesarVentas = pedido.sendKioscoFE(respuesta);
        }
        return respuestaProcesarVentas;
    }

    // //Json para subir la venta al servidor de KCO y CAN
    // private JsonObject ventaSubirServer(JsonObject json, JsonObject cliente, MovimientosBean movimiento, boolean canastilla) {

    //     JsonObject ventaTransmison = new JsonObject();
    //     JsonObject respuestaFactura = json.get("datos_FE").getAsJsonObject().get("venta").getAsJsonObject();
    //     boolean consumoPropio = respuestaFactura.has("consumo_propio") ? respuestaFactura.get("consumo_propio").getAsBoolean() : false;
    //     JsonObject venta = new JsonObject();
    //     venta.addProperty("identificadorNegocio", respuestaFactura.get("identificadorNegocio").getAsLong());
    //     venta.addProperty("isElectronica", true);
    //     venta.addProperty("identificacionEstacion", respuestaFactura.get("identificacionEstacion").getAsLong());
    //     venta.addProperty("codigoEstacion", respuestaFactura.get("codigoEstacion").getAsString());
    //     venta.addProperty("prefijo", respuestaFactura.get("prefijo").getAsString());
    //     venta.addProperty("nombreEstacion", respuestaFactura.get("nombreEstacion").getAsString());
    //     venta.addProperty("aliasEstacion", respuestaFactura.get("aliasEstacion").getAsString());
    //     venta.addProperty("identificadorEstacion", respuestaFactura.get("identificadorEstacion").getAsString());
    //     venta.addProperty("identificadorTicketVenta", respuestaFactura.get("consecutivoActual").getAsLong());
    //     venta.addProperty("identificadorTicket", respuestaFactura.get("consecutivo_id").getAsLong());
    //     venta.addProperty("idTransaccionVenta", movimiento.getId());
    //     venta.addProperty("fechaTransaccion", respuestaFactura.get("fechaTransaccion").getAsString());
    //     venta.addProperty("identificadorPromotor", respuestaFactura.get("identificadorPromotor").getAsLong());
    //     venta.addProperty("nombresPromotor", respuestaFactura.get("nombresPromotor").getAsString());
    //     venta.addProperty("apellidosPromotor", respuestaFactura.get("apellidosPromotor").getAsString());
    //     venta.addProperty("identificacionPromotor", respuestaFactura.get("identificacionPromotor").getAsString());
    //     venta.addProperty("identificadorPersona", respuestaFactura.get("identificadorPersona").getAsLong());
    //     venta.addProperty("identificacionPersona", respuestaFactura.get("identificacionPersona").getAsString());
    //     venta.addProperty("nombresPersona", respuestaFactura.get("nombresPersona").getAsString());
    //     venta.addProperty("apellidosPersona", respuestaFactura.get("apellidosPersona").getAsString());
    //     venta.addProperty("identificadorProveedor", respuestaFactura.get("identificadorProveedor").getAsLong());
    //     venta.addProperty("identificadorBodega", respuestaFactura.get("identificadorBodega").getAsLong());
    //     venta.addProperty("nombresBodega", respuestaFactura.get("nombresBodega").getAsString());
    //     venta.addProperty("codigoBodega", respuestaFactura.get("codigoBodega").getAsString());
    //     venta.addProperty("costoTotal", respuestaFactura.get("costoTotal").getAsFloat());
    //     venta.addProperty("ventaTotal", respuestaFactura.get("ventaTotal").getAsFloat());
    //     if (consumoPropio) {
    //         venta.addProperty("ventaTotal", respuestaFactura.get("impuestoTotal").getAsFloat());
    //     }
    //     venta.addProperty("descuentoTotal", respuestaFactura.get("descuentoTotal").getAsFloat());
    //     venta.addProperty("impuestoTotal", respuestaFactura.get("impuestoTotal").getAsFloat());
    //     venta.addProperty("identificadorEquipo", respuestaFactura.get("identificadorEquipo").getAsLong());
    //     venta.addProperty("impresoTiquete", respuestaFactura.get("impresoTiquete").getAsString());
    //     venta.addProperty("usoDolar", respuestaFactura.get("usoDolar").getAsFloat());
    //     venta.addProperty("identificadorJornada", respuestaFactura.get("identificadorJornada").getAsLong());
    //     venta.addProperty("identificadorOrigen", respuestaFactura.get("identificadorOrigen").getAsLong());
    //     JsonObject atributos = new JsonObject();
    //     atributos.addProperty("fidelizada", movimiento.getVentaFidelizada());
    //     String tipoNegocio = canastilla ? NovusConstante.PARAMETER_CAN : NovusUtils.getTipoNegocioComplementario();
    //     atributos.addProperty("tipo_negocio", NovusUtils.idTipoNegocio(tipoNegocio));
    //     if (configuracionFE.remisionHabilitada()) {
    //         venta.addProperty("prefijo", "RM");
    //         venta.addProperty("isElectronica", false);
    //         venta.addProperty("identificadorTicketVenta", mdao.numeroRemision());
    //         venta.addProperty("identificadorTicket", mdao.numeroRemision());
    //         if (!NovusConstante.HAY_INTERNET) {
    //             atributos.addProperty("consultarCliente", true);
    //             Gson gson = new Gson();
    //             cliente = gson.fromJson(ClienteConsumidorFinal.cliente, JsonObject.class);
    //             cliente.addProperty("consultarCliente", true);
    //             venta.addProperty("nombresPersona", cliente.get("nombreRazonSocial").isJsonNull() ? cliente.get("nombreComercial").getAsString() : cliente.get("nombreRazonSocial").getAsString());
    //             venta.addProperty("identificacionPersona", cliente.get("numeroDocumento").getAsLong());
    //         } else {
    //             venta.addProperty("nombresPersona", cliente.get("nombreRazonSocial").isJsonNull() ? cliente.get("nombreComercial").getAsString() : cliente.get("nombreRazonSocial").getAsString());
    //             venta.addProperty("identificacionPersona", cliente.get("numeroDocumento").getAsLong());
    //         }
    //         atributos.addProperty("tipoVenta", 100);
    //         atributos.addProperty("identificadorTicket", mdao.numeroRemision());
    //         atributos.add("detallesBono", respuestaFactura.has("detallesBono") ? respuestaFactura.get("detallesBono").getAsJsonArray() : null);
    //     } else {
    //         atributos.addProperty("identificadorTicket", mdao.numeroRemision());
    //         atributos.addProperty("isElectronica", true);
    //         atributos.addProperty("isContingencia", NovusConstante.VENTAS_CONTINGENCIA);
    //         atributos.add("detallesBono", respuestaFactura.has("detallesBono") ? respuestaFactura.get("detallesBono").getAsJsonArray() : null);
    //     }
    //     int tipo = obtenerTipoDocumento(cliente);
    //     cliente.addProperty("descripcionTipoDocumento", NovusUtils.tipoDocumento(tipo));
    //     atributos.add("cliente", cliente);
    //     venta.add("atributos", atributos);

    //     JsonArray detalleventa = new JsonArray();
    //     Map<Integer, Integer> remotoId = new HashMap<>();
    //     JsonArray detalle = json.get("datos_FE").getAsJsonObject().get("detallesVenta").getAsJsonArray();
    //     remotoId.clear();
    //     for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
    //         MovimientosDetallesBean value = entry.getValue();
    //         remotoId.put(Integer.parseInt(value.getPlu()), Integer.parseInt(value.getRemotoId() + ""));
    //     }
    //     for (JsonElement elementDetalle : detalle) {
    //         JsonObject obj = elementDetalle.getAsJsonObject();
    //         JsonObject objDetalle = new JsonObject();
    //         objDetalle.addProperty("idTransaccionVentaDetalle", remotoId.get(Integer.parseInt(obj.get("identificacionProducto").getAsString())));
    //         objDetalle.addProperty("idTransaccionDetalleVenta", obj.get("idTransaccionDetalleVenta").getAsLong());
    //         objDetalle.addProperty("identificadorProducto", obj.get("identificadorProducto").getAsLong());
    //         objDetalle.addProperty("nombreProducto", obj.get("nombreProducto").getAsString());
    //         objDetalle.addProperty("identificacionProducto", obj.get("identificacionProducto").getAsString());
    //         objDetalle.addProperty("fechaTransaccion", obj.get("fechaTransaccion").getAsString());
    //         objDetalle.addProperty("cantidadVenta", obj.get("cantidadVenta").getAsFloat());
    //         objDetalle.addProperty("identificadorUnidad", obj.get("identificadorUnidad").getAsLong());
    //         objDetalle.addProperty("costoProducto", obj.get("costoProducto").getAsFloat());
    //         objDetalle.addProperty("precioProducto", obj.get("precioVentaFeWeb").getAsFloat());
    //         objDetalle.addProperty("identificadorDescuento", obj.get("identificadorDescuento").getAsLong());
    //         objDetalle.addProperty("descuentoTotal", obj.get("descuentoTotal").getAsFloat());
    //         objDetalle.addProperty("subTotalVenta", obj.get("subTotalVenta").getAsFloat());
    //         JsonObject atributo = new JsonObject();
    //         atributo.addProperty("categoriaId", obj.get("atributos").getAsJsonObject().get("categoriaId").getAsLong());
    //         atributo.addProperty("categoriaDescripcion", obj.get("atributos").getAsJsonObject().get("categoriaDescripcion").getAsString());
    //         atributo.addProperty("tipo", obj.get("atributos").getAsJsonObject().get("tipo").getAsLong());
    //         if (!mdao.remisionActiva()) {
    //             atributo.addProperty("isElectronica", true);
    //         } else {
    //             atributo.addProperty("isElectronica", false);
    //         }

    //         long idPrecioEspecial = mdao.getIdPrecioEspecial(obj.get("identificadorProducto").getAsLong());

    //         if (idPrecioEspecial != 0) {
    //             atributo.addProperty("precio_especial_id", idPrecioEspecial);
    //         }

    //         System.out.println("[ventaSubirServer][obj]" + obj.toString());

    //         objDetalle.add("atributos", atributo);
    //         detalleventa.add(objDetalle);
    //         JsonArray ingredientes = new JsonArray();
    //         for (JsonElement jsonElement1 : obj.get("ingredientesAplicados").getAsJsonArray()) {

    //             JsonObject objItem = jsonElement1.getAsJsonObject();

    //             JsonObject objIngrediente = new JsonObject();
    //             objIngrediente.addProperty("identificadorProducto", objItem.get("id").getAsLong());
    //             objIngrediente.addProperty("cantidadVenta", objItem.get("compuesto_cantidad").getAsFloat() * obj.get("cantidadVenta").getAsFloat());
    //             objIngrediente.addProperty("costo", objItem.get("costo").getAsInt() * obj.get("cantidadVenta").getAsFloat());

    //             ingredientes.add(objIngrediente);
    //         }
    //         objDetalle.add("ingredientesAplicados", ingredientes);

    //         JsonArray impuestos = new JsonArray();
    //         float impuestoConsumoPropio = 0;
    //         for (JsonElement jsonElement1 : obj.get("impuestosAplicados").getAsJsonArray()) {
    //             JsonObject objI = jsonElement1.getAsJsonObject();
    //             JsonObject objImpuesto = new JsonObject();
    //             objImpuesto.addProperty("identificadorImpuesto", objI.get("identificadorImpuesto").getAsLong());
    //             objImpuesto.addProperty("nombreImpuesto", objI.get("nombreImpuesto").getAsString());
    //             objImpuesto.addProperty("tipoImpuesto", objI.get("tipoImpuesto").getAsString());
    //             objImpuesto.addProperty("valorImpAplicado", objI.get("valorImpAplicado").getAsLong());
    //             objImpuesto.addProperty("valorImpuestoAplicado", objI.get("valorImpuestoAplicado").getAsFloat());
    //             if (objI.get("tipoImpuesto").getAsString().equals("%")) {
    //                 impuestoConsumoPropio = impuestoConsumoPropio + objI.get("valorImpuestoAplicado").getAsFloat();
    //             }
    //             if (!consumoPropio) {
    //                 impuestos.add(objImpuesto);
    //             }

    //         }
    //         if (consumoPropio) {
    //             if (obj.get("impuestosAplicados").getAsJsonArray().size() > 0) {
    //                 objDetalle.addProperty("subTotalVenta", impuestoConsumoPropio);
    //             } else {
    //                 objDetalle.addProperty("subTotalVenta", 0);
    //             }
    //         }

    //         objDetalle.add("impuestosAplicados", impuestos);
    //     }

    //     JsonArray pagos = new JsonArray();
    //     JsonArray arrPagos = json.get("datos_FE").getAsJsonObject().get("pagos").getAsJsonArray();
    //     for (JsonElement arrPago : arrPagos) {
    //         JsonObject objP = arrPago.getAsJsonObject();
    //         JsonObject objPagos = new JsonObject();

    //         objPagos.addProperty("identificacionMediosPagos", objP.get("identificacionMediosPagos").getAsLong());
    //         if (consumoPropio) {
    //             objPagos.addProperty("totalMedioPago", respuestaFactura.get("impuestoTotal").getAsFloat());
    //             objPagos.addProperty("recibidoMedioPago", respuestaFactura.get("impuestoTotal").getAsFloat());
    //         } else {
    //             objPagos.addProperty("totalMedioPago", objP.get("totalMedioPago").getAsFloat());
    //             objPagos.addProperty("recibidoMedioPago", objP.get("recibidoMedioPago").getAsFloat());
    //         }
    //         objPagos.addProperty("identificacionComprobante", objP.get("identificacionComprobante").getAsString());
    //         objPagos.addProperty("vueltoMedioPago", objP.get("vueltoMedioPago").getAsFloat());
    //         pagos.add(objPagos);
    //     }
    //     ventaTransmison.add("transaccion", venta);
    //     ventaTransmison.add("detallesVenta", detalleventa);
    //     ventaTransmison.add("mediosPagos", pagos);
    //     if (movimiento.isFidelizar()) {
    //         JsonObject header = movimiento.getDatosFidelizacion().get("body").getAsJsonObject().get("headers").getAsJsonObject();
    //         movimiento.getDatosFidelizacion().get("body").getAsJsonObject().remove("headers");
    //         ventaTransmison.add("fidelizacion", movimiento.getDatosFidelizacion());
    //         ventaTransmison.get("fidelizacion").getAsJsonObject().addProperty("urlAcumulacionCliente", NovusConstante.SECURE_CENTRAL_POINT_ACUMULACION_CLIENTE);
    //         ventaTransmison.get("fidelizacion").getAsJsonObject().addProperty("urlConsultaCliente", NovusConstante.SECURE_CENTRAL_POINT_CONSULTA_CLIENTE);
    //         ventaTransmison.get("fidelizacion").getAsJsonObject().addProperty("identificadorNegocio", movimiento.getEmpresasId());
    //         ventaTransmison.get("fidelizacion").getAsJsonObject().add("headers", header);
    //     }
    //     return ventaTransmison;
    // }

     private JsonObject ventaSubirServer(JsonObject json, JsonObject cliente, MovimientosBean movimiento, boolean canastilla) {

        JsonObject ventaTransmison = new JsonObject();
        JsonObject respuestaFactura = json.get("datos_FE").getAsJsonObject().get("venta").getAsJsonObject();
        boolean consumoPropio = respuestaFactura.has("consumo_propio") ? respuestaFactura.get("consumo_propio").getAsBoolean() : false;
        JsonObject venta = new JsonObject();
        venta.addProperty("identificadorNegocio", respuestaFactura.get("identificadorNegocio").getAsLong());
        venta.addProperty("isElectronica", true);
        venta.addProperty("identificacionEstacion", respuestaFactura.get("identificacionEstacion").getAsLong());
        venta.addProperty("codigoEstacion", respuestaFactura.get("codigoEstacion").getAsString());
        venta.addProperty("prefijo", respuestaFactura.get("prefijo").getAsString());
        venta.addProperty("nombreEstacion", respuestaFactura.get("nombreEstacion").getAsString());
        venta.addProperty("aliasEstacion", respuestaFactura.get("aliasEstacion").getAsString());
        venta.addProperty("identificadorEstacion", respuestaFactura.get("identificadorEstacion").getAsString());
        venta.addProperty("identificadorTicketVenta", respuestaFactura.get("consecutivoActual").getAsLong());
        venta.addProperty("identificadorTicket", respuestaFactura.get("consecutivo_id").getAsLong());
        venta.addProperty("idTransaccionVenta", movimiento.getId());
        venta.addProperty("fechaTransaccion", respuestaFactura.get("fechaTransaccion").getAsString());
        venta.addProperty("identificadorPromotor", respuestaFactura.get("identificadorPromotor").getAsLong());
        venta.addProperty("nombresPromotor", respuestaFactura.get("nombresPromotor").getAsString());
        venta.addProperty("apellidosPromotor", respuestaFactura.get("apellidosPromotor").getAsString());
        venta.addProperty("identificacionPromotor", respuestaFactura.get("identificacionPromotor").getAsString());
        venta.addProperty("identificadorPersona", respuestaFactura.get("identificadorPersona").getAsLong());
        venta.addProperty("identificacionPersona", respuestaFactura.get("identificacionPersona").getAsString());
        venta.addProperty("nombresPersona", respuestaFactura.get("nombresPersona").getAsString());
        venta.addProperty("apellidosPersona", respuestaFactura.get("apellidosPersona").getAsString());
        venta.addProperty("identificadorProveedor", respuestaFactura.get("identificadorProveedor").getAsLong());
        venta.addProperty("identificadorBodega", respuestaFactura.get("identificadorBodega").getAsLong());
        venta.addProperty("nombresBodega", respuestaFactura.get("nombresBodega").getAsString());
        venta.addProperty("codigoBodega", respuestaFactura.get("codigoBodega").getAsString());
        venta.addProperty("costoTotal", respuestaFactura.get("costoTotal").getAsFloat());
        venta.addProperty("ventaTotal", respuestaFactura.get("ventaTotal").getAsFloat());
        if (consumoPropio) {
            venta.addProperty("ventaTotal", respuestaFactura.get("impuestoTotal").getAsFloat());
        }
        venta.addProperty("descuentoTotal", respuestaFactura.get("descuentoTotal").getAsFloat());
        venta.addProperty("impuestoTotal", respuestaFactura.get("impuestoTotal").getAsFloat());
        venta.addProperty("identificadorEquipo", respuestaFactura.get("identificadorEquipo").getAsLong());
        venta.addProperty("impresoTiquete", respuestaFactura.get("impresoTiquete").getAsString());
        venta.addProperty("usoDolar", respuestaFactura.get("usoDolar").getAsFloat());
        venta.addProperty("identificadorJornada", respuestaFactura.get("identificadorJornada").getAsLong());
        venta.addProperty("identificadorOrigen", respuestaFactura.get("identificadorOrigen").getAsLong());
        JsonObject atributos = new JsonObject();
        atributos.addProperty("fidelizada", movimiento.getVentaFidelizada());
        String tipoNegocio = canastilla ? NovusConstante.PARAMETER_CAN : NovusUtils.getTipoNegocioComplementario();
        atributos.addProperty("tipo_negocio", NovusUtils.idTipoNegocio(tipoNegocio));
        if (configuracionFE.remisionHabilitada()) {
            venta.addProperty("prefijo", "RM");
            venta.addProperty("isElectronica", false);
            venta.addProperty("identificadorTicketVenta", mdao.numeroRemision());
            venta.addProperty("identificadorTicket", mdao.numeroRemision());
            if (!NovusConstante.HAY_INTERNET) {
                atributos.addProperty("consultarCliente", true);
                Gson gson = new Gson();
                cliente = gson.fromJson(ClienteConsumidorFinal.cliente, JsonObject.class);
                cliente.addProperty("consultarCliente", true);
                venta.addProperty("nombresPersona", cliente.get("nombreRazonSocial").isJsonNull() ? cliente.get("nombreComercial").getAsString() : cliente.get("nombreRazonSocial").getAsString());
                venta.addProperty("identificacionPersona", cliente.get("numeroDocumento").getAsLong());
            } else {
                venta.addProperty("nombresPersona", cliente.get("nombreRazonSocial").isJsonNull() ? cliente.get("nombreComercial").getAsString() : cliente.get("nombreRazonSocial").getAsString());
                venta.addProperty("identificacionPersona", cliente.get("numeroDocumento").getAsLong());
            }
            atributos.addProperty("tipoVenta", 100);
            atributos.addProperty("identificadorTicket", mdao.numeroRemision());
            atributos.add("detallesBono", respuestaFactura.has("detallesBono") ? respuestaFactura.get("detallesBono").getAsJsonArray() : null);
        } else {
            atributos.addProperty("identificadorTicket", mdao.numeroRemision());
            atributos.addProperty("isElectronica", true);
            atributos.addProperty("isContingencia", NovusConstante.VENTAS_CONTINGENCIA);
            atributos.add("detallesBono", respuestaFactura.has("detallesBono") ? respuestaFactura.get("detallesBono").getAsJsonArray() : null);
        }
        int tipo = obtenerTipoDocumento(cliente);
        cliente.addProperty("descripcionTipoDocumento", NovusUtils.tipoDocumento(tipo));
        atributos.add("cliente", cliente);
        venta.add("atributos", atributos);

        JsonArray detalleventa = new JsonArray();
        Map<Integer, Integer> remotoId = new HashMap<>();
        JsonArray detalle = json.get("datos_FE").getAsJsonObject().get("detallesVenta").getAsJsonArray();
        remotoId.clear();
        for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
            MovimientosDetallesBean value = entry.getValue();
            remotoId.put(Integer.parseInt(value.getPlu()), Integer.parseInt(value.getRemotoId() + ""));
        }
        for (JsonElement elementDetalle : detalle) {
            JsonObject obj = elementDetalle.getAsJsonObject();
            JsonObject objDetalle = new JsonObject();
            objDetalle.addProperty("idTransaccionVentaDetalle", remotoId.get(Integer.parseInt(obj.get("identificacionProducto").getAsString())));
            objDetalle.addProperty("idTransaccionDetalleVenta", obj.get("idTransaccionDetalleVenta").getAsLong());
            objDetalle.addProperty("identificadorProducto", obj.get("identificadorProducto").getAsLong());
            objDetalle.addProperty("nombreProducto", obj.get("nombreProducto").getAsString());
            objDetalle.addProperty("identificacionProducto", obj.get("identificacionProducto").getAsString());
            objDetalle.addProperty("fechaTransaccion", obj.get("fechaTransaccion").getAsString());
            objDetalle.addProperty("cantidadVenta", obj.get("cantidadVenta").getAsFloat());
            objDetalle.addProperty("identificadorUnidad", obj.get("identificadorUnidad").getAsLong());
            objDetalle.addProperty("costoProducto", obj.get("costoProducto").getAsFloat());
            objDetalle.addProperty("precioProducto", obj.get("precioVentaFeWeb").getAsFloat());
            objDetalle.addProperty("identificadorDescuento", obj.get("identificadorDescuento").getAsLong());
            objDetalle.addProperty("descuentoTotal", obj.get("descuentoTotal").getAsFloat());
            objDetalle.addProperty("subTotalVenta", obj.get("subTotalVenta").getAsFloat());
            JsonObject atributo = new JsonObject();
            atributo.addProperty("categoriaId", obj.get("atributos").getAsJsonObject().get("categoriaId").getAsLong());
            atributo.addProperty("categoriaDescripcion", obj.get("atributos").getAsJsonObject().get("categoriaDescripcion").getAsString());
            atributo.addProperty("tipo", obj.get("atributos").getAsJsonObject().get("tipo").getAsLong());
            if (!findByParameterUseCase.execute()) {
                atributo.addProperty("isElectronica", true);
            } else {
                atributo.addProperty("isElectronica", false);
            }

            long idPrecioEspecial = mdao.getIdPrecioEspecial(obj.get("identificadorProducto").getAsLong());

            if (idPrecioEspecial != 0) {
                atributo.addProperty("precio_especial_id", idPrecioEspecial);
            }

            System.out.println("[ventaSubirServer][obj]" + obj.toString());

            objDetalle.add("atributos", atributo);
            detalleventa.add(objDetalle);
            JsonArray ingredientes = new JsonArray();
            for (JsonElement jsonElement1 : obj.get("ingredientesAplicados").getAsJsonArray()) {

                JsonObject objItem = jsonElement1.getAsJsonObject();

                JsonObject objIngrediente = new JsonObject();
                objIngrediente.addProperty("identificadorProducto", objItem.get("id").getAsLong());
                objIngrediente.addProperty("cantidadVenta", objItem.get("compuesto_cantidad").getAsFloat() * obj.get("cantidadVenta").getAsFloat());
                objIngrediente.addProperty("costo", objItem.get("costo").getAsInt() * obj.get("cantidadVenta").getAsFloat());

                ingredientes.add(objIngrediente);
            }
            objDetalle.add("ingredientesAplicados", ingredientes);

            JsonArray impuestos = new JsonArray();
            float impuestoConsumoPropio = 0;
            for (JsonElement jsonElement1 : obj.get("impuestosAplicados").getAsJsonArray()) {
                JsonObject objI = jsonElement1.getAsJsonObject();
                JsonObject objImpuesto = new JsonObject();
                objImpuesto.addProperty("identificadorImpuesto", objI.get("identificadorImpuesto").getAsLong());
                objImpuesto.addProperty("nombreImpuesto", objI.get("nombreImpuesto").getAsString());
                objImpuesto.addProperty("tipoImpuesto", objI.get("tipoImpuesto").getAsString());
                objImpuesto.addProperty("valorImpAplicado", objI.get("valorImpAplicado").getAsLong());
                objImpuesto.addProperty("valorImpuestoAplicado", objI.get("valorImpuestoAplicado").getAsFloat());
                if (objI.get("tipoImpuesto").getAsString().equals("%")) {
                    impuestoConsumoPropio = impuestoConsumoPropio + objI.get("valorImpuestoAplicado").getAsFloat();
                }
                if (!consumoPropio) {
                    impuestos.add(objImpuesto);
                }

            }
            if (consumoPropio) {
                if (obj.get("impuestosAplicados").getAsJsonArray().size() > 0) {
                    objDetalle.addProperty("subTotalVenta", impuestoConsumoPropio);
                } else {
                    objDetalle.addProperty("subTotalVenta", 0);
                }
            }

            objDetalle.add("impuestosAplicados", impuestos);
        }

        JsonArray pagos = new JsonArray();
        JsonArray arrPagos = json.get("datos_FE").getAsJsonObject().get("pagos").getAsJsonArray();
        for (JsonElement arrPago : arrPagos) {
            JsonObject objP = arrPago.getAsJsonObject();
            JsonObject objPagos = new JsonObject();

            objPagos.addProperty("identificacionMediosPagos", objP.get("identificacionMediosPagos").getAsLong());
            if (consumoPropio) {
                objPagos.addProperty("totalMedioPago", respuestaFactura.get("impuestoTotal").getAsFloat());
                objPagos.addProperty("recibidoMedioPago", respuestaFactura.get("impuestoTotal").getAsFloat());
            } else {
                objPagos.addProperty("totalMedioPago", objP.get("totalMedioPago").getAsFloat());
                objPagos.addProperty("recibidoMedioPago", objP.get("recibidoMedioPago").getAsFloat());
            }
            objPagos.addProperty("identificacionComprobante", objP.get("identificacionComprobante").getAsString());
            objPagos.addProperty("vueltoMedioPago", objP.get("vueltoMedioPago").getAsFloat());
            pagos.add(objPagos);
        }
        ventaTransmison.add("transaccion", venta);
        ventaTransmison.add("detallesVenta", detalleventa);
        ventaTransmison.add("mediosPagos", pagos);
        if (movimiento.isFidelizar()) {
            JsonObject header = movimiento.getDatosFidelizacion().get("body").getAsJsonObject().get("headers").getAsJsonObject();
            movimiento.getDatosFidelizacion().get("body").getAsJsonObject().remove("headers");
            ventaTransmison.add("fidelizacion", movimiento.getDatosFidelizacion());
            ventaTransmison.get("fidelizacion").getAsJsonObject().addProperty("urlAcumulacionCliente", NovusConstante.SECURE_CENTRAL_POINT_ACUMULACION_CLIENTE);
            ventaTransmison.get("fidelizacion").getAsJsonObject().addProperty("urlConsultaCliente", NovusConstante.SECURE_CENTRAL_POINT_CONSULTA_CLIENTE);
            ventaTransmison.get("fidelizacion").getAsJsonObject().addProperty("identificadorNegocio", movimiento.getEmpresasId());
            ventaTransmison.get("fidelizacion").getAsJsonObject().add("headers", header);
        }
        return ventaTransmison;
    }

    private boolean validarComprobante(JsonObject comprobante) {
        boolean imprimir = false;

        float total = 0;
        for (JsonElement elemt : comprobante.get("detalle").getAsJsonArray()) {
            JsonObject data = elemt.getAsJsonObject();
            if (data.get("cortesia").getAsBoolean() && !data.get("cortesia").isJsonNull()) {
                total += 0;
            } else {
                total += data.get("subtotal").getAsFloat();
            }
        }
        if (total > 0) {
            imprimir = true;
            inicio = true;
        }
        return imprimir;
    }

    private int obtenerTipoDocumento(JsonObject cliente) {
        if (cliente != null) {
            if (cliente.has("tipoDocumento") && !cliente.get("tipoDocumento").isJsonNull()) {
                return cliente.get("tipoDocumento").getAsInt();
            } else if (cliente.has("identificacion_cliente") && !cliente.get("identificacion_cliente").isJsonNull()) {
                return cliente.get("identificacion_cliente").getAsInt();
            }
        }
        return 0;
    }

    public FoundClient getFoundClient() {
        return foundClient;
    }

    public void setFoundClient(FoundClient foundClient) {
        this.foundClient = foundClient;
    }

}
