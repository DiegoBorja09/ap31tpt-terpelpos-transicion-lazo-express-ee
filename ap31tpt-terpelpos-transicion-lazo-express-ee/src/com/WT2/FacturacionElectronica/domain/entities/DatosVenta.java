package com.WT2.FacturacionElectronica.domain.entities;

import com.bean.AtributosBono;
import java.util.List;

public class DatosVenta {

    private String identificacionPromotor;
    private long ventaTotal;
    private long identificadorTicketVenta;
    private int operacion;
    private String fechaTransaccion;
    private List<AtributosBono> detallesBono;

    public String getIdentificacionPromotor() {
        return identificacionPromotor;
    }

    public void setIdentificacionPromotor(String identificacionPromotor) {
        this.identificacionPromotor = identificacionPromotor;
    }

    public long getVentaTotal() {
        return ventaTotal;
    }

    public void setVentaTotal(long ventaTotal) {
        this.ventaTotal = ventaTotal;
    }

    public long getIdentificadorTicketVenta() {
        return identificadorTicketVenta;
    }

    public void setIdentificadorTicketVenta(long identificadorTicketVenta) {
        this.identificadorTicketVenta = identificadorTicketVenta;
    }

    public int getOperacion() {
        return operacion;
    }

    public void setOperacion(int operacion) {
        this.operacion = operacion;
    }

    public String getFechaTransaccion() {
        return fechaTransaccion;
    }

    public void setFechaTransaccion(String fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
    }

    public List<AtributosBono> getDetallesBono() {
        return detallesBono;
    }

    public void setDetallesBono(List<AtributosBono> detallesBono) {
        this.detallesBono = detallesBono;
    }

}
