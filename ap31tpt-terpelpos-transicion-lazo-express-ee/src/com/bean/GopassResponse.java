/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bean;

/**
 *
 * @author Devitech
 */
public class GopassResponse {
    private String mensaje;
    private String fecha;
    private String estadoPago;
    private int IDgopass;
    private boolean Error;

    public boolean isError() {
        return Error;
    }

    public void setError(boolean Error) {
        this.Error = Error;
    }

    public GopassResponse(boolean Error) {
       this.Error = Error;
    }

    public GopassResponse(String mensaje, String fecha, String estadoPago, int IDgopass) {
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.estadoPago = estadoPago;
        this.IDgopass = IDgopass;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public int gtIDgopass() {
        return IDgopass;
    }

    public void setIDgopass(int IDgopass) {
        this.IDgopass = IDgopass;
    }

    
}
