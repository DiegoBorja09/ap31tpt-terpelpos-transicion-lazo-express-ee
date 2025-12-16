/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.firefuel.datafonos;

/**
 *
 * @author Devitech
 */
public enum TransaccionOperacion {
    PAGO(1),
    ANULACION(2),
    BORRAR(3);
    
    private int estados;
    private TransaccionOperacion (int estados){
        this.estados = estados;
    }
    
    public int getEstadoTransaccionOperacion(){
        return this.estados;
    }
}
