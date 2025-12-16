/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.domain.entities.beans;

import com.WT2.loyalty.domain.entities.request.ConsultClientRequestBody;

/**
 *
 * @author USUARIO
 */
public class FoundClient {

    private String nombreCliente;
    private String adicional;
    private String mensaje;
    private int status;
    private boolean existeClient;
    private ConsultClientRequestBody datosCliente;
    private boolean fidelizarMarket;

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public boolean isExisteClient() {
        return existeClient;
    }

    public void setExisteClient(boolean existeClient) {
        this.existeClient = existeClient;
    }

    public String getAdicional() {
        return adicional;
    }

    public void setAdicional(String adicional) {
        this.adicional = adicional;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ConsultClientRequestBody getDatosCliente() {
        return datosCliente;
    }

    public void setDatosCliente(ConsultClientRequestBody datosCliente) {
        this.datosCliente = datosCliente;
    }

    public boolean isFidelizarMarket() {
        return fidelizarMarket;
    }

    public void setFidelizarMarket(boolean fidelizarMarket) {
        this.fidelizarMarket = fidelizarMarket;
    }
    
}
