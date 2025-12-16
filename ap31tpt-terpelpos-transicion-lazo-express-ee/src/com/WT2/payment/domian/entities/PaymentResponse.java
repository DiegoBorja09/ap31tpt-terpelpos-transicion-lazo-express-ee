package com.WT2.payment.domian.entities;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author USUARIO
 */
public class PaymentResponse {
    private String IDSeguimiento ;
    private String idTransaccion;
    private String estadoPago;
    private int technicalCode ;
    private String mensaje;

    public String getIDSeguimiento() {
        return IDSeguimiento;
    }

    public void setIDSeguimiento(String IDSeguimiento) {
        this.IDSeguimiento = IDSeguimiento;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public int getTechnicalCode() {
        return technicalCode;
    }

    public void setTechnicalCode(int technicalCode) {
        this.technicalCode = technicalCode;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    
 
    
    
}
