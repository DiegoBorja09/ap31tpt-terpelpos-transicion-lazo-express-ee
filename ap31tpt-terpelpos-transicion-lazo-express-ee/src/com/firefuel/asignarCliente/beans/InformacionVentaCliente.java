package com.firefuel.asignarCliente.beans;

import com.bean.MovimientosBean;
import com.bean.ReciboExtended;
import com.google.gson.JsonObject;
import com.neo.app.bean.Recibo;
import java.util.concurrent.TimeUnit;

public class InformacionVentaCliente {

    private Recibo recibo;
    private ReciboExtended reciboRec;
    private JsonObject atributosVenta;
    private boolean fidelizar;
    private MovimientosBean infoMovimiento;

    public Recibo getRecibo() {
        return recibo;
    }

    public void setRecibo(Recibo recibo) {
        this.recibo = recibo;
    }

    public ReciboExtended getReciboRec() {
        return reciboRec;
    }

    public void setReciboRec(ReciboExtended reciboRec) {
        this.reciboRec = reciboRec;
    }

    public JsonObject getAtributosVenta() {
        return atributosVenta;
    }

    public void setAtributosVenta(JsonObject atributosVenta) {
        this.atributosVenta = atributosVenta;
    }

    public boolean haTranscurridoTiempo(long tiempoMinutos) {
        if (reciboRec == null || reciboRec.getFecha() == null) {
            return false;
        }
        long millisegundosTranscurridos = System.currentTimeMillis() - reciboRec.getFecha().getTime();
        long tiempoAsignacion = TimeUnit.MINUTES.toMillis(tiempoMinutos);
        return millisegundosTranscurridos > tiempoAsignacion;
    }

    public boolean isFidelizar() {
        return fidelizar;
    }

    public void setFidelizar(boolean fidelizar) {
        this.fidelizar = fidelizar;
    }

    public MovimientosBean getInfoMovimiento() {
        return infoMovimiento;
    }

    public void setInfoMovimiento(MovimientosBean infoMovimiento) {
        this.infoMovimiento = infoMovimiento;
    }
    
    
    
}
