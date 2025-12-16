/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.WT2.appTerpel.domain.entities;

/**
 *
 * @author Devitech
 */
public class VentasAppterpelBotonesValidador {
    
    private boolean pago;
    private boolean proceso;
    private boolean fidelizacion;

    public boolean isPago() {
        return pago;
    }

    public void setPago(boolean pago) {
        this.pago = pago;
    }

    public boolean isProceso() {
        return proceso;
    }

    public void setProceso(boolean proceso) {
        this.proceso = proceso;
    }

    public boolean isFidelizacion() {
        return fidelizacion;
    }

    public void setFidelizacion(boolean fidelizacion) {
        this.fidelizacion = fidelizacion;
    }
    
    
    
}
