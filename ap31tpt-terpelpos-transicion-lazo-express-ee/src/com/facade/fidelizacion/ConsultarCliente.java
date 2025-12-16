
package com.facade.fidelizacion;

import com.WT2.loyalty.domain.entities.beans.FoundClient;
import com.WT2.loyalty.presentation.handler.ConsultarClienteHandler;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.EquipoDao;
import com.dao.fidelizacionDAO.FidelizacionDao;
import com.google.gson.JsonObject;
import com.utils.enums.TipoNegociosFidelizacion;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class ConsultarCliente {

    FidelizacionDao fidelizacionDao;
    private static final String STATUS = "status";
    private static final String MENSAJE = "mensaje";
    private static final String ADICIONAL = "adicional";
    private static final String EXISTE_CLIENTE = "existeCliente";
    private static final String NOMBRE_CLIENTE = "nombreCliente";
    private static final String MENSAJE_ERROR = "mensajeError";
    private static final String DATOS_CLIENTES = "datosCliente";
    private static final String CODIGO_ERROR = "codigoError";
    SimpleDateFormat sdf;
    SimpleDateFormat sdfISO;
    FoundClient foundClient;
    EquipoDao equipoDao;
    ConsultarClienteHandler consultarHandler;


    public ConsultarCliente() {
        this.fidelizacionDao = new FidelizacionDao();
        this.sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
        this.sdfISO = new SimpleDateFormat(NovusConstante.FORMAT_DATE_ISO);
        this.foundClient = new FoundClient();
        this.equipoDao = new EquipoDao();
        this.consultarHandler = new ConsultarClienteHandler();
    }


    public FoundClient execute(String tipoDocumento, String numeroDocumento, String negocio) {
        String identificadorPuntoDeVenta = identificaionPuntoDeVenta(negocio);
        if (identificadorPuntoDeVenta != null) {
            return validarCamposWT2(tipoDocumento, numeroDocumento, negocio);
        }
        return foundClient;

    }


    private String identificaionPuntoDeVenta(String negocio) {
        if (negocio.equals(TipoNegociosFidelizacion.COMBUSTIBLE.getTipoNegocio())) {
            return this.equipoDao.getIdentificacionPuntoVenta();
        } else {
            return this.fidelizacionDao.indentficadorPuntoVenta(negocio);
        }
    }


    //METODOS TESTING NUEVO CONSULTAR CLIENTE
    private FoundClient validarCamposWT2(String tipoDocumento, String numeroDocumento, String identificadorPuntoDeVenta) {
        JsonObject datosCliente = new JsonObject();
        datosCliente.addProperty("tipoDocumento", tipoDocumento);
        datosCliente.addProperty("numeroDocumento", numeroDocumento);
        return this.informationCliente(datosCliente, identificadorPuntoDeVenta);

    }

    //METODOS TESTING NUEVO CONSULTAR CLIENTE
    private FoundClient informationCliente(JsonObject data, String tipoDeSitioVenta) {
        Map<String, String> params = new HashMap<>();
        params.put("identificacionPuntoVenta", tipoDeSitioVenta);
        params.put("codigoTipoIdentificacion", data.get("tipoDocumento").getAsString());
        params.put("identifier", data.get("numeroDocumento").getAsString());
        FoundClient foundClient = consultarHandler.execute(params);
        return foundClient;
    }

    private JsonObject validarRespuesta(JsonObject response, JsonObject objCliente) {
        if (response != null) {
            JsonObject datosCliente = new JsonObject();
            datosCliente.add(DATOS_CLIENTES, objCliente);
            datosCliente.addProperty(STATUS, 200);
            switch (validarCodigoRespuesta(response)) {
                case 20000:
                    datosCliente.addProperty(NOMBRE_CLIENTE, response.get(NOMBRE_CLIENTE).getAsString().toUpperCase());
                    datosCliente.addProperty(MENSAJE, response.get("mensajeRespuesta").getAsString());
                    datosCliente.addProperty(EXISTE_CLIENTE, true);
                    datosCliente.addProperty(ADICIONAL, "");
                    break;
                case 50000:
                    datosCliente.add(DATOS_CLIENTES, objCliente);
                    JsonObject cliente = new JsonObject();
                    datosCliente.addProperty(STATUS, 600);
                    datosCliente.addProperty(NOMBRE_CLIENTE, "NO REGISTRADO");
                    datosCliente.addProperty(EXISTE_CLIENTE, true);
                    datosCliente.addProperty(ADICIONAL, validacionMensaje(response));
                    datosCliente.addProperty(MENSAJE, validacionMensaje(response));
                    cliente.addProperty(MENSAJE, "sin conexion a internet");
                    datosCliente.add("responseCliente", cliente);
                    break;
                case 400:
                    datosCliente.addProperty(STATUS, 200);
                    datosCliente.addProperty(NOMBRE_CLIENTE, "NO REGISTRADO");
                    datosCliente.addProperty(MENSAJE, validacionMensaje(response));
                    datosCliente.addProperty(EXISTE_CLIENTE, false);
                    datosCliente.addProperty(ADICIONAL, "Ingrese otro núnero de documento");
                    break;
                default:
                    datosCliente.addProperty(NOMBRE_CLIENTE, "NO REGISTRADO");
                    datosCliente.addProperty(MENSAJE, validacionMensaje(response));
                    datosCliente.addProperty(EXISTE_CLIENTE, false);
                    datosCliente.addProperty(ADICIONAL, "Ingrese otro núnero de documento");
            }
            return datosCliente;
        } else {
            JsonObject datosCliente = new JsonObject();
            datosCliente.addProperty(NOMBRE_CLIENTE, "No hay conexión con el Motor de Fidelización");
            datosCliente.addProperty(MENSAJE, "No hay conexión con el Motor de Fidelización");
            datosCliente.addProperty(EXISTE_CLIENTE, false);
            datosCliente.addProperty(ADICIONAL, "");
            return datosCliente;
        }
    }

    private int validarCodigoRespuesta(JsonObject response) {
        if (response.has("codigoRespuesta") && response.get("codigoRespuesta").getAsString().equals("20000")) {
            return 20000;
        } else if (response.has(CODIGO_ERROR) && response.get(CODIGO_ERROR).getAsString().equals("50000")) {
            return 50000;
        } else if (response.has(CODIGO_ERROR) && response.get(CODIGO_ERROR).getAsString().equals("99")) {
            return 400;
        } else {
            return 400;
        }
    }

    private String validacionMensaje(JsonObject response) {
        String mensaje;
        try {
            if (response.has(MENSAJE_ERROR) && response.get(MENSAJE_ERROR).isJsonPrimitive() && response.get(MENSAJE_ERROR).getAsString() != null) {
                mensaje = response.get(MENSAJE_ERROR).getAsString();
                return mensaje;
            }

            if (response.has(MENSAJE) && response.get(MENSAJE).isJsonPrimitive() && response.get(MENSAJE).getAsString() != null) {
                mensaje = response.get(MENSAJE).getAsString();
                return mensaje;
            }
            mensaje = "No hay conexión con el Motor de Fidelización.";
        } catch (Exception e) {
            mensaje = "No hay conexión con el Motor de Fidelización.";
        }

        return mensaje;
    }

    public JsonObject respuesta(String mensaje, int status) {
        JsonObject response = new JsonObject();
        response.addProperty(STATUS, status);
        response.addProperty(MENSAJE, mensaje);
        return response;
    }
}
