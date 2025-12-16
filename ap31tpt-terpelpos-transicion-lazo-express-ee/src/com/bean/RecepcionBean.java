/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bean;

import java.util.Date;

/**
 *
 * @author novusteam
 */
public class RecepcionBean {

    private long id;
    private String placa;
    private String documento;
    private long promotor;
    private long tanqueId;
    private long productoId;
    private String productoDescripcion;
    private String tanqueDescripcion;
    private int cantidad;

    private float alturaInicial;
    private double volumenInicial;
    private float aguaInicial;
    
    private Date fecha;
    

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public long getPromotor() {
        return promotor;
    }

    public void setPromotor(long promotor) {
        this.promotor = promotor;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTanqueId() {
        return tanqueId;
    }

    public void setTanqueId(long tanqueId) {
        this.tanqueId = tanqueId;
    }

    public String getTanqueDescripcion() {
        return tanqueDescripcion;
    }

    public void setTanqueDescripcion(String tanqueDescripcion) {
        this.tanqueDescripcion = tanqueDescripcion;
    }

    public long getProductoId() {
        return productoId;
    }

    public void setProductoId(long productoId) {
        this.productoId = productoId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public float getAlturaInicial() {
        return alturaInicial;
    }

    public void setAlturaInicial(float alturaInicial) {
        this.alturaInicial = alturaInicial;
    }

    public double getVolumenInicial() {
        return volumenInicial;
    }

    public void setVolumenInicial(double volumenInicial) {
        this.volumenInicial = volumenInicial;
    }

    public float getAguaInicial() {
        return aguaInicial;
    }

    public void setAguaInicial(float aguaInicial) {
        this.aguaInicial = aguaInicial;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getProductoDescripcion() {
        return productoDescripcion;
    }

    public void setProductoDescripcion(String productoDescripcion) {
        this.productoDescripcion = productoDescripcion;
    }

    @Override
    public String toString() {
        return "RecepcionBean{" + "id=" + id + ", placa=" + placa + ", documento=" + documento + ", promotor=" + promotor + ", tanqueId=" + tanqueId + ", productoId=" + productoId + ", productoDescripcion=" + productoDescripcion + ", tanqueDescripcion=" + tanqueDescripcion + ", cantidad=" + cantidad + ", alturaInicial=" + alturaInicial + ", volumenInicial=" + volumenInicial + ", aguaInicial=" + aguaInicial + ", fecha=" + fecha + '}';
    }
    
}
