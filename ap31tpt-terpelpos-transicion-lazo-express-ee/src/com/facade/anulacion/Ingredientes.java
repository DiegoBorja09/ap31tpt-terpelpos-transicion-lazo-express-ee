/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.facade.anulacion;

import java.util.List;

/**
 *
 * @author Usuario
 */
public class Ingredientes {

    private long id;
    private long productos_id;
    private String productos_plu;
    private String productos_descripcion;
    private double productos_precio;
    private long identificadorProducto;
    private double productos_precio_especial;
    private double productos_descuento_porcentaje;
    private double descuento_base;
    private double productos_descuento_valor;
    private double cantidad;
    private int compuesto_cantidad;
    private List<Impuestos> impuestos;
    private double costo;

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

    public double getProductos_descuento_porcentaje() {
        return productos_descuento_porcentaje;
    }

    public void setProductos_descuento_porcentaje(double productos_descuento_porcentaje) {
        this.productos_descuento_porcentaje = productos_descuento_porcentaje;
    }

    public double getDescuento_base() {
        return descuento_base;
    }

    public void setDescuento_base(double descuento_base) {
        this.descuento_base = descuento_base;
    }

    public double getProductos_descuento_valor() {
        return productos_descuento_valor;
    }

    public void setProductos_descuento_valor(double productos_descuento_valor) {
        this.productos_descuento_valor = productos_descuento_valor;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public int getCompuesto_cantidad() {
        return compuesto_cantidad;
    }

    public void setCompuesto_cantidad(int compuesto_cantidad) {
        this.compuesto_cantidad = compuesto_cantidad;
    }

    public List<Impuestos> getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(List<Impuestos> impuestos) {
        this.impuestos = impuestos;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }
    
    

    @Override
    public String toString() {
        return "Impuestos{" + "id=" + id
                + ", productos_id=" + productos_id
                + ", productos_plu=" + productos_plu
                + ", productos_descripcion=" + productos_descripcion
                + ", productos_precio=" + productos_precio
                + ", identificadorProducto=" + identificadorProducto
                + ", productos_precio_especial=" + productos_precio_especial
                + ", productos_descuento_porcentaje=" + productos_descuento_porcentaje
                + ", descuento_base=" + descuento_base
                + ", productos_descuento_valor=" + productos_descuento_valor
                + ", cantidad=" + cantidad
                + ", compuesto_cantidad=" + compuesto_cantidad
                + ", impuestos=" + impuestos
                + '}';
    }
}
