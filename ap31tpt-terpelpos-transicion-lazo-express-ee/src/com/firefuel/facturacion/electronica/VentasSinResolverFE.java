/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.facturacion.electronica;

/**
 *
 * @author Devitech
 */
public class VentasSinResolverFE {
    private long identificador;
    private boolean asignardatos;

    public VentasSinResolverFE(long identificador, boolean asignardatos) {
        this.identificador = identificador;
        this.asignardatos = asignardatos;
    }

    public long getIdentificador() {
        return identificador;
    }

    public void setIdentificador(long identificador) {
        this.identificador = identificador;
    }

    public boolean isAsignardatos() {
        return asignardatos;
    }

    public void setAsignardatos(boolean asignardatos) {
        this.asignardatos = asignardatos;
    }
    
    
}
