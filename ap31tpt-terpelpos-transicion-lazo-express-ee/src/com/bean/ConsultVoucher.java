/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bean;

/**
 *
 * @author Devitech
 */
public class ConsultVoucher {
    public String origenVenta;
    public String identificacionPuntoVenta;
    public String codigoBono;
    public long valorBono;

    public ConsultVoucher() {
    }

    public ConsultVoucher(String origenVenta, String identificacionPuntoVenta, String codigoBono, long valorBono) {
        this.origenVenta = origenVenta;
        this.identificacionPuntoVenta = identificacionPuntoVenta;
        this.codigoBono = codigoBono;
        this.valorBono = valorBono;
    }

    public String getOrigenVenta() {
        return origenVenta;
    }

    public void setOrigenVenta(String origenVenta) {
        this.origenVenta = origenVenta;
    }

    public String getIdentificacionPuntoVenta() {
        return identificacionPuntoVenta;
    }

    public void setIdentificacionPuntoVenta(String identificacionPuntoVenta) {
        this.identificacionPuntoVenta = identificacionPuntoVenta;
    }

    public String getCodigoBono() {
        return codigoBono;
    }

    public void setCodigoBono(String codigoBono) {
        this.codigoBono = codigoBono;
    }

    public long getValorBono() {
        return valorBono;
    }

    public void setValorBono(long valorBono) {
        this.valorBono = valorBono;
    }

    
}
