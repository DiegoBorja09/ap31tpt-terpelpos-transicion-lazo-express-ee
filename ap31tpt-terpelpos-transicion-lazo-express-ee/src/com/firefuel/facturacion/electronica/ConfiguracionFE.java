/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.facturacion.electronica;

import com.application.useCases.parametros.GetMensajesComprobanteUseCase;
import com.application.useCases.parametros.GetMensajesFEUseCase;
import com.dao.MovimientosDao;
import com.google.gson.JsonObject;
import com.application.useCases.wacherparametros.FindByParameterUseCase;
import com.application.commons.CtWacherParametrosEnum;

import java.util.logging.Logger;

/**
 *
 * @author Devitech
 */
public class ConfiguracionFE {

    FindByParameterUseCase findByParameterUseCase = new FindByParameterUseCase(CtWacherParametrosEnum.CODIGO.getColumnName(), "REMISION");
    MovimientosDao mdao = new MovimientosDao();
    static final String EXTITO_ENVIO_FE= "exitoEnvioFE";
    static final String EXTITO_CONVERSION_FE = "exitoConversionFE";
    static final String EXTITO_NOTA_CREDITO = "exitoNotaCredito";
    static final String ERROR_ENVIO_FE = "errorEnvioFE";
    static final String ERROR_CONVERSION_FE = "errorConversionFE";
    static final String ERROR_NOTA_CREDITO = "errorNotaCredito";
    static final Logger logger = Logger.getLogger(ConfiguracionFE.class.getName());

    /*
    metodo para consulta de vensimiento de resoluciones 
    destino = este se usará para saber en que base de datos buscar
        1 = lazoexpresscore 
        2 = lazoexpressregistry
    tipo = kiosco, canastilla y combustible
        
     */
    public JsonObject vencimientoDeResoluciones(int destino, String tipo) {
        JsonObject datos;
        if (destino == 2) {
            datos = mdao.fechaDevencimiento(31, tipo, "lazoexpressregistry");
        } else {
            boolean facExt = mdao.facturacionExterna();
            if (facExt) {
                datos = mdao.fechaDevencimiento(31, "CAN", "lazoexpressregistry");
            } else {
                datos = mdao.fechaDevencimiento(31, tipo, "lazoexpresscore");
            }
        }
        return datos;
    }

    /**
    metodo para consultar los mensajes de la facturacion electronica   
    en el parametro tipo indicare que mensaje debe mostrar<br>
    <b>operacion:</b><br>
    <b>"ventaKCO-CAN"-></b> Kiosco y canastilla<br> 
    <b>"conversion-fe"-></b> para cuando se genera una conversion de factura electronica<br>
    <b>"nota-credito"-></b> para cuando se genere una nota credito<br>
     **/
    public String mensajesFE(int tipo, String operacion) {
        String mensaje = "";
        //JsonObject data = mdao.mensajesFE();
        JsonObject data = new GetMensajesFEUseCase().execute();
        
        if (tipo == 200) {
            switch (operacion) {
                case "ventaKCO-CAN":
                    mensaje = !data.has(EXTITO_ENVIO_FE) || data.get(EXTITO_ENVIO_FE).isJsonNull() ? "FACTURA ELECTRONICA ENVIADA CON EXITO" :  data.get(EXTITO_ENVIO_FE).getAsString();
                    break;
                case "conversion-fe":
                    mensaje = !data.has(EXTITO_CONVERSION_FE) || data.get(EXTITO_CONVERSION_FE).isJsonNull() ? "FACTURA CONVERTIDA CON EXITO" : data.get(EXTITO_CONVERSION_FE).getAsString();
                    break;
                case "nota-credito":
                    mensaje = !data.has(EXTITO_NOTA_CREDITO) || data.get(EXTITO_NOTA_CREDITO).isJsonNull() ? "NOTA CREDITO GENERADA CON EXITO" : data.get(EXTITO_NOTA_CREDITO).getAsString();
                    break;
                default:
                    throw new AssertionError();
            }
        } else {
            switch (operacion) {
                 case "ventaKCO-CAN":
                    mensaje = !data.has(ERROR_ENVIO_FE) ||  data.get(ERROR_ENVIO_FE).isJsonNull() ? "ERROR AL ENVIAR LA FACTURA ELECTRONICA" : data.get(ERROR_ENVIO_FE).getAsString();
                    break;
                case "conversion-fe":
                    mensaje = !data.has(ERROR_CONVERSION_FE) || data.get(ERROR_CONVERSION_FE).isJsonNull() ? "ERROR AL GENRAR LA CONVERSION DE LA FACTURA" : data.get(ERROR_CONVERSION_FE).getAsString();
                    break;
                case "nota-credito":
                    mensaje = !data.has(ERROR_NOTA_CREDITO) || data.get(ERROR_NOTA_CREDITO).isJsonNull() ? "ERROR AL GENERAR LA NOTA CREDITO" : data.get(ERROR_NOTA_CREDITO).getAsString();
                    break;
                default:
                    throw new AssertionError();
            }
       } 
        mensaje = mensaje.substring(0, 1).toUpperCase()
                        + mensaje.substring(1).toLowerCase();
        return mensaje;
    }
    
    public String mensajeComprobantes() {
        //JsonObject data = mdao.mensajesComprobante();
        JsonObject data = new GetMensajesComprobanteUseCase().execute();
        return !data.has("impresionComprobante") || data.get("impresionComprobante").isJsonNull() ? "ESTE DOCUMENTO NO CONSTITUYE \n UNA FACTURA ELECTRONICA" :  data.get("impresionComprobante").getAsString();
    }
    
    // public boolean remisionHabilitada(){
    //     return mdao.remisionActiva();
    // }

     public boolean remisionHabilitada(){
        return findByParameterUseCase.execute();
    }
   
}
