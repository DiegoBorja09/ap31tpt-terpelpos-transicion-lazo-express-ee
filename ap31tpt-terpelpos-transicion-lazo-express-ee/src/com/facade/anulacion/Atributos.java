/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.facade.anulacion;

/**
 *
 * @author Usuario
 */
public class Atributos {
   private long categoriaId;
   private String categoriaDescripcion;
   private int tipo;
   private boolean isElectronica;

    public long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getCategoriaDescripcion() {
        return categoriaDescripcion;
    }

    public void setCategoriaDescripcion(String categoriaDescripcion) {
        this.categoriaDescripcion = categoriaDescripcion;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public boolean isIsElectronica() {
        return isElectronica;
    }

    public void setIsElectronica(boolean isElectronica) {
        this.isElectronica = isElectronica;
    }
   
   
}
