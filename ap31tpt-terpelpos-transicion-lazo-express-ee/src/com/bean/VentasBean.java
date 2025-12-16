/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bean;

import java.util.Date;

/**
 *
 * @author Admin
 */
public class VentasBean {

    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    private Date fechaVenta;
    private long consecutivoVenta;
    private int cantidadVenta;

    public int getCantidadVenta() {
        return cantidadVenta;
    }

    public void setCantidadVenta(int cantidadVenta) {
        this.cantidadVenta = cantidadVenta;
    }
    private float cantidadCombustible;
    private Surtidor surtidor;
    private float ventaTotal;
    private String placa;
    private float recaudo;
    private long personas_id;
    private String personaNombre;
    private boolean anulada;
    private float impuestoTotal;

    public float getImpuestoTotal() {
        return impuestoTotal;
    }

    public void setImpuestoTotal(float impuestoTotal) {
        this.impuestoTotal = impuestoTotal;
    }

    public boolean isAnulada() {
        return anulada;
    }

    public void setAnulada(boolean anulada) {
        this.anulada = anulada;
    }

    public String getPersonaNombre() {
        return personaNombre;
    }

    public void setPersonaNombre(String personaNombre) {
        this.personaNombre = personaNombre;
    }

    public long getPersonas_id() {
        return personas_id;
    }

    public void setPersonas_id(long personas_id) {
        this.personas_id = personas_id;
    }

    public float getRecaudo() {
        return recaudo;
    }

    public void setRecaudo(float recaudo) {
        this.recaudo = recaudo;
    }

    public String getPlaca() {
        return placa;
    }

    public Float getCantidadCombustible() {
        return this.cantidadCombustible;
    }

    public void setCantidadCombustible(float cantidadCombustible) {
        this.cantidadCombustible = cantidadCombustible;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public float getVentaTotal() {
        return ventaTotal;
    }

    public void setVentaTotal(float ventaTotal) {
        this.ventaTotal = ventaTotal;
    }

    public Date getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(Date fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public long getConsecutivoVenta() {
        return consecutivoVenta;
    }

    public void setConsecutivoVenta(long consecutivoVenta) {
        this.consecutivoVenta = consecutivoVenta;
    }

    public Surtidor getSurtidor() {
        return surtidor;
    }
    public String unidadMedida;

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public void setSurtidor(Surtidor surtidor) {
        this.surtidor = surtidor;
    }
}
