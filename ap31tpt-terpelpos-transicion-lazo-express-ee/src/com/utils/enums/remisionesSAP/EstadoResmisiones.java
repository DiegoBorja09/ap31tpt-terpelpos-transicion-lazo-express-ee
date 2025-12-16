/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.utils.enums.remisionesSAP;

/**
 *
 * @author Devitech
 */
public enum EstadoResmisiones {
    PENDIENTE(1),
    FINALIZADA(2),
    EN_USO(3);
    
    int estado;
    private EstadoResmisiones(int estado){
        this.estado = estado;
    }
    
    public int getEstado (){
        return this.estado;
    }
}
