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
public class AnulacionProductos {

    private long consecutivo;
    private String prefijo;
    private double total;
    private float cantidad;
    private float cantidadProducto;
    private String producto;
    private double precio;
    private double base;
    private Long id;
    private double subtotal;
    private long productos_id;
    private String productos_plu;
    private String producto_descripcion;
    private int costo_unidad;
    private double costo_producto;
    private String unidad;
    private String unidad_descripcion;
    private String producto_tipo;
    private int descuento_id;
    private double descuento_producto;
    private List<Impuestos> descuentos;
    private int remoto_id;
    private String sincronizado;
    private boolean cortesia;
    private String compuesto;
    private Atributos atributos;
    private List<Ingredientes> ingredientes;
    private List<Impuestos> impuestos;
    private List<Impuestos> impoconsumo;
    private long identificadorBodega;
    private double totalImpuesto;
    private double costo;
    private long unidadId;

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public long getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(long consecutivo) {
        this.consecutivo = consecutivo;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double precio) {
        this.total = precio;
    }

    public float getCantidad() {
        return cantidad;
    }

    public void setCantidad(float cantidad) {
        this.cantidad = cantidad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Ingredientes> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<Ingredientes> ingredientes) {
        this.ingredientes = ingredientes;
    }

    public List<Impuestos> getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(List<Impuestos> impuestos) {
        this.impuestos = impuestos;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getBase() {
        return base;
    }

    public void setBase(double base) {
        this.base = base;
    }

    public double getSubTotal() {
        return subtotal;
    }

    public void setSubTotal(double subTotal) {
        this.subtotal = subTotal;
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

    public String getProducto_descripcion() {
        return producto_descripcion;
    }

    public void setProducto_descripcion(String producto_descripcion) {
        this.producto_descripcion = producto_descripcion;
    }

    public int getCosto_unidad() {
        return costo_unidad;
    }

    public void setCosto_unidad(int costo_unidad) {
        this.costo_unidad = costo_unidad;
    }

    public double getCosto_producto() {
        return costo_producto;
    }

    public void setCosto_producto(double costo_producto) {
        this.costo_producto = costo_producto;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getUnidad_descripcion() {
        return unidad_descripcion;
    }

    public void setUnidad_descripcion(String unidad_descripcion) {
        this.unidad_descripcion = unidad_descripcion;
    }

    public String getProducto_tipo() {
        return producto_tipo;
    }

    public void setProducto_tipo(String producto_tipo) {
        this.producto_tipo = producto_tipo;
    }

    public int getDescuento_id() {
        return descuento_id;
    }

    public void setDescuento_id(int descuento_id) {
        this.descuento_id = descuento_id;
    }

    public double getDescuento_producto() {
        return descuento_producto;
    }

    public void setDescuento_producto(double descuento_producto) {
        this.descuento_producto = descuento_producto;
    }

    public List<Impuestos> getDescuentos() {
        return descuentos;
    }

    public void setDescuentos(List<Impuestos> descuentos) {
        this.descuentos = descuentos;
    }

    public int getRemoto_id() {
        return remoto_id;
    }

    public void setRemoto_id(int remoto_id) {
        this.remoto_id = remoto_id;
    }

    public String getSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(String sincronizado) {
        this.sincronizado = sincronizado;
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

    public Atributos getAtributos() {
        return atributos;
    }

    public void setAtributos(Atributos atributos) {
        this.atributos = atributos;
    }

    public long getIdentificadorBodega() {
        return identificadorBodega;
    }

    public void setIdentificadorBodega(long identificadorBodega) {
        this.identificadorBodega = identificadorBodega;
    }

    public float getCantidadProducto() {
        return cantidadProducto;
    }

    public void setCantidadProducto(float cantidadProducto) {
        this.cantidadProducto = cantidadProducto;
    }

    public double getTotalImpuesto() {
        return totalImpuesto;
    }

    public void setTotalImpuesto(double totalImpuesto) {
        this.totalImpuesto = totalImpuesto;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public List<Impuestos> getImpoconsumo() {
        return impoconsumo;
    }

    public void setImpoconsumo(List<Impuestos> impoconsumo) {
        this.impoconsumo = impoconsumo;
    }

    public long getUnidadId() {
        return unidadId;
    }

    public void setUnidadId(long unidadId) {
        this.unidadId = unidadId;
    }

}
