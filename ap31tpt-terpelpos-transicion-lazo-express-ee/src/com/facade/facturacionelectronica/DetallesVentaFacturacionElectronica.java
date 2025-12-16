/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.facade.facturacionelectronica;

import java.util.ArrayList;

/**
 *
 * @author Devitech
 */
public class DetallesVentaFacturacionElectronica {
    private long productos_id;
    private String productos_plu;
    private String producto_descripcion;
    private float cantidad;
    private double costo_unidad;
    private double costo_producto;
    private double precio;
    private String unidad;
    private String producto_tipo;
    private long descuento_id;
    private double descuento_producto;
    private ArrayList descuentos;
    private long remoto_id;
    private String sincronizado;
    private double subtotal;
    private boolean cortesia;
    private String compuesto;
    private AtributosDetalleVentaFacturacionELectronica atributos;
    private ArrayList<IngredientesDetallesVentaFacturaElectronica> ingredientes;
    private double base;
    private ArrayList<ImpuestosFacturacionElectronica> impuestos;

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

    public String getProducto_descripcion() {
        return producto_descripcion;
    }

    public void setProducto_descripcion(String producto_descripcion) {
        this.producto_descripcion = producto_descripcion;
    }

    public float getCantidad() {
        return cantidad;
    }

    public void setCantidad(float cantidad) {
        this.cantidad = cantidad;
    }

    public double getCosto_unidad() {
        return costo_unidad;
    }

    public void setCosto_unidad(double costo_unidad) {
        this.costo_unidad = costo_unidad;
    }

    public double getCosto_producto() {
        return costo_producto;
    }

    public void setCosto_producto(double costo_producto) {
        this.costo_producto = costo_producto;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getProducto_tipo() {
        return producto_tipo;
    }

    public void setProducto_tipo(String producto_tipo) {
        this.producto_tipo = producto_tipo;
    }

    public long getDescuento_id() {
        return descuento_id;
    }

    public void setDescuento_id(long descuento_id) {
        this.descuento_id = descuento_id;
    }

    public double getDescuento_producto() {
        return descuento_producto;
    }

    public void setDescuento_producto(double descuento_producto) {
        this.descuento_producto = descuento_producto;
    }

    public ArrayList getDescuentos() {
        return descuentos;
    }

    public void setDescuentos(ArrayList descuentos) {
        this.descuentos = descuentos;
    }

    public long getRemoto_id() {
        return remoto_id;
    }

    public void setRemoto_id(long remoto_id) {
        this.remoto_id = remoto_id;
    }

    public String getSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(String sincronizado) {
        this.sincronizado = sincronizado;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public boolean isCortesia() {
        return cortesia;
    }

    public void setCortesia(boolean cortesia) {
        this.cortesia = cortesia;
    }

    public String getCompuesto() {
        return compuesto;
    }

    public void setCompuesto(String compuesto) {
        this.compuesto = compuesto;
    }

    public AtributosDetalleVentaFacturacionELectronica getAtributos() {
        return atributos;
    }

    public void setAtributos(AtributosDetalleVentaFacturacionELectronica atributos) {
        this.atributos = atributos;
    }

    public ArrayList<IngredientesDetallesVentaFacturaElectronica> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(ArrayList<IngredientesDetallesVentaFacturaElectronica> ingredientes) {
        this.ingredientes = ingredientes;
    }

    public double getBase() {
        return base;
    }

    public void setBase(double base) {
        this.base = base;
    }

    public ArrayList<ImpuestosFacturacionElectronica> getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(ArrayList<ImpuestosFacturacionElectronica> impuestos) {
        this.impuestos = impuestos;
    }
    
}
