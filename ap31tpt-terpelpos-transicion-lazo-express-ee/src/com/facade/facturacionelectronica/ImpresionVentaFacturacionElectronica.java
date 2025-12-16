/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.facade.facturacionelectronica;

import com.google.gson.JsonObject;
import java.util.List;

/**
 *
 * @author Devitech
 */
public class ImpresionVentaFacturacionElectronica {
    private VentaFacturacionElectronica venta;
    private List<DetallesVentaFacturacionElectronica> detalle;
    private List<MediosPagosFactruracionElectronica> pagos;
    private JsonObject cliente;
    private ObservacionesFacturacionElectronica observaciones;
    private String resoluciones;
    private String tipoEmpresa;
    
    public VentaFacturacionElectronica getVentaFacturacionElectronica() {
        return venta;
    }

    public void setVentaFacturacionElectronica(VentaFacturacionElectronica ventaFacturacionElectronica) {
        this.venta = ventaFacturacionElectronica;
    }

    public List<DetallesVentaFacturacionElectronica> getDetalles() {
        return detalle;
    }

    public void setDetalles(List<DetallesVentaFacturacionElectronica> detalles) {
        this.detalle = detalles;
    }

    public List<MediosPagosFactruracionElectronica> getMediosPagos() {
        return pagos;
    }

    public void setMediosPagos(List<MediosPagosFactruracionElectronica> mediosPagos) {
        this.pagos = mediosPagos;
    }

    public JsonObject getCliente() {
        return cliente;
    }

    public void setCliente(JsonObject cliente) {
        this.cliente = cliente;
    }

    public ObservacionesFacturacionElectronica getObservacionesFacturacionElectronica() {
        return observaciones;
    }

    public void setObservacionesFacturacionElectronica(ObservacionesFacturacionElectronica observacionesFacturacionElectronica) {
        this.observaciones = observacionesFacturacionElectronica;
    }

    public String getResoluciones() {
        return resoluciones;
    }

    public void setResoluciones(String resoluciones) {
        this.resoluciones = resoluciones;
    }

    public String getTipoEmpresa() {
        return tipoEmpresa;
    }

    public void setTipoEmpresa(String TipoEmpresa) {
        this.tipoEmpresa = TipoEmpresa;
    }    
    
}
