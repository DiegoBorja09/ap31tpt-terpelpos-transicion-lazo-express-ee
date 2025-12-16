/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bean;

/**
 *
 * @author Devitech
 */
public class BonoViveTerpel {

    private String voucher;
    private float valor;
    private int millas;

    public String getVoucher() {
        return voucher;
    }

    public int getMillas() {
        return millas;
    }

    public void setMillas(int millas) {
        this.millas = millas;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "BonoViveTerpel{" + "voucher=" + voucher + ", valor=" + valor + ", millas=" + millas + '}';
    }
    
}
