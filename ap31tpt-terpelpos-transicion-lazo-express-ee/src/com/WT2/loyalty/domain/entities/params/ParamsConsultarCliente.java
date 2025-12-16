/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.domain.entities.params;

import java.util.Date;

/**
 *
 * @author USUARIO
 */
public class ParamsConsultarCliente {
    
  private  String codigoTipoIdentificacion;
  private  String identifier;
  private  String fechaTransaccion; 
  private String tipoSitioVenta;

    public String getTipoSitioVenta() {
        return tipoSitioVenta;
    }

    public void setTipoSitioVenta(String tipoSitioVenta) {
        this.tipoSitioVenta = tipoSitioVenta;
    }
  
    public String getCodigoTipoIdentificacion() {
        return codigoTipoIdentificacion;
    }

    public void setCodigoTipoIdentificacion(String codigoTipoIdentificacion) {
        this.codigoTipoIdentificacion = codigoTipoIdentificacion;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getFechaTransaccion() {
        return fechaTransaccion;
    }

    public void setFechaTransaccion(String fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
    }
  
  
    
}
