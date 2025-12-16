/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.domain.entities;

import java.time.LocalDateTime;
import java.util.Date;

/**
 *
 * @author USUARIO
 */
public class Payment {
   private Integer idPago;
   private Integer idEstadoPago;
   private Integer idEstadoProceso; 
   private Integer idTipoIntegracion;
   private Integer idMovimiento;
   private Date fechaCreacion;
   private Date fechaActualizacion; 
   private String estadoProcesoDescripcion;
   private String estadoPagoDescripcion;

    public Integer getIdPago() {
        return idPago;
    }

    public void setIdPago(Integer idPago) {
        this.idPago = idPago;
    }

    public Integer getIdEstadoPago() {
        return idEstadoPago;
    }

    public void setIdEstadoPago(Integer idEstadoPago) {
        this.idEstadoPago = idEstadoPago;
    }

    public Integer getIdEstadoProceso() {
        return idEstadoProceso;
    }

    public void setIdEstadoProceso(Integer idEstadoProceso) {
        this.idEstadoProceso = idEstadoProceso;
    }

    public Integer getIdTipoIntegracion() {
        return idTipoIntegracion;
    }

    public void setIdTipoIntegracion(Integer idTipoIntegracion) {
        this.idTipoIntegracion = idTipoIntegracion;
    }

    public Integer getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(Integer idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Date fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getEstadoProcesoDescripcion() {
        return estadoProcesoDescripcion;
    }

    public void setEstadoProcesoDescripcion(String estadoProcesoDescripcion) {
        this.estadoProcesoDescripcion = estadoProcesoDescripcion;
    }

    public String getEstadoPagoDescripcion() {
        return estadoPagoDescripcion;
    }

    public void setEstadoPagoDescripcion(String estadoPagoDescripcion) {
        this.estadoPagoDescripcion = estadoPagoDescripcion;
    }
   
   
    
}
