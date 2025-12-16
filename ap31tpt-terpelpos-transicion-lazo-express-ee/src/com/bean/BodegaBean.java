/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bean;

import java.util.ArrayList;

/**
 *
 * @author novus
 */
public class BodegaBean {

    long id;
    String descripcion;
    String estado;
    long empresaId;
    String codigo;
    String dimension;
    String ubicacion;
    private String finalidad;
    int numeroStand;
    long familiaId;
    float galonTanque = -1;
    float volumenMaximo;
    float temperaturaTanque = 0;
    double altura_total;
    double altura_agua;
    ArrayList<ProductoBean> productos;
    ArrayList<ConsecutivoBean> consecutivos;

    public float getTemperaturaTanque() {
        return temperaturaTanque;
    }

    public void setTemperaturaTanque(float temperaturaTanque) {
        this.temperaturaTanque = temperaturaTanque;
    }

    public float getVolumenMaximo() {
        return volumenMaximo;
    }

    public void setVolumenMaximo(float volumenMaximo) {
        this.volumenMaximo = volumenMaximo;
    }

    public float getGalonTanque() {
        return galonTanque;
    }

    public void setGalonTanque(float galonTanque) {
        this.galonTanque = galonTanque;
    }    

    public long getFamiliaId() {
        return familiaId;
    }

    public void setFamiliaId(long familiaId) {
        this.familiaId = familiaId;
    }

    public double getAltura_total() {
        return altura_total;
    }

    public void setAltura_total(double altura_total) {
        this.altura_total = altura_total;
    }

    public double getAltura_agua() {
        return altura_agua;
    }

    public void setAltura_agua(double altura_agua) {
        this.altura_agua = altura_agua;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(long empresaId) {
        this.empresaId = empresaId;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public int getNumeroStand() {
        return numeroStand;
    }

    public void setNumeroStand(int numeroStand) {
        this.numeroStand = numeroStand;
    }

    public ArrayList<ProductoBean> getProductos() {
        return productos;
    }

    public void setProductos(ArrayList<ProductoBean> productos) {
        this.productos = productos;
    }

    public ArrayList<ConsecutivoBean> getConsecutivos() {
        return consecutivos;
    }

    public void setConsecutivos(ArrayList<ConsecutivoBean> consecutivos) {
        this.consecutivos = consecutivos;
    }

    
    @Override
    public String toString() {
        return getNumeroStand() + "-" + getDescripcion().trim().toUpperCase();
    }

    public String getFinalidad() {
        return finalidad;
    }

    public void setFinalidad(String finalidad) {
        this.finalidad = finalidad;
    }

}
