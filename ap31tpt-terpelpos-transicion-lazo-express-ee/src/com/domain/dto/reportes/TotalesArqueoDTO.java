package com.domain.dto.reportes;

import java.io.Serializable;

/**
 * DTO para representar los totales generales del arqueo de promotor.
 * 
 * @author Clean Architecture
 * @version 1.0
 */
public class TotalesArqueoDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private float totalCombustible;
    private float totalCanastilla;
    private float totalMarket;
    private float totalComplementarios;
    private float totalSobres;
    private float totalCalibraciones;
    private float totalGeneral;
    
    public TotalesArqueoDTO() {
    }
    
    public TotalesArqueoDTO(float totalCombustible, float totalCanastilla, float totalMarket,
                            float totalComplementarios, float totalSobres, float totalCalibraciones) {
        this.totalCombustible = totalCombustible;
        this.totalCanastilla = totalCanastilla;
        this.totalMarket = totalMarket;
        this.totalComplementarios = totalComplementarios;
        this.totalSobres = totalSobres;
        this.totalCalibraciones = totalCalibraciones;
        this.totalGeneral = calcularTotalGeneral();
    }
    
    // Getters y Setters
    public float getTotalCombustible() {
        return totalCombustible;
    }
    
    public void setTotalCombustible(float totalCombustible) {
        this.totalCombustible = totalCombustible;
        recalcularTotalGeneral();
    }
    
    public float getTotalCanastilla() {
        return totalCanastilla;
    }
    
    public void setTotalCanastilla(float totalCanastilla) {
        this.totalCanastilla = totalCanastilla;
        recalcularTotalGeneral();
    }
    
    public float getTotalMarket() {
        return totalMarket;
    }
    
    public void setTotalMarket(float totalMarket) {
        this.totalMarket = totalMarket;
        recalcularTotalGeneral();
    }
    
    public float getTotalComplementarios() {
        return totalComplementarios;
    }
    
    public void setTotalComplementarios(float totalComplementarios) {
        this.totalComplementarios = totalComplementarios;
        recalcularTotalGeneral();
    }
    
    public float getTotalSobres() {
        return totalSobres;
    }
    
    public void setTotalSobres(float totalSobres) {
        this.totalSobres = totalSobres;
        recalcularTotalGeneral();
    }
    
    public float getTotalCalibraciones() {
        return totalCalibraciones;
    }
    
    public void setTotalCalibraciones(float totalCalibraciones) {
        this.totalCalibraciones = totalCalibraciones;
        recalcularTotalGeneral();
    }
    
    public float getTotalGeneral() {
        return totalGeneral;
    }
    
    public void setTotalGeneral(float totalGeneral) {
        this.totalGeneral = totalGeneral;
    }
    
    private float calcularTotalGeneral() {
        return totalCombustible + totalCanastilla + totalMarket + 
               totalComplementarios + totalSobres + totalCalibraciones;
    }
    
    private void recalcularTotalGeneral() {
        this.totalGeneral = calcularTotalGeneral();
    }
    
    @Override
    public String toString() {
        return "TotalesArqueoDTO{" +
                "totalCombustible=" + totalCombustible +
                ", totalCanastilla=" + totalCanastilla +
                ", totalMarket=" + totalMarket +
                ", totalComplementarios=" + totalComplementarios +
                ", totalSobres=" + totalSobres +
                ", totalCalibraciones=" + totalCalibraciones +
                ", totalGeneral=" + totalGeneral +
                '}';
    }
}

