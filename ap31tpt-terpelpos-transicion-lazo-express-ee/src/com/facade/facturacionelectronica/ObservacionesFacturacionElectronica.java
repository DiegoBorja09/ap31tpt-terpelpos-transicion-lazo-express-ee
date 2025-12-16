/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.facade.facturacionelectronica;

/**
 *
 * @author Devitech
 */
public class ObservacionesFacturacionElectronica {
    private long id;
    private long id_estacion;
    private boolean autorretenedor;
    private String autorretenedor_numero_autorizacion;
    private String autorretenedor_fecha_inicio;
    private boolean responsable_iva;
    private boolean gran_contribuyente;
    private String gran_contribuyente_numero_autorizacion;
    private String gran_contribuyente_fecha_inicio;
    private boolean retenedor_iva;
    private String notas_adicionales;
    private String pie_pagina_factura_pos;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId_estacion() {
        return id_estacion;
    }

    public void setId_estacion(long id_estacion) {
        this.id_estacion = id_estacion;
    }

    public boolean isAutorretenedor() {
        return autorretenedor;
    }

    public void setAutorretenedor(boolean autorretenedor) {
        this.autorretenedor = autorretenedor;
    }

    public String getAutorretenedor_numero_autorizacion() {
        return autorretenedor_numero_autorizacion;
    }

    public void setAutorretenedor_numero_autorizacion(String autorretenedor_numero_autorizacion) {
        this.autorretenedor_numero_autorizacion = autorretenedor_numero_autorizacion;
    }

    public String getAutorretenedor_fecha_inicio() {
        return autorretenedor_fecha_inicio;
    }

    public void setAutorretenedor_fecha_inicio(String autorretenedor_fecha_inicio) {
        this.autorretenedor_fecha_inicio = autorretenedor_fecha_inicio;
    }

    public boolean isResponsable_iva() {
        return responsable_iva;
    }

    public void setResponsable_iva(boolean responsable_iva) {
        this.responsable_iva = responsable_iva;
    }

    public boolean isGran_contribuyente() {
        return gran_contribuyente;
    }

    public void setGran_contribuyente(boolean gran_contribuyente) {
        this.gran_contribuyente = gran_contribuyente;
    }

    public String getGran_contribuyente_numero_autorizacion() {
        return gran_contribuyente_numero_autorizacion;
    }

    public void setGran_contribuyente_numero_autorizacion(String gran_contribuyente_numero_autorizacion) {
        this.gran_contribuyente_numero_autorizacion = gran_contribuyente_numero_autorizacion;
    }

    public String getGran_contribuyente_fecha_inicio() {
        return gran_contribuyente_fecha_inicio;
    }

    public void setGran_contribuyente_fecha_inicio(String gran_contribuyente_fecha_inicio) {
        this.gran_contribuyente_fecha_inicio = gran_contribuyente_fecha_inicio;
    }

    public boolean isRetenedor_iva() {
        return retenedor_iva;
    }

    public void setRetenedor_iva(boolean retenedor_iva) {
        this.retenedor_iva = retenedor_iva;
    }

    public String getNotas_adicionales() {
        return notas_adicionales;
    }

    public void setNotas_adicionales(String notas_adicionales) {
        this.notas_adicionales = notas_adicionales;
    }

    public String getPie_pagina_factura_pos() {
        return pie_pagina_factura_pos;
    }

    public void setPie_pagina_factura_pos(String pie_pagina_factura_pos) {
        this.pie_pagina_factura_pos = pie_pagina_factura_pos;
    }
    
}
