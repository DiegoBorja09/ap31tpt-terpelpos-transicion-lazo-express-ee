/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bean;

import com.controllers.NovusUtils;
import com.firefuel.PedidoItemView;
import com.google.gson.JsonObject;
import java.util.Date;
import java.util.TreeMap;

/**
 *
 * @author novus
 */
public class MovimientosDetallesBean extends ProductoBean implements Comparable<MovimientosDetallesBean> {

    private long movimientoId;
    private long bodegasId;
    private long productoId;

    private int tipo_operacion;
    private Date fecha;
    private long descuentoId;
    private float descuentoProducto;
    private long remotoId;
    private int sincronizado;
    private long categoriaId;
    
    private int surtidor;
    private int cara;
    private int manguera;
    
    private float precioN;
    private Date aplicado;

    public Date getAplicado() {
        return aplicado;
    }

    public void setAplicado(Date aplicado) {
        this.aplicado = aplicado;
    }
    
    
    
    public float getPrecioN() {
        return precioN;
    }

    public void setPrecioN(float precioN) {
        this.precioN = precioN;
    }
    

    public int getSurtidor() {
        return surtidor;
    }

    public void setSurtidor(int surtidor) {
        this.surtidor = surtidor;
    }

    public int getCara() {
        return cara;
    }

    public void setCara(int cara) {
        this.cara = cara;
    }

    public int getManguera() {
        return manguera;
    }

    public void setManguera(int manguera) {
        this.manguera = manguera;
    }

    
    
    public long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(long categoriaId) {
        this.categoriaId = categoriaId;
    }

    private JsonObject atributos;
    private int item;
    private String codigoBarra;

    public JsonObject getAtributos() {
        return atributos;
    }

    public void setAtributos(JsonObject atributos) {
        this.atributos = atributos;
    }

    private float subtotal;
    private TreeMap<Long, Surtidor> surtidorProducto;

    public TreeMap<Long, Surtidor> getSurtidorProducto() {
        return surtidorProducto;
    }

    public void setSurtidorProducto(TreeMap<Long, Surtidor> surtidorProducto) {
        this.surtidorProducto = surtidorProducto;
    }

    boolean nuevo;
    PedidoItemView panelView;

    public PedidoItemView getPanelView() {
        return panelView;
    }

    public void setPanelView(PedidoItemView panelView) {
        this.panelView = panelView;
    }

    public long getMovimientoId() {
        return movimientoId;
    }

    public void setMovimientoId(long movimientoId) {
        this.movimientoId = movimientoId;
    }

    public long getBodegasId() {
        return bodegasId;
    }

    public void setBodegasId(long bodegasId) {
        this.bodegasId = bodegasId;
    }

    public long getProductoId() {
        return productoId;
    }

    public void setProductoId(long productoId) {
        this.productoId = productoId;
    }

    public int getTipo_operacion() {
        return tipo_operacion;
    }

    public void setTipo_operacion(int tipo_operacion) {
        this.tipo_operacion = tipo_operacion;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    @Override
    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public long getDescuentoId() {
        return descuentoId;
    }

    public void setDescuentoId(long descuentoId) {
        this.descuentoId = descuentoId;
    }

    public float getDescuentoProducto() {
        return descuentoProducto;
    }

    public void setDescuentoProducto(float descuentoProducto) {
        this.descuentoProducto = descuentoProducto;
    }

    public long getRemotoId() {
        return remotoId;
    }

    public void setRemotoId(long remotoId) {
        this.remotoId = remotoId;
    }

    public int getSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(int sincronizado) {
        this.sincronizado = sincronizado;
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public String getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(String codigoBarra) {
        this.codigoBarra = codigoBarra;
    }

    public float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(float subtotal) {
        this.subtotal = subtotal;
    }

    public boolean isNuevo() {
        return nuevo;
    }

    public void setNuevo(boolean nuevo) {
        this.nuevo = nuevo;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Object obj = null;
        try {
            obj = super.clone();
        } catch (CloneNotSupportedException ex) {
            NovusUtils.printLn("MovimientosDetallesBean > clone > CloneNotSupportedException");
        }
        return obj;
    }

    @Override
    public int compareTo(MovimientosDetallesBean o) {
        return descripcion.compareTo(o.descripcion);
    }

    @Override
    public String toString() {
        return "MovimientosDetallesBean{" + "movimientoId=" + movimientoId + ", bodegasId=" + bodegasId + ", productoId=" + productoId + ", tipo_operacion=" + tipo_operacion + ", fecha=" + fecha + ", descuentoId=" + descuentoId + ", descuentoProducto=" + descuentoProducto + ", remotoId=" + remotoId + ", sincronizado=" + sincronizado + ", categoriaId=" + categoriaId + ", surtidor=" + surtidor + ", cara=" + cara + ", manguera=" + manguera + ", precioN=" + precioN + ", aplicado=" + aplicado + ", atributos=" + atributos + ", item=" + item + ", codigoBarra=" + codigoBarra + ", subtotal=" + subtotal + ", surtidorProducto=" + surtidorProducto + ", nuevo=" + nuevo + ", panelView=" + panelView + '}';
    }
    
    

}