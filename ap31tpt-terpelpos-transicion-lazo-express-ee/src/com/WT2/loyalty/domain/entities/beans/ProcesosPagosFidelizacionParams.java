/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.WT2.loyalty.domain.entities.beans;

/**
 *
 * @author Devitech
 */
public class ProcesosPagosFidelizacionParams {
    
    private Long idMov;
    private int tipoIdentificador;
    private int idTipoTransaccionProceso;
    private int idEstadoProceso;
    private int idTipoNegocio;
    private String fideliza;
    private boolean editarFidelizacion;

    public Long getIdMov() {
        return idMov;
    }

    public void setIdMov(Long idMov) {
        this.idMov = idMov;
    }

    public int getTipoIdentificador() {
        return tipoIdentificador;
    }

    public void setTipoIdentificador(int tipoIdentificador) {
        this.tipoIdentificador = tipoIdentificador;
    }

    public int getIdTipoTransaccionProceso() {
        return idTipoTransaccionProceso;
    }

    public void setIdTipoTransaccionProceso(int idTipoTransaccionProceso) {
        this.idTipoTransaccionProceso = idTipoTransaccionProceso;
    }

    public int getIdEstadoProceso() {
        return idEstadoProceso;
    }

    public void setIdEstadoProceso(int idEstadoProceso) {
        this.idEstadoProceso = idEstadoProceso;
    }

    public int getIdTipoNegocio() {
        return idTipoNegocio;
    }

    public void setIdTipoNegocio(int idTipoNegocio) {
        this.idTipoNegocio = idTipoNegocio;
    }

    public String getFideliza() {
        return fideliza;
    }

    public void setFideliza(String fideliza) {
        this.fideliza = fideliza;
    }

    public boolean isEditarFidelizacion() {
        return editarFidelizacion;
    }

    public void setEditarFidelizacion(boolean editarFidelizacion) {
        this.editarFidelizacion = editarFidelizacion;
    }
    
    
    
}
