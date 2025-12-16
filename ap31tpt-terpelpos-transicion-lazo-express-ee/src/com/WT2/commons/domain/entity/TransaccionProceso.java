/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.commons.domain.entity;

/**
 *
 * @author USUARIO
 */
public class TransaccionProceso {

    private long idTrasccion;
    private long idintegracion;
    private long idMov;
    private long idEstadoIntegracion;
    private long idEstadoProceso;

    public long getIdTrasccion() {
        return idTrasccion;
    }

    public void setIdTrasccion(long idTrasccion) {
        this.idTrasccion = idTrasccion;
    }

    public long getIdintegracion() {
        return idintegracion;
    }

    public void setIdintegracion(long idintegracion) {
        this.idintegracion = idintegracion;
    }

    public long getIdMov() {
        return idMov;
    }

    public void setIdMov(long idMov) {
        this.idMov = idMov;
    }

    public long getIdEstadoIntegracion() {
        return idEstadoIntegracion;
    }

    public void setIdEstadoIntegracion(long idEstadoIntegracion) {
        this.idEstadoIntegracion = idEstadoIntegracion;
    }

    public long getIdEstadoProceso() {
        return idEstadoProceso;
    }

    public void setIdEstadoProceso(long idEstadoProceso) {
        this.idEstadoProceso = idEstadoProceso;
    }

    public boolean esRechazadoOnoExiste() {
        if(idEstadoIntegracion == 0) return true;
        return idEstadoIntegracion >= 3 && idEstadoIntegracion <= 5;
    }
}
