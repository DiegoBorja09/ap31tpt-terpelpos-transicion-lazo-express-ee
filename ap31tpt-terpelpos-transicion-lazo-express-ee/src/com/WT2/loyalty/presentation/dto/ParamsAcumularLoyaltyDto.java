/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.presentation.dto;

import com.WT2.loyalty.domain.entities.beans.IdentificationClient;
import com.WT2.loyalty.domain.entities.beans.MediosPagoLoyalty;
import com.bean.MovimientosBean;
import com.bean.MovimientosDetallesBean;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.bean.MediosPagosBean;
import java.util.List;

/**
 *
 * @author USUARIO
 */
public class ParamsAcumularLoyaltyDto {

    private String fechaTransaccion;
    private String identificacionPuntoVenta;
    private String origenVenta;
    private String tipoVenta;
    private String identificacionPromotor;
    private String identificacionVenta;
    private long valorTotalVenta;
    private long totalImpuesto;
    private long descuentoVenta;
    private long pagoTotal;
    private MovimientosBean movimiento;
    private long movimientoId;
    private IdentificationClient identificacionCliente;
    private List<MediosPagosBean> mediosPagos;
    private List<MovimientosDetallesBean> medMovimientosDetallesBeans;
    private boolean complementario;

    public List<MovimientosDetallesBean> getMedMovimientosDetallesBeans() {
        return medMovimientosDetallesBeans;
    }

    public void setMedMovimientosDetallesBeans(List<MovimientosDetallesBean> medMovimientosDetallesBeans) {
        this.medMovimientosDetallesBeans = medMovimientosDetallesBeans;
    }

    public long getMovimientoId() {
        return movimientoId;
    }

    public void setMovimientoId(long movimientoId) {
        this.movimientoId = movimientoId;
    }

    public String getFechaTransaccion() {
        return fechaTransaccion;
    }

    public void setFechaTransaccion(String fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
    }

    public String getIdentificacionPuntoVenta() {
        return identificacionPuntoVenta;
    }

    public void setIdentificacionPuntoVenta(String identificacionPuntoVenta) {
        this.identificacionPuntoVenta = identificacionPuntoVenta;
    }

    public String getOrigenVenta() {
        return origenVenta;
    }

    public void setOrigenVenta(String origenVenta) {
        this.origenVenta = origenVenta;
    }

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

    public void setValorTotalVenta(int valorTotalVenta) {
        this.valorTotalVenta = valorTotalVenta;
    }

    public long getTotalImpuesto() {
        return totalImpuesto;
    }

    public void setTotalImpuesto(int totalImpuesto) {
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

    public MovimientosBean getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientosBean movimiento) {
        this.movimiento = movimiento;
    }

    public IdentificationClient getIdentificacionCliente() {
        return identificacionCliente;
    }

    public void setIdentificacionCliente(IdentificationClient identificacionCliente) {
        this.identificacionCliente = identificacionCliente;
    }

    public List<MediosPagosBean> getMediosPagos() {
        return mediosPagos;
    }

    public void setMediosPagos(List<MediosPagosBean> mediosPagos) {
        this.mediosPagos = mediosPagos;
    }

    public boolean isComplementario() {
        return complementario;
    }

    public void setComplementario(boolean complementario) {
        this.complementario = complementario;
    }
    
}
