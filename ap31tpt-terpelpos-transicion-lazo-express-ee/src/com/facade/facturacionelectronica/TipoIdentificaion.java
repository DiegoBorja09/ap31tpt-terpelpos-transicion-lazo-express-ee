/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.facade.facturacionelectronica;

/**
 *
 * @author Devitech
 */
public class TipoIdentificaion {

    private String tipoDocumento;
    private long codigoTipoDocumento;
    private boolean aplicaFidelizacion;
    private String caracteresPermitidos;
    private int cantidadCaracteres;

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public long getCodigoTipoDocumento() {
        return codigoTipoDocumento;
    }

    public void setCodigoTipoDocumento(long codigoTipoDocumento) {
        this.codigoTipoDocumento = codigoTipoDocumento;
    }

    public boolean isAplicaFidelizacion() {
        return aplicaFidelizacion;
    }

    public void setAplicaFidelizacion(boolean aplicaFidelizacion) {
        this.aplicaFidelizacion = aplicaFidelizacion;
    }

    public String getCaracteresPermitidos() {
        return caracteresPermitidos;
    }

    public void setCaracteresPermitidos(String caracteresPermitidos) {
        this.caracteresPermitidos = caracteresPermitidos;
    }

    public int getCantidadCaracteres() {
        return cantidadCaracteres;
    }

    public void setCantidadCaracteres(int cantidadCaracteres) {
        this.cantidadCaracteres = cantidadCaracteres;
    }
}
