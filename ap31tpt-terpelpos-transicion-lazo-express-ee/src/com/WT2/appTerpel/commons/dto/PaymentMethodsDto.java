/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.commons.dto;

/**
 *
 * @author USUARIO
 */
public class PaymentMethodsDto {
    
     private long medio;
    private String medioDescripcion;
    private float valorPago;

    public long getMedio() {
        return medio;
    }

    public void setMedio(long medio) {
        this.medio = medio;
    }

    public String getMedioDescripcion() {
        return medioDescripcion;
    }

    public void setMedioDescripcion(String medioDescripcion) {
        this.medioDescripcion = medioDescripcion;
    }

    public float getValorPago() {
        return valorPago;
    }

    public void setValorPago(float valorPago) {
        this.valorPago = valorPago;
    }
}
