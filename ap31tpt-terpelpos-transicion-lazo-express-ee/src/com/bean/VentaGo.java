/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bean;

import com.google.gson.JsonObject;

/**
 *
 * @author Devitech
 */
public class VentaGo {

    public int id;
    public String fecha;
    public String ventaTotal;
    public JsonObject atributos;
    public int consecutivo;
    public double cantidad;
    public String precio_producto;
    public String Description;
    public String prefijo;

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public int getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(int consecutivo) {
        this.consecutivo = consecutivo;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public String getPrecio_producto() {
        return precio_producto;
    }

    public void setPrecio_producto(String precio_producto) {
        this.precio_producto = precio_producto;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    } 

    public VentaGo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getVentaTotal() {
        return ventaTotal;
    }

    public void setVentaTotal(String ventaTotal) {
        this.ventaTotal = ventaTotal;
    }

    public JsonObject getAtributos() {
        return atributos;
    }

    public void setAtributos(JsonObject atributos) {
        this.atributos =  atributos;
    }

   
   
    
}
