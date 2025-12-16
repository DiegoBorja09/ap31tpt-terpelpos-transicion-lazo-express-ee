/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.commons.domain.entity;

/**
 *
 * @author USUARIO
 */
public class ValoresParametros {
    
    
    private String descrtipcion;
    private long id_parametro_valor;
    private long id_empresa;
    private long valor_minimo;
    private long valor_maximo;
    private String estado;
    private String nombre;
    private String valor;

    public String getDescrtipcion() {
        return descrtipcion;
    }

    public void setDescrtipcion(String descrtipcion) {
        this.descrtipcion = descrtipcion;
    }

    public long getId_parametro_valor() {
        return id_parametro_valor;
    }

    public void setId_parametro_valor(long id_parametro_valor) {
        this.id_parametro_valor = id_parametro_valor;
    }

    public long getId_empresa() {
        return id_empresa;
    }

    public void setId_empresa(long id_empresa) {
        this.id_empresa = id_empresa;
    }

    public long getValor_minimo() {
        return valor_minimo;
    }

    public void setValor_minimo(long valor_minimo) {
        this.valor_minimo = valor_minimo;
    }

    public long getValor_maximo() {
        return valor_maximo;
    }

    public void setValor_maximo(long valor_maximo) {
        this.valor_maximo = valor_maximo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
    
    
            
    
}
