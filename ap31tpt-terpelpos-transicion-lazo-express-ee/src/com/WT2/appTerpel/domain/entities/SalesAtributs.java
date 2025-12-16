/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.domain.entities;

/**
 *
 * @author USUARIO
 */
public class SalesAtributs {

    private String vehiculo_placa;
    private String voucher;
    private String orden;
    private String vehiculo_odometro;
    private String vehiculo_numero;
    private Boolean isElectronica;
    private Boolean recuperada;
    private Boolean isCredito;
    private float tipoVenta;
    private Consecutive consecutive;
    private SalesAtributeExtraData extraData;

    public Consecutive getConsecutive() {
        return consecutive;
    }

    public void setConsecutive(Consecutive consecutive) {
        this.consecutive = consecutive;
    }

    public String getVehiculo_placa() {
        return vehiculo_placa;
    }

    public void setVehiculo_placa(String vehiculo_placa) {
        this.vehiculo_placa = vehiculo_placa;
    }

    public String getVoucher() {
        return voucher;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }

    public String getOrden() {
        return orden;
    }

    public void setOrden(String orden) {
        this.orden = orden;
    }

    public String getVehiculo_odometro() {
        return vehiculo_odometro;
    }

    public void setVehiculo_odometro(String vehiculo_odometro) {
        this.vehiculo_odometro = vehiculo_odometro;
    }

    public String getVehiculo_numero() {
        return vehiculo_numero;
    }

    public void setVehiculo_numero(String vehiculo_numero) {
        this.vehiculo_numero = vehiculo_numero;
    }

    public Boolean getIsElectronica() {
        return isElectronica;
    }

    public void setIsElectronica(Boolean isElectronica) {
        this.isElectronica = isElectronica;
    }

    public Boolean getRecuperada() {
        return recuperada;
    }

    public void setRecuperada(Boolean recuperada) {
        this.recuperada = recuperada;
    }

    public Boolean getIsCredito() {
        return isCredito;
    }

    public void setIsCredito(Boolean isCredito) {
        this.isCredito = isCredito;
    }

    public float getTipoVenta() {
        return tipoVenta;
    }

    public void setTipoVenta(float tipoVenta) {
        this.tipoVenta = tipoVenta;
    }

    public SalesAtributeExtraData getExtraData() {
        return extraData;
    }

    public void setExtraData(SalesAtributeExtraData extraData) {
        this.extraData = extraData;
    }

    
}
