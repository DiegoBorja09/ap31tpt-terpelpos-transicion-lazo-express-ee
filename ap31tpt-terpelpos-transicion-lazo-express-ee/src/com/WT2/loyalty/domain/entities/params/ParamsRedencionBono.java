package com.WT2.loyalty.domain.entities.params;

import com.WT2.loyalty.domain.entities.beans.DatosVenta;
import com.WT2.loyalty.domain.entities.beans.IdentificacionClienteRedencion;
import com.WT2.loyalty.domain.entities.beans.MediosPagoRedencion;
import java.util.List;

public class ParamsRedencionBono {

    private DatosVenta datosVenta;
    private IdentificacionClienteRedencion identificacionClienteRedencion;
    private List<MediosPagoRedencion> mediosPagoRedencion;

    public DatosVenta getDatosVenta() {
        return datosVenta;
    }

    public void setDatosVenta(DatosVenta datosVenta) {
        this.datosVenta = datosVenta;
    }

    public IdentificacionClienteRedencion getIdentificacionClienteRedencion() {
        return identificacionClienteRedencion;
    }

    public void setIdentificacionClienteRedencion(IdentificacionClienteRedencion identificacionClienteRedencion) {
        this.identificacionClienteRedencion = identificacionClienteRedencion;
    }

    public List<MediosPagoRedencion> getMediosPagoRedencion() {
        return mediosPagoRedencion;
    }

    public void setMediosPagoRedencion(List<MediosPagoRedencion> mediosPagoRedencion) {
        this.mediosPagoRedencion = mediosPagoRedencion;
    }

}
