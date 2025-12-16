/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.domain.entities;

import java.time.LocalDateTime;

/**
 *
 * @author USUARIO
 */
public class Sales {

    private long consecutivo;
    private long numero;
    private String tipo;
    private double cantidad;
    private long precio;
    private long total;
    private long cara;
    private long manguera;
    private LocalDateTime fecha;
    private String producto;
    private String operador;
    private String identificacionPromotor;
    private long identificacionProducto;
    private String unidadMedida;
    private String proceso;
    private SalesAtributs atributos;
    private PaymentMethods mediosDePago;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    
    
    public long getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(long consecutivo) {
        this.consecutivo = consecutivo;
    }

    public long getNumero() {
        return numero;
    }

    public void setNumero(long numero) {
        this.numero = numero;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public long getPrecio() {
        return precio;
    }

    public void setPrecio(long precio) {
        this.precio = precio;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getCara() {
        return cara;
    }

    public void setCara(long cara) {
        this.cara = cara;
    }

    public long getManguera() {
        return manguera;
    }

    public void setManguera(long manguera) {
        this.manguera = manguera;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public String getIdentificacionPromotor() {
        return identificacionPromotor;
    }

    public void setIdentificacionPromotor(String identificacionPromotor) {
        this.identificacionPromotor = identificacionPromotor;
    }

    public long getIdentificacionProducto() {
        return identificacionProducto;
    }

    public void setIdentificacionProducto(long identificacionProducto) {
        this.identificacionProducto = identificacionProducto;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public SalesAtributs getAtributos() {
        return atributos;
    }

    public void setAtributos(SalesAtributs atributos) {
        this.atributos = atributos;
    }

    public PaymentMethods getMediosDePago() {
        return mediosDePago;
    }

    public void setMediosDePago(PaymentMethods mediosDePago) {
        this.mediosDePago = mediosDePago;
    }
   
 
}
