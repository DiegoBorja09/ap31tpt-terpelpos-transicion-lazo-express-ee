/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.facade.facturacionelectronica;

/**
 *
 * @author Devitech
 */
public class IngredientesDetallesVentaFacturaElectronica {
    private long id;
    private long productos_id;
    private String productos_plu;
    private String productos_descripcion;
    private double productos_precio;
    private long identificadorProducto;
    private double productos_precio_especial;
    private float productos_descuento_porcentaje;
    private float descuento_base;
    private float productos_descuento_valor;
    private float cantidad;
    private float compuesto_cantidad;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProductos_id() {
        return productos_id;
    }

    public void setProductos_id(long productos_id) {
        this.productos_id = productos_id;
    }

    public String getProductos_plu() {
        return productos_plu;
    }

    public void setProductos_plu(String productos_plu) {
        this.productos_plu = productos_plu;
    }

    public String getProductos_descripcion() {
        return productos_descripcion;
    }

    public void setProductos_descripcion(String productos_descripcion) {
        this.productos_descripcion = productos_descripcion;
    }

    public double getProductos_precio() {
        return productos_precio;
    }

    public void setProductos_precio(double productos_precio) {
        this.productos_precio = productos_precio;
    }

    public long getIdentificadorProducto() {
        return identificadorProducto;
    }

    public void setIdentificadorProducto(long identificadorProducto) {
        this.identificadorProducto = identificadorProducto;
    }

    public double getProductos_precio_especial() {
        return productos_precio_especial;
    }

    public void setProductos_precio_especial(double productos_precio_especial) {
        this.productos_precio_especial = productos_precio_especial;
    }

    public float getProductos_descuento_porcentaje() {
        return productos_descuento_porcentaje;
    }

    public void setProductos_descuento_porcentaje(float productos_descuento_porcentaje) {
        this.productos_descuento_porcentaje = productos_descuento_porcentaje;
    }

    public float getDescuento_base() {
        return descuento_base;
    }

    public void setDescuento_base(float descuento_base) {
        this.descuento_base = descuento_base;
    }

    public float getProductos_descuento_valor() {
        return productos_descuento_valor;
    }

    public void setProductos_descuento_valor(float productos_descuento_valor) {
        this.productos_descuento_valor = productos_descuento_valor;
    }

    public float getCantidad() {
        return cantidad;
    }

    public void setCantidad(float cantidad) {
        this.cantidad = cantidad;
    }

    public float getCompuesto_cantidad() {
        return compuesto_cantidad;
    }

    public void setCompuesto_cantidad(float compuesto_cantidad) {
        this.compuesto_cantidad = compuesto_cantidad;
    }
    
}
