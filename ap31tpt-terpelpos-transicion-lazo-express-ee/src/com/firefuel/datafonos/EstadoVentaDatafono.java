/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.firefuel.datafonos;

/**
 *
 * @author Devitech
 */
public enum EstadoVentaDatafono {
    PENDIENTE(1),
    EXPIRADO(2),
    COMPLETADO(3),
    RECHAZADO(4),
    POR_ENVIAR(5),
    PAGO_CANCELADO(6);
    
    int estado;
    
    private EstadoVentaDatafono(int estado){
        this.estado = estado;
    }
    
    public int getValor() {
        return estado;
    }
}
