/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bean;

import com.google.gson.JsonObject;

/**
 *
 * @author ASUS-PC
 */
public class ImpuestosBean implements Comparable<ImpuestosBean> {

    long id;
    long impuestos_id;
    boolean iva_incluido;
    float valor;
    String descripcion;
    String porcentaje_valor;
    float calculado;
    float totales;
    float base;
    String codigo;
    String tipo;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public static ImpuestosBean fromJson(JsonObject json) {
        ImpuestosBean imp = new ImpuestosBean();
        imp.setDescripcion(json.get("descripcion").getAsString());
        imp.setId(json.get("id").getAsLong());
        imp.setImpuestos_id(json.get("impuestos_id").getAsLong());
        if (json.get("iva_incluido") != null && !json.get("iva_incluido").isJsonNull()) {
            imp.setIva_incluido(json.get("iva_incluido").getAsString().equals("S"));
        } else {
            imp.setIva_incluido(false);
        }
        imp.setTipo(json.get("tipo").getAsString());
        imp.setValor(json.get("valor").getAsFloat());
        imp.setPorcentaje_valor(json.get("porcentaje_valor").getAsString());
        return imp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getImpuestos_id() {
        return impuestos_id;
    }

    public void setImpuestos_id(long impuestos_id) {
        this.impuestos_id = impuestos_id;
    }

    public boolean isIva_incluido() {
        return iva_incluido;
    }

    public void setIva_incluido(boolean iva_incluido) {
        this.iva_incluido = iva_incluido;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPorcentaje_valor() {
        return porcentaje_valor;
    }

    public void setPorcentaje_valor(String porcentaje_valor) {
        this.porcentaje_valor = porcentaje_valor;
    }

    public float getCalculado() {
        return calculado;
    }

    public void setCalculado(float calculado) {
        this.calculado = calculado;
    }

    public float getTotales() {
        return totales;
    }

    public void setTotales(float totales) {
        this.totales = totales;
    }

    public String getCodigo() {
        if (valor == 19) {
            this.codigo = "A";
        } else if (valor == 5) {
            this.codigo = "B";
        } else {
            this.codigo = "E";
        }
        return this.codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public float getBase() {
        return base;
    }

    public void setBase(float base) {
        this.base = base;
    }

    @Override
    public int compareTo(ImpuestosBean candidate) {
        return (this.getValor() > candidate.getValor() ? 1
                : (this.getValor() == candidate.getValor() ? 0 : -1));
    }

    @Override
    public String toString() {
        return "ImpuestosBean{" + "id=" + id + ", impuestos_id=" + impuestos_id + ", iva_incluido=" + iva_incluido + ", valor=" + valor + ", descripcion=" + descripcion + ", porcentaje_valor=" + porcentaje_valor + ", calculado=" + calculado + ", totales=" + totales + ", base=" + base + ", codigo=" + codigo + ", tipo=" + tipo + '}';
    }
    

}
