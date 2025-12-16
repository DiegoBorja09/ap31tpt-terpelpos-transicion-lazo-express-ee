/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.firefuel.facturacion.electronica;

/**
 *
 * @author Devitech
 */
public enum DatosClientesRequeridos {
    
    CODIGO_SAP("codigoSAP"),
    CODIGO_TIPO_IDENTIFICAION("codigoTipoIdentificacion"),
    NUMERO_IDENTIFICACION("numeroIdentificacion"),
    CORREO_ELECTRONICO("correoElectronico"),
    REGIMEN_FISCAL("regimenFiscal"),
    TIPO_RESPONSABILIDAD("tipoResponsabilidad"),
    DATOS_TRIBUTARIOS_ADQUIRIENTE("datosTributariosAdquirente");
    
    private String propiedad = "";
    private DatosClientesRequeridos(String propiedad){
        this.propiedad = propiedad;
    }
    
    public String getPropiedad(){
        return this.propiedad;
    }
}
