
package com.WT2.appTerpel.domain.entities;

public class MedioPagoAtributos {
    
    private String codigoSAP;
    private String codigoSalesfore;
    private String codigoDian; 
    private Boolean visible;
    private Boolean bonoTerpel;
    private String empresasFinales;

    public Boolean getBonoTerpel() {
        return bonoTerpel;
    }

    public void setBonoTerpel(Boolean bonoTerpel) {
        this.bonoTerpel = bonoTerpel;
    }
    
    public String getCodigoSAP() {
        return codigoSAP;
    }

    public void setCodigoSAP(String codigoSAP) {
        this.codigoSAP = codigoSAP;
    }

    public String getCodigoSalesfore() {
        return codigoSalesfore;
    }

    public void setCodigoSalesfore(String codigoSalesfore) {
        this.codigoSalesfore = codigoSalesfore;
    }

    public String getCodigoDian() {
        return codigoDian;
    }

    public void setCodigoDian(String codigoDian) {
        this.codigoDian = codigoDian;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public String getEmpresasFinales() {
        return empresasFinales;
    }

    public void setEmpresasFinales(String empresasFinales) {
        this.empresasFinales = empresasFinales;
    }
    
    
    
    
}
