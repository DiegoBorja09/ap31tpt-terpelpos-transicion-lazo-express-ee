/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bean.entradaCombustible;

import java.time.LocalDateTime;

/**
 *
 * @author Devitech
 */
public class ProductoDividido {
    private int id;
    private long idProducto;
    private long idTanque;
    private long idRemisionProducto;
    private long idRemisionSap;
    private float cantidad;
    private long idPromotor;
    private LocalDateTime fechaDeCreacion;
    private int year;
    private int mes;
    private int dia;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(long idProducto) {
        this.idProducto = idProducto;
    }

    public long getIdTanque() {
        return idTanque;
    }

    public void setIdTanque(long idTanque) {
        this.idTanque = idTanque;
    }

    public long getIdRemisionProducto() {
        return idRemisionProducto;
    }

    public void setIdRemisionProducto(long idRemision) {
        this.idRemisionProducto = idRemision;
    }

    public long getIdRemisionSap() {
        return idRemisionSap;
    }

    public void setIdRemisionSap(long idRemisionSap) {
        this.idRemisionSap = idRemisionSap;
    }

    public float getCantidad() {
        return cantidad;
    }

    public void setCantidad(float cantidad) {
        this.cantidad = cantidad;
    }

    public long getIdPromotor() {
        return idPromotor;
    }

    public void setIdPromotor(long idPromotor) {
        this.idPromotor = idPromotor;
    }

    public LocalDateTime getFechaDeCreacion() {
        return fechaDeCreacion;
    }

    public void setFechaDeCreacion(LocalDateTime fechaDeCreacion) {
        this.fechaDeCreacion = fechaDeCreacion;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    @Override
    public String toString() {
        return "ProductoDividido{" + "id=" + id + ", idProducto=" + idProducto + ", idTanque=" + idTanque + ", idRemision=" + idRemisionProducto + ", idRemisionSap=" + idRemisionSap + ", cantidad=" + cantidad + ", idPromotor=" + idPromotor + ", fechaDeCreacion=" + fechaDeCreacion + ", year=" + year + ", mes=" + mes + ", dia=" + dia + '}';
    }  
    
}
