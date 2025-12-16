package com.domain.dto.reportes;

import java.io.Serializable;

/**
 * DTO para representar una venta de combustible en el arqueo de promotor.
 * 
 * @author Clean Architecture
 * @version 1.0
 */
public class VentaCombustibleDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String producto;
    private float galones;
    private float precio;
    private float descuento;
    private float total;
    
    public VentaCombustibleDTO() {
    }
    
    public VentaCombustibleDTO(String producto, float galones, float precio, float descuento, float total) {
        this.producto = producto;
        this.galones = galones;
        this.precio = precio;
        this.descuento = descuento;
        this.total = total;
    }
    
    // Getters y Setters
    public String getProducto() {
        return producto;
    }
    
    public void setProducto(String producto) {
        this.producto = producto;
    }
    
    public float getGalones() {
        return galones;
    }
    
    public void setGalones(float galones) {
        this.galones = galones;
    }
    
    public float getPrecio() {
        return precio;
    }
    
    public void setPrecio(float precio) {
        this.precio = precio;
    }
    
    public float getDescuento() {
        return descuento;
    }
    
    public void setDescuento(float descuento) {
        this.descuento = descuento;
    }
    
    public float getTotal() {
        return total;
    }
    
    public void setTotal(float total) {
        this.total = total;
    }
    
    @Override
    public String toString() {
        return "VentaCombustibleDTO{" +
                "producto='" + producto + '\'' +
                ", galones=" + galones +
                ", precio=" + precio +
                ", descuento=" + descuento +
                ", total=" + total +
                '}';
    }
}

