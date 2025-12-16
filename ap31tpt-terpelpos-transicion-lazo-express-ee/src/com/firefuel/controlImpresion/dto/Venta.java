package com.firefuel.controlImpresion.dto;

import java.sql.Timestamp;

public class Venta {

    private long id;
    private Timestamp fecha;
    private String placa;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        if (placa == null) {
            this.placa = "";
        }
        this.placa = placa;
    }

}
