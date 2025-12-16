/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.firefuel.datafonos;

/**
 *
 * @author Devitech
 */
public enum EstadosTransaccionVenta {
    
    PENDIENTE(1),
    EXPIRADO(2),
    COMPLETADO(3),
    RECHAZADO(4),
    POR_ENVIAR(5),
    PAGO_CANCELADO(6),
    REENVIAR(7),
    PENDIENTE_ANULACION(8),
    ANULACION(9);
    
    private int estados = 0;
    private EstadosTransaccionVenta (int estados){
        this.estados = estados;
    }
    
    public int getEstadoVentaTransaccion(){
        return this.estados;
    }
}
