/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.domain.entities;

/**
 *
 * @author USUARIO
 */
public class MedioPagoInTableView {
    
    
    private String medio;
    private String voucher;
    private int valor;
    private long identificadorMovimeinto;

    public String getMedio() {
        return medio;
    }

    public void setMedio(String medio) {
        this.medio = medio;
    }

    public String getVoucher() {
        return voucher;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }
    
    
    public boolean validateIsPayment(String paymentType){
        return medio.equals(paymentType);
    }

    public long getIdentificadorMovimeinto() {
        return identificadorMovimeinto;
    }

    public void setIdentificadorMovimeinto(long identificadorMovimeinto) {
        this.identificadorMovimeinto = identificadorMovimeinto;
    }
    
    
}
