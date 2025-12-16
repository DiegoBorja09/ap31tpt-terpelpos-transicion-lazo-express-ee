
package com.WT2.loyalty.domain.entities.beans;


public class MediosPagoRedencion {
    
    private String identificacionMedioPago;
    private double valorPago;
    private double millasRedimidas;
    private String codigoVoucher;

    public String getIdentificacionMedioPago() {
        return identificacionMedioPago;
    }

    public void setIdentificacionMedioPago(String identificacionMedioPago) {
        this.identificacionMedioPago = identificacionMedioPago;
    }

    public double getValorPago() {
        return valorPago;
    }

    public void setValorPago(double valorPago) {
        this.valorPago = valorPago;
    }

    public double getMillasRedimidas() {
        return millasRedimidas;
    }

    public void setMillasRedimidas(double millasRedimidas) {
        this.millasRedimidas = millasRedimidas;
    }

    public String getCodigoVoucher() {
        return codigoVoucher;
    }

    public void setCodigoVoucher(String codigoVoucher) {
        this.codigoVoucher = codigoVoucher;
    }
    
    
    
}
