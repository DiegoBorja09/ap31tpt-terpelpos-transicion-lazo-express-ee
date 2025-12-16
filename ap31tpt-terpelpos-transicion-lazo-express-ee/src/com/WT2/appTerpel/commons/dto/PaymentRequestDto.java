/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.commons.dto;

import com.WT2.appTerpel.domain.entities.MedioPagoInTableView;

/**
 *
 * @author USUARIO
 */
public class PaymentRequestDto {

    private String medioDescription;
    private long identificadorMovimiento;

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
