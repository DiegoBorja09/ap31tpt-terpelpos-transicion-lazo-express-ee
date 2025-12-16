/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bean;

/**
 *
 * @author devitech
 */
public class MedioIdentificacionBean {

    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public String getUrlImagen_2x() {
        return urlImagen_2x;
    }

    public void setUrlImagen_2x(String urlImagen_2x) {
        this.urlImagen_2x = urlImagen_2x;
    }

    public boolean isNecesarioLector() {
        return necesarioLector;
    }

    public void setNecesarioLector(boolean necesarioLector) {
        this.necesarioLector = necesarioLector;
    }
    String descripcion;
    String urlImagen;
    String urlImagen_2x;
    boolean necesarioLector;
}
