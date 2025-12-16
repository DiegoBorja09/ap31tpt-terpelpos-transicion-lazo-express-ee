package com.WT2.loyalty.domain.entities.beans;

import java.util.List;

public class DatosVenta {

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
    private List<Productos> productos;

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

    public List<Productos> getProductos() {
        return productos;
    }

    public void setProductos(List<Productos> productos) {
        this.productos = productos;
    }

    public String getIdentificacionVenta() {
        return identificacionVenta;
    }

    public void setIdentificacionVenta(String identificacionVenta) {
        this.identificacionVenta = identificacionVenta;
    }

}
