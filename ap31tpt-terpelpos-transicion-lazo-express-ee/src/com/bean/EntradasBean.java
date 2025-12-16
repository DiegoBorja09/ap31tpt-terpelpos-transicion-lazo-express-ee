/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bean;

import com.google.gson.JsonObject;
import java.util.Date;

/**
 *
 * @author Admin
 */
public class EntradasBean {

    Date fechaInicio;
    Date fechaFin;
    BodegaBean tanque;
    PersonaBean persona;

    public PersonaBean getPersona() {
        return persona;
    }

    public void setPersona(PersonaBean persona) {
        this.persona = persona;
    }

    public BodegaBean getTanque() {
        return tanque;
    }

    public void setTanque(BodegaBean tanque) {
        this.tanque = tanque;
    }
    ProductoBean productoDetalle;
    JsonObject lecturasVeederIniciales;
    JsonObject lecturasVeederFinales;

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public ProductoBean getProductoDetalle() {
        return productoDetalle;
    }

    public void setProductoDetalle(ProductoBean productoDetalle) {
        this.productoDetalle = productoDetalle;
    }

    public JsonObject getLecturasVeederIniciales() {
        return lecturasVeederIniciales;
    }

    public void setLecturasVeederIniciales(JsonObject lecturasVeederIniciales) {
        this.lecturasVeederIniciales = lecturasVeederIniciales;
    }

    public JsonObject getLecturasVeederFinales() {
        return lecturasVeederFinales;
    }

    public void setLecturasVeederFinales(JsonObject lecturasVeederFinales) {
        this.lecturasVeederFinales = lecturasVeederFinales;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getNroOrden() {
        return nroOrden;
    }

    public void setNroOrden(String nroOrden) {
        this.nroOrden = nroOrden;
    }

    public BodegaBean getTanqueLecturaInicial() {
        return tanqueLecturaInicial;
    }

    public void setTanqueLecturaInicial(BodegaBean tanqueLecturaInicial) {
        this.tanqueLecturaInicial = tanqueLecturaInicial;
    }

    public BodegaBean getTanqueLecturaFinal() {
        return tanqueLecturaFinal;
    }

    public void setTanqueLecturaFinal(BodegaBean tanqueLecturaFinal) {
        this.tanqueLecturaFinal = tanqueLecturaFinal;
    }
    String placa;
    String nroOrden;
    BodegaBean tanqueLecturaInicial;
    BodegaBean tanqueLecturaFinal;
}
