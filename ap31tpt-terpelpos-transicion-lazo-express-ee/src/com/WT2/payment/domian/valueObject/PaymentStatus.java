/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.payment.domian.valueObject;

/**
 *
 * @author USUARIO
 */
public final class PaymentStatus {

    private PaymentStatus() {}
    
    public static final String  PENDIENTE = "PENDIENTE";
    public static final String RECHAZADO = "RECHAZADO";
    public static final String APROBADO = "APROBADO";
    public static final String EXPIRADO = "EXPIRADO";
    public static final String RECHAZADO_FAIL = "RECHAZADO_POR_FALLA";
    public static final String NO_ENCONTRADO = "NO_ENCONTRADO";
    
}
