package com.application.useCases.movimientos.dto;

import com.google.gson.JsonObject;

public class CtMovimientoJsonObjDto {
    public final JsonObject jsonMovimientos;
    public final JsonObject jsonMovimientosCredito;
    public final JsonObject jsonMovimientoDetalles;
    public final JsonObject jsonMovimientoMediosPago;
    public final JsonObject request;
    public final JsonObject response;

    public CtMovimientoJsonObjDto(JsonObject jsonMovimientos,
                                  JsonObject jsonMovimientosCredito,
                                  JsonObject jsonMovimientoDetalles,
                                  JsonObject jsonMovimientoMediosPago,
                                  JsonObject request,
                                  JsonObject response) {
        this.jsonMovimientos = jsonMovimientos;
        this.jsonMovimientosCredito = jsonMovimientosCredito;
        this.jsonMovimientoDetalles = jsonMovimientoDetalles;
        this.jsonMovimientoMediosPago = jsonMovimientoMediosPago;
        this.request = request;
        this.response = response;
    }
}
