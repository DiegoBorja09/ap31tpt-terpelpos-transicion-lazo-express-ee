/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.commons.domain.valueObject;

/**
 *
 * @author USUARIO
 */
public class ProcessStatus {

    public static String PENDIENTE = "PENDIENTE";
    public static String EN_ESPERA = "EN_ESPERA";
    public static String PROCESADO = "PROCESADO";
    public static String NO_ENCONTRADO = "NO_ENCONTRADO";
    public static String CONFIRMADA = "PAGO_CONFIRMADO";
    public static String PENDIENTE_CONFIRMACION = "PENDIENTE_CONFIRMACION";
    public static String PENDIENTE_REENVIO ="PEDIENTE_REENVIO";
    
    
    public static Integer ID_PENDIENTE = 2;
    public static Integer ID_ESPERA = 1;
    public static Integer ID_PROCESADO = 3;
    public static Integer ID_NO_ENCONTRADO = 0;
    public static Integer ID_CONFIRMADA = 5;
    public static Integer ID_PENDIENTE_CONFIRMACION = 6;
    public static Integer ID_PENDIENTE_REENVIO =7;
}
