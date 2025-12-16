package com.WT2.FacturacionElectronica.domain.entities;

public class DetallesVenta {

    private String codigoBarra;
    private double cantidadVenta;
    private long precioVentaFeWeb;

    public String getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(String codigoBarra) {
        this.codigoBarra = codigoBarra;
    }

    public double getCantidadVenta() {
        return cantidadVenta;
    }

    public void setCantidadVenta(double cantidadVenta) {
        this.cantidadVenta = cantidadVenta;
    }

    public long getPrecioVentaFeWeb() {
        return precioVentaFeWeb;
    }

    public void setPrecioVentaFeWeb(long precioVentaFeWeb) {
        this.precioVentaFeWeb = precioVentaFeWeb;
    }
    
    

}
