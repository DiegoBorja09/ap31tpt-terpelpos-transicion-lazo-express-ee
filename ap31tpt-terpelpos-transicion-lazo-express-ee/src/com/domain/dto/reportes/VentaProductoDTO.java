package com.domain.dto.reportes;

import java.io.Serializable;

/**
 * DTO para representar una venta de producto (canastilla/market) en el arqueo de promotor.
 * 
 * @author Clean Architecture
 * @version 1.0
 */
public class VentaProductoDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String codigo;
    private String producto;
    private float cantidad;
    private float precio;
    private float total;
    
    public VentaProductoDTO() {
    }
    
    public VentaProductoDTO(String codigo, String producto, float cantidad, float precio, float total) {
        this.codigo = codigo;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precio = precio;
        this.total = total;
    }
    
    // Getters y Setters
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public String getProducto() {
        return producto;
    }
    
    public void setProducto(String producto) {
        this.producto = producto;
    }
    
    public float getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(float cantidad) {
        this.cantidad = cantidad;
    }
    
    public float getPrecio() {
        return precio;
    }
    
    public void setPrecio(float precio) {
        this.precio = precio;
    }
    
    public float getTotal() {
        return total;
    }
    
    public void setTotal(float total) {
        this.total = total;
    }
    
    @Override
    public String toString() {
        return "VentaProductoDTO{" +
                "codigo='" + codigo + '\'' +
                ", producto='" + producto + '\'' +
                ", cantidad=" + cantidad +
                ", precio=" + precio +
                ", total=" + total +
                '}';
    }
}

