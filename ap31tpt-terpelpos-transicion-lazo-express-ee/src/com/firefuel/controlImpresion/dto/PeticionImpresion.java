
package com.firefuel.controlImpresion.dto;
  

public class PeticionImpresion {

    private long identificadorMovimiento;
    private long identificadorEquipo;
    private String placa;
    private String odometro;
    private String numero;
    private String orden;

    
    public long getIdentificadorMovimiento() {
        return identificadorMovimiento;
    }

    public void setIdentificadorMovimiento(long identificadorMovimiento) {
        this.identificadorMovimiento = identificadorMovimiento;
    }

    public long getIdentificadorEquipo() {
        return identificadorEquipo;
    }

    public void setIdentificadorEquipo(long identificadorEquipo) {
        this.identificadorEquipo = identificadorEquipo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getOdometro() {
        return odometro;
    }

    public void setOdometro(String odometro) {
        this.odometro = odometro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getOrden() {
        return orden;
    }

    public void setOrden(String orden) {
        this.orden = orden;
    }

    
}

    

