/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.goPass.domain.entity.beans;

/**
 *
 * @author Devitech
 */
public class PlacaGopass {

    private String placa;
    private String tagGopass;
    private String nombreUsuario;
    private String isla;
    private String fechahora;

    public void setTagGopass(String tagGopass) {
        this.tagGopass = tagGopass;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public void setFechahora(String fechahora) {
        this.fechahora = fechahora;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public void setIsla(String isla) {
        this.isla = isla;
    }

    public String getTagGopass() {
        return tagGopass;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getIsla() {
        return isla;
    }

    public String getFechahora() {
        return fechahora;
    }

}
