package com.WT2.FacturacionElectronica.domain.entities;

import java.util.List;

public class DatosVentaFE {

    private DatosVenta venta;
    private List<MediosPagoVenta> pagos;
    private List<DetallesVenta> detallesVenta;
    private String resoluciones;
    private String TipoEmpresa;

    public String getResoluciones() {
        return resoluciones;
    }

    public void setResoluciones(String resoluciones) {
        this.resoluciones = resoluciones;
    }

    public String getTipoEmpresa() {
        return TipoEmpresa;
    }

    public void setTipoEmpresa(String TipoEmpresa) {
        this.TipoEmpresa = TipoEmpresa;
    }

    public List<MediosPagoVenta> getPagos() {
        return pagos;
    }

    public void setPagos(List<MediosPagoVenta> pagos) {
        this.pagos = pagos;
    }

    public List<DetallesVenta> getDetallesVenta() {
        return detallesVenta;
    }

    public void setDetallesVenta(List<DetallesVenta> detallesVenta) {
        this.detallesVenta = detallesVenta;
    }

    public DatosVenta getVenta() {
        return venta;
    }

    public void setVenta(DatosVenta venta) {
        this.venta = venta;
    }

}
