/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.goPass.domain.entity.beans;

import com.WT2.payment.domian.entities.PaymentRequest;

/**
 *
 * @author USUARIO
 */
public class PaymentGopassParams {
    
    private PaymentRequest identificadorVentaTerpel;
    private String placa;
    private Long timeOut;

    public Long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Long timeOut) {
        this.timeOut = timeOut;
    }

    
    
    public PaymentRequest getIdentificadorVentaTerpel() {
        return identificadorVentaTerpel;
    }

    public String getPlaca() {
        return placa;
    }

    public void setIdentificadorVentaTerpel(PaymentRequest identificadorVentaTerpel) {
        this.identificadorVentaTerpel = identificadorVentaTerpel;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }
    
    
    
}
