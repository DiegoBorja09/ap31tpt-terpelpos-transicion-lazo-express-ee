/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.firefuel.datafonos;

/**
 *
 * @author Devitech
 */
public enum ParametrizacionDatafanos {
    CODIGO_COMERCIO("CODIGO_COMERCIO"),
    URL_PAGO("URL_PAGO"),
    URL_ANULACION("URL_ANULACION"),
    URL_BORRAR_TRANSACION("URL_BORRAR_TRANSACION"),
    URL_CONSULTAR_ESTADO("URL_CONSULTAR_ESTADO"),
    URL_AUTENTICACION("URL_AUTENTICACION"),
    AUTENTICACION_CLIENTE_ID("AUTENTICACION_CLIENTE_ID"),
    AUTENTICACION_USUARIO("AUTENTICACION_USUARIO"),
    AUTENTICACION_CLAVE("AUTENTICACION_CLAVE"),
    AUTENTICACION_TIPO_AUTENTICACION("AUTENTICACION_TIPO_AUTENTICACION"),
    AUTENTICACION_CLIENTE_SECRETO("AUTENTICACION_CLIENTE_SECRETO"),
    CLAVE_SUPERVISOR("CLAVE_SUPERVISOR");
    
    private String parametros;
    private ParametrizacionDatafanos(String parametros) {
        this.parametros = parametros;
    }
    
    public String getValor(){
        return this.parametros;
    }

}
