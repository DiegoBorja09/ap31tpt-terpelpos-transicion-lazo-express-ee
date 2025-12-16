/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.facade.anulacion;

/**
 *
 * @author Usuario
 */
public class Impuestos {

    private long impuestos_id;
    private String tipo;
    private int valor;
    private double valor_imp;

    public long getImpuestos_id() {
        return impuestos_id;
    }

    public void setImpuestos_id(long impuestos_id) {
        this.impuestos_id = impuestos_id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public double getValor_imp() {
        return valor_imp;
    }

    public void setValor_imp(double valor_imp) {
        this.valor_imp = valor_imp;
    }

}
