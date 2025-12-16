/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.facade.facturacionelectronica;

/**
 *
 * @author Devitech
 */
public class MediosPagosFactruracionElectronica {
    private int medios_pagos_id;
    private double valor;
    private double recibido;
    private double cambio;
    private String medios_pagos_descripcion;
    private String formaDePago;

    public int getMedios_pagos_id() {
        return medios_pagos_id;
    }

    public void setMedios_pagos_id(int medios_pagos_id) {
        this.medios_pagos_id = medios_pagos_id;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public double getRecibido() {
        return recibido;
    }

    public void setRecibido(double recibido) {
        this.recibido = recibido;
    }

    public double getCambio() {
        return cambio;
    }

    public void setCambio(double cambio) {
        this.cambio = cambio;
    }

    public String getDescripcion() {
        return medios_pagos_descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.medios_pagos_descripcion = descripcion;
    }

    public String getMedios_pagos_descripcion() {
        return medios_pagos_descripcion;
    }

    public void setMedios_pagos_descripcion(String medios_pagos_descripcion) {
        this.medios_pagos_descripcion = medios_pagos_descripcion;
    }

    public String getFormaDePago() {
        return formaDePago;
    }

    public void setFormaDePago(String formaDePago) {
        this.formaDePago = formaDePago;
    }
    
}
