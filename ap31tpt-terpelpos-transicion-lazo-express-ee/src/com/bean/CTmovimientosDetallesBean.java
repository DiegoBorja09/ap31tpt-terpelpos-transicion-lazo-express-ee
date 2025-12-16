package com.bean;

public class CTmovimientosDetallesBean {

    private long movimientosId;
    private long bodegasId;
    private float cantidad;
    private float costoProducto;
    private float precio;
    private float descuentosId;
    private float descuentoCalculado;
    private String fecha;
    private long ano;
    private long mes;
    private long dia;
    private long remotoId;
    private String sincronizado;
    private float subTotal;
    private long subMovimientosDetallesId;
    private long unidadesId;
    private long productosId;
    private CTmovimientosDetallesAtributosBean atributos;

    public long getMovimientosId() {
        return movimientosId;
    }

    public void setMovimientosId(long movimientosId) {
        this.movimientosId = movimientosId;
    }

    public long getBodegasId() {
        return bodegasId;
    }

    public void setBodegasId(long bodegasId) {
        this.bodegasId = bodegasId;
    }

    public float getCantidad() {
        return cantidad;
    }

    public void setCantidad(float cantidad) {
        this.cantidad = cantidad;
    }

    public float getCostoProducto() {
        return costoProducto;
    }

    public void setCostoProducto(float costoProducto) {
        this.costoProducto = costoProducto;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public float getDescuentosId() {
        return descuentosId;
    }

    public void setDescuentosId(float descuentosId) {
        this.descuentosId = descuentosId;
    }

    public float getDescuentoCalculado() {
        return descuentoCalculado;
    }

    public void setDescuentoCalculado(float descuentoCalculado) {
        this.descuentoCalculado = descuentoCalculado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public long getAno() {
        return ano;
    }

    public void setAno(long ano) {
        this.ano = ano;
    }

    public long getMes() {
        return mes;
    }

    public void setMes(long mes) {
        this.mes = mes;
    }

    public long getDia() {
        return dia;
    }

    public void setDia(long dia) {
        this.dia = dia;
    }

    public long getRemotoId() {
        return remotoId;
    }

    public void setRemotoId(long remotoId) {
        this.remotoId = remotoId;
    }

    public String getSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(String sincronizado) {
        this.sincronizado = sincronizado;
    }

    public float getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(float subTotal) {
        this.subTotal = subTotal;
    }

    public long getSubMovimientosDetallesId() {
        return subMovimientosDetallesId;
    }

    public void setSubMovimientosDetallesId(long subMovimientosDetallesId) {
        this.subMovimientosDetallesId = subMovimientosDetallesId;
    }

    public long getUnidadesId() {
        return unidadesId;
    }

    public void setUnidadesId(long unidadesId) {
        this.unidadesId = unidadesId;
    }

    public long getProductosId() {
        return productosId;
    }

    public void setProductosId(long productosId) {
        this.productosId = productosId;
    }

    public CTmovimientosDetallesAtributosBean getAtributos() {
        return atributos;
    }

    public void setAtributos(CTmovimientosDetallesAtributosBean atributos) {
        this.atributos = atributos;
    }

}
