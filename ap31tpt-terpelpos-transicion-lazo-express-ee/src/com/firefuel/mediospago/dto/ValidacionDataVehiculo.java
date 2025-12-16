/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.mediospago.dto;

/**
 *
 * @author USUARIO
 */
public class ValidacionDataVehiculo {

    private String placa;
    private String nombreUsuario;

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        if (nombreUsuario == null) {
            this.nombreUsuario = "";
        }
        this.nombreUsuario = nombreUsuario;
    }

}
