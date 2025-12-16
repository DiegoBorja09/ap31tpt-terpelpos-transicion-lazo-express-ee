package com.WT2.commons.domain.entity.beans;

import java.util.List;

public class InformacionFidelizacionRetenida {

    private long idMovimiento;
    private String fechaTransaccion;
    private String origenVenta;
    private long pagoTotal;
    private String descripcionNegocio;
    private List<InformacionVentaFidelizacionRetenida> productos;

    public String getOrigenVenta() {
        return origenVenta;
    }

    public void setOrigenVenta(String origenVenta) {
        this.origenVenta = origenVenta;
    }

    public long getPagoTotal() {
        return pagoTotal;
    }

    public void setPagoTotal(long pagoTotal) {
        this.pagoTotal = pagoTotal;
    }

    public String getDescripcionNegocio() {
        return descripcionNegocio;
    }

    public void setDescripcionNegocio(String descripcionNegocio) {
        this.descripcionNegocio = descripcionNegocio;
    }

    public long getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(long idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public String getFechaTransaccion() {
        return fechaTransaccion;
    }

    public void setFechaTransaccion(String fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
    }

    public List<InformacionVentaFidelizacionRetenida> getProductos() {
        return productos;
    }

    public void setProductos(List<InformacionVentaFidelizacionRetenida> productos) {
        this.productos = productos;
    }

}
