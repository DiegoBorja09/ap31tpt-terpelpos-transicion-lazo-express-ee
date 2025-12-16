package com.bean;

import com.google.gson.JsonObject;

public class CrearPosVentaFEParams {
    private final int cara;
    private final int manguera;
    private final JsonObject clienteJson;

    public CrearPosVentaFEParams(int cara, int manguera, JsonObject clienteJson) {
        this.cara = cara;
        this.manguera = manguera;
        this.clienteJson = clienteJson;
    }

    public int getCara() {
        return cara;
    }

    public int getManguera() {
        return manguera;
    }

    public JsonObject getClienteJson() {
        return clienteJson;
    }
} 
