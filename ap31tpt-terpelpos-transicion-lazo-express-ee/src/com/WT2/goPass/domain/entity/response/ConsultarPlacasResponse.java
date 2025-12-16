/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.goPass.domain.entity.response;

import com.WT2.goPass.domain.entity.beans.PlacaGopass;
import java.util.List;

/**
 *
 * @author USUARIO
 */
public class ConsultarPlacasResponse {
    
    
    private List<PlacaGopass> datos;
    private String mensaje ;
    private String fechaProceso;
    private String codigoEDS;

    public List<PlacaGopass> getDatos() {
        return datos;
    }

    public void setDatos(List<PlacaGopass> datos) {
        this.datos = datos;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFechaProceso() {
        return fechaProceso;
    }

    public void setFechaProceso(String fechaProceso) {
        this.fechaProceso = fechaProceso;
    }

    public String getCodigoEDS() {
        return codigoEDS;
    }

    public void setCodigoEDS(String codigoEDS) {
        this.codigoEDS = codigoEDS;
    }
    
    
    
    
}
