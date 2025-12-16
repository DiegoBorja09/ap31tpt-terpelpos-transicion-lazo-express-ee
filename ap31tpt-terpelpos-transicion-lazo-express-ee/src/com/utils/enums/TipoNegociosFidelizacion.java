/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.utils.enums;

/**
 *
 * @author Devitech
 */
public enum TipoNegociosFidelizacion {
    TIENDAS_DE_CONVENIENCIA("TDC"),
    KIOSCO("KCO"),
    CANASTILLA("CLA"),
    COMBUSTIBLE("EDS"),
    CDL("CDL");
    
    String tipoNegocio = "";
    private TipoNegociosFidelizacion(String negocio){
        this.tipoNegocio = negocio;
    }
    
    public String getTipoNegocio (){
        return this.tipoNegocio;
    }
}
