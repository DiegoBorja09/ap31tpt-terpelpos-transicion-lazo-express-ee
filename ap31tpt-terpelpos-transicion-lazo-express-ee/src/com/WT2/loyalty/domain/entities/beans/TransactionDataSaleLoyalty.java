/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.domain.entities.beans;

import java.util.List;

/**
 *
 * @author USUARIO
 */
public class TransactionDataSaleLoyalty {

    private String fechaTransaccion;
    private String identificacionPuntoVenta;
    private String origenVenta;
    private String tipoVenta;
    private String identificacionPromotor;
    private String identificacionVenta;
    private long totalImpuesto;
    private long valorTotalVenta;
    private long descuentoVenta;
    private long pagoTotal;
    private long movimientoId;

    public String getTipoVenta() {
        return tipoVenta;
    }

    public void setTipoVenta(String tipoVenta) {
        this.tipoVenta = tipoVenta;
    }

    public String getIdentificacionPromotor() {
        return identificacionPromotor;
    }

    public void setIdentificacionPromotor(String identificacionPromotor) {
        this.identificacionPromotor = identificacionPromotor;
    }

    public String getIdentificacionVenta() {
        return identificacionVenta;
    }

    public void setIdentificacionVenta(String identificacionVenta) {
        this.identificacionVenta = identificacionVenta;
    }

    public long getValorTotalVenta() {
        return valorTotalVenta;
    }

    public void setValorTotalVenta(long valorTotalVenta) {
        this.valorTotalVenta = valorTotalVenta;
    }

    public long getTotalImpuesto() {
        return totalImpuesto;
    }

    public void setTotalImpuesto(long totalImpuesto) {
        this.totalImpuesto = totalImpuesto;
    }

    public long getDescuentoVenta() {
        return descuentoVenta;
    }

    public void setDescuentoVenta(long descuentoVenta) {
        this.descuentoVenta = descuentoVenta;
    }

    public long getPagoTotal() {
        return pagoTotal;
    }

    public void setPagoTotal(long pagoTotal) {
        this.pagoTotal = pagoTotal;
    }

    public String getFechaTransaccion() {
        return fechaTransaccion;
    }

    public String getIdentificacionPuntoVenta() {
        return identificacionPuntoVenta;
    }

    public String getOrigenVenta() {
        return origenVenta;
    }

    public long getMovimientoId() {
        return movimientoId;
    }

    public void setFechaTransaccion(String fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
    }

    public void setIdentificacionPuntoVenta(String identificacionPuntoVenta) {
        this.identificacionPuntoVenta = identificacionPuntoVenta;
    }

    public void setOrigenVenta(String origenVenta) {
        this.origenVenta = origenVenta;
    }

    public void setMovimientoId(long movimientoId) {
        this.movimientoId = movimientoId;
    }

    
}
