/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.payment.domian.entities;

import com.WT2.commons.domain.entity.Movimiento;
import com.WT2.commons.domain.entity.Request;

/**
 *
 * @author USUARIO
 */
public class PaymentRequest {

    private long identificadorMovimiento;
    private String medioDescription;

    public String getMedioDescription() {
        return medioDescription;
    }

    public void setMedioDescription(String medioDescription) {
        this.medioDescription = medioDescription;
    }

    public long getIdentificadorMovimiento() {
        return identificadorMovimiento;
    }

    public void setIdentificadorMovimiento(long identificadorMovimiento) {
        this.identificadorMovimiento = identificadorMovimiento;
    }

    
}
