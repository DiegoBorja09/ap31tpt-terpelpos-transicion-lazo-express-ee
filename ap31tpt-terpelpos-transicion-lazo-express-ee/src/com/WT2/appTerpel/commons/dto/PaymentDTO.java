/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.commons.dto;

import java.time.LocalDateTime;
import java.util.Date;

/**
 *
 * @author USUARIO
 */
public class PaymentDTO {
    private Integer id_pago;
   private Integer id_estatus;
   private Integer id_estado; 
   private Integer id_tipo_integracion;
   private Integer id_movimiento;
   private Date fecha_creacion;
   private Date fecha_actualizacion; 
   private String estado_proceso_descripcion;
   private String estado_venta_descripcion;

    public Integer getId_pago() {
        return id_pago;
    }

    public void setId_pago(Integer id_pago) {
        this.id_pago = id_pago;
    }

    public Integer getId_estatus() {
        return id_estatus;
    }

    public void setId_estatus(Integer id_estatus) {
        this.id_estatus = id_estatus;
    }

    public Integer getId_estado() {
        return id_estado;
    }

    public void setId_estado(Integer id_estado) {
        this.id_estado = id_estado;
    }

    public Integer getId_tipo_integracion() {
        return id_tipo_integracion;
    }

    public void setId_tipo_integracion(Integer id_tipo_integracion) {
        this.id_tipo_integracion = id_tipo_integracion;
    }

    public Integer getId_movimiento() {
        return id_movimiento;
    }

    public void setId_movimiento(Integer id_movimiento) {
        this.id_movimiento = id_movimiento;
    }

    public Date getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(Date fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    public Date getFecha_actualizacion() {
        return fecha_actualizacion;
    }

    public void setFecha_actualizacion(Date fecha_actualizacion) {
        this.fecha_actualizacion = fecha_actualizacion;
    }

    public String getEstado_proceso_descripcion() {
        return estado_proceso_descripcion;
    }

    public void setEstado_proceso_descripcion(String estado_proceso_descripcion) {
        this.estado_proceso_descripcion = estado_proceso_descripcion;
    }

    public String getEstado_venta_descripcion() {
        return estado_venta_descripcion;
    }

    public void setEstado_venta_descripcion(String estado_venta_descripcion) {
        this.estado_venta_descripcion = estado_venta_descripcion;
    }

 
   
}
