/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bean;

/**
 *
 * @author Devitech
 */
public class MillasMiles {

    private String IdCliente;
    private String tipoId;
    private String pinCliente;
    private float Valor;
    private int valorMillas;

    public String getIdCliente() {
        return IdCliente;
    }

    public void setIdCliente(String IdCliente) {
        this.IdCliente = IdCliente;
    }

    public String getTipoId() {
        return tipoId;
    }

    public void setTipoId(String tipoId) {
        this.tipoId = tipoId;
    }

    public String getPinCliente() {
        return pinCliente;
    }

    public void setPinCliente(String pinCliente) {
        this.pinCliente = pinCliente;
    }

    public float getValor() {
        return Valor;
    }

    public void setValor(float Valor) {
        this.Valor = Valor;
    }

    public int getValorMillas() {
        return valorMillas;
    }

    public void setValorMillas(int valorMillas) {
        this.valorMillas = valorMillas;
    }
}
