package com.WT2.FacturacionElectronica.domain.entities;

public class MediosPagoVenta {

    private String descripcionMedio;
    private int identificacionMediosPagos;
    private double recibidoMedioPago;
    private double totalMedioPago;
    private double vueltoMedioPago;
    private String formaDePago;
    private String identificacionComprobante;
    private String monedaLocal;
    private double trm;

    public String getDescripcionMedio() {
        return descripcionMedio;
    }

    public void setDescripcionMedio(String descripcionMedio) {
        this.descripcionMedio = descripcionMedio;
    }

    public int getIdentificacionMediosPagos() {
        return identificacionMediosPagos;
    }

    public void setIdentificacionMediosPagos(int identificacionMediosPagos) {
        this.identificacionMediosPagos = identificacionMediosPagos;
    }

    public double getRecibidoMedioPago() {
        return recibidoMedioPago;
    }

    public void setRecibidoMedioPago(double recibidoMedioPago) {
        this.recibidoMedioPago = recibidoMedioPago;
    }

    public double getTotalMedioPago() {
        return totalMedioPago;
    }

    public void setTotalMedioPago(double totalMedioPago) {
        this.totalMedioPago = totalMedioPago;
    }

    public double getVueltoMedioPago() {
        return vueltoMedioPago;
    }

    public void setVueltoMedioPago(double vueltoMedioPago) {
        this.vueltoMedioPago = vueltoMedioPago;
    }

    public String getFormaDePago() {
        return formaDePago;
    }

    public void setFormaDePago(String formaDePago) {
        this.formaDePago = formaDePago;
    }

    public String getIdentificacionComprobante() {
        return identificacionComprobante;
    }

    public void setIdentificacionComprobante(String identificacionComprobante) {
        this.identificacionComprobante = identificacionComprobante;
    }

    public String getMonedaLocal() {
        return monedaLocal;
    }

    public void setMonedaLocal(String monedaLocal) {
        this.monedaLocal = monedaLocal;
    }

    public double getTrm() {
        return trm;
    }

    public void setTrm(double trm) {
        this.trm = trm;
    }

}
