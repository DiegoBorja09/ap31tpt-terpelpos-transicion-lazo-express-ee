/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bean;

import java.util.Date;


public class EquiposAutorizacionBean {

    private long idEquipo;
    private String macEquipo;
    private String tokenEquipo;
    private Date ultimaSincronizacion;
    private Date primeraAutorizacion;
    private String estado;
    private String pinSincronizacion;
    private String serialEquipo;
    private String ipEquipo;
    private String tipoEquipo;

    public Date getPrimeraAutorizacion() {
        return primeraAutorizacion;
    }

    public void setPrimeraAutorizacion(Date primeraAutorizacion) {
        this.primeraAutorizacion = primeraAutorizacion;
    }

    public String getTipoEquipo() {
        return tipoEquipo;
    }

    public void setTipoEquipo(String tipoEquipo) {
        this.tipoEquipo = tipoEquipo;
    }

    public final String ESTADO_BLOQUEADO = "B";
    public final String ESTADO_AUTORIZADO = "A";
    public final String ESTADO_SINAUTORIZAR = "S";
    public final String ESTADO_DESTRUIDO = "D";

    public String getSerialEquipo() {
        return serialEquipo;
    }

    public void setSerialEquipo(String serialEquipo) {
        this.serialEquipo = serialEquipo;
    }

    public String getIpEquipo() {
        return ipEquipo;
    }

    public void setIpEquipo(String ipEquipo) {
        this.ipEquipo = ipEquipo;
    }

    @Override
    public String toString() {
        return "EquiposAutorizacionBean{" + "idEquipo=" + idEquipo + ", macEquipo=" + macEquipo + ", tokenEquipo=" + tokenEquipo + ", ultimaSincronizacion=" + ultimaSincronizacion + ", primeraSincronizacion=" + primeraAutorizacion + ", estado=" + estado + ", pinSincronizacion=" + pinSincronizacion + '}';
    }

    public long getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(long idEquipo) {
        this.idEquipo = idEquipo;
    }

    public String getMacEquipo() {
        return macEquipo;
    }

    public void setMacEquipo(String macEquipo) {
        this.macEquipo = macEquipo;
    }

    public String getTokenEquipo() {
        return tokenEquipo;
    }

    public void setTokenEquipo(String tokenEquipo) {
        this.tokenEquipo = tokenEquipo;
    }

    public Date getUltimaSincronizacion() {
        return ultimaSincronizacion;
    }

    public void setUltimaSincronizacion(Date ultimaSincronizacion) {
        this.ultimaSincronizacion = ultimaSincronizacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPinSincronizacion() {
        return pinSincronizacion;
    }

    public void setPinSincronizacion(String pinSincronizacion) {
        this.pinSincronizacion = pinSincronizacion;
    }

   
}
