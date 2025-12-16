package com.domain.dto.reportes;

import java.io.Serializable;

/**
 * DTO para representar el resumen de un medio de pago en el arqueo de promotor.
 * 
 * @author Clean Architecture
 * @version 1.0
 */
public class MedioPagoResumenDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String medio;
    private float combustible;
    private float canastilla;
    private float market;
    private float complementarios;
    private float total;
    
    public MedioPagoResumenDTO() {
    }
    
    public MedioPagoResumenDTO(String medio, float combustible, float canastilla, 
                               float market, float complementarios) {
        this.medio = medio;
        this.combustible = combustible;
        this.canastilla = canastilla;
        this.market = market;
        this.complementarios = complementarios;
        this.total = combustible + canastilla + market + complementarios;
    }
    
    // Getters y Setters
    public String getMedio() {
        return medio;
    }
    
    public void setMedio(String medio) {
        this.medio = medio;
    }
    
    public float getCombustible() {
        return combustible;
    }
    
    public void setCombustible(float combustible) {
        this.combustible = combustible;
        recalcularTotal();
    }
    
    public float getCanastilla() {
        return canastilla;
    }
    
    public void setCanastilla(float canastilla) {
        this.canastilla = canastilla;
        recalcularTotal();
    }
    
    public float getMarket() {
        return market;
    }
    
    public void setMarket(float market) {
        this.market = market;
        recalcularTotal();
    }
    
    public float getComplementarios() {
        return complementarios;
    }
    
    public void setComplementarios(float complementarios) {
        this.complementarios = complementarios;
        recalcularTotal();
    }
    
    public float getTotal() {
        return total;
    }
    
    public void setTotal(float total) {
        this.total = total;
    }
    
    private void recalcularTotal() {
        this.total = this.combustible + this.canastilla + this.market + this.complementarios;
    }
    
    @Override
    public String toString() {
        return "MedioPagoResumenDTO{" +
                "medio='" + medio + '\'' +
                ", combustible=" + combustible +
                ", canastilla=" + canastilla +
                ", market=" + market +
                ", complementarios=" + complementarios +
                ", total=" + total +
                '}';
    }
}

