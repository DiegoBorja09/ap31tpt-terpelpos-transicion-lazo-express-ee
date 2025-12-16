/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.domain.entities.beans;

import java.math.BigDecimal;

/**
 *
 * @author USUARIO
 */
public class ProductsLoyalty {

    private String identificacionProducto;
    private float cantidadProducto;
    private int valorUnitarioProducto;
    private String lineaNegocio;

    public String getIdentificacionProducto() {
        return identificacionProducto;
    }

    public void setIdentificacionProducto(String identificacionProducto) {
        this.identificacionProducto = identificacionProducto;
    }


    public float  getCantidadProducto() {
        return cantidadProducto;
    }

    public void setCantidadProducto(float  cantidadProducto) {
        this.cantidadProducto = cantidadProducto;
    }

    public int getValorUnitarioProducto() {
        return valorUnitarioProducto;
    }

    public void setValorUnitarioProducto(int valorUnitarioProducto) {
        this.valorUnitarioProducto = valorUnitarioProducto;
    }

    public String getLineaNegocio() {
        return lineaNegocio;
    }

    public void setLineaNegocio(String lineaNegocio) {
        this.lineaNegocio = lineaNegocio;
    }

}
