/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bean;

import java.util.Date;

/**
 *
 * @author usuario
 */
public class CatalogoBean {

    long id;
    String descripcion;
    String valor;
    String tipo;
    String atributos;
    Date fecha;

    public CatalogoBean() {
    }

    public CatalogoBean(long id, String descripcion, String valor, String tipo, String atributos) {
        this.id = id;
        this.descripcion = descripcion;
        this.valor = valor;
        this.tipo = tipo;
        this.atributos = atributos;
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

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getAtributos() {
        return atributos;
    }

    public void setAtributos(String atributos) {
        this.atributos = atributos;
    }

}
