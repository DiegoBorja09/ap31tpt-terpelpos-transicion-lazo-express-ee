/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.ImpresonesFE.domain.entities;

/**
 *
 * @author USUARIO
 */
public class ParametrosPeticionFePrinter {
    
  private  PeticionFeImprimir peticionFeImprimir;
  private  String url;
  private long timeOut;
  private long initialTime;
  private boolean procesada;

    public PeticionFeImprimir getPeticionFeImprimir() {
        return peticionFeImprimir;
    }

    public void setPeticionFeImprimir(PeticionFeImprimir peticionFeImprimir) {
        this.peticionFeImprimir = peticionFeImprimir;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }

    public long getInitialTime() {
        return initialTime;
    }

    public void setInitialTime(long initialTime) {
        this.initialTime = initialTime;
    }

    public boolean isProcesada() {
        return procesada;
    }

    public void setProcesada(boolean procesada) {
        this.procesada = procesada;
        System.out.println("[DEBUG] setProcesada(" + procesada + ") ejecutado para movimiento: " + this.peticionFeImprimir.getIdentificadorMovimiento());
    }
    
    
    
  
}
