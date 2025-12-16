package com.firefuel.ventas.dto;

import com.google.gson.JsonObject;

public class VentaDTO {

    // -------- Para mostrar en tabla --------
    private String prefijo;                  // Ej: "SETT-"
    private long numero;                    // Ej: 3850433
    private String fecha;                   // Ej: "04-04 10:52 A. M."
    private String producto;
    private String promotor;
    private String cara;
    private String placa;
    private String cantidadConUnidad;       // Ej: "2,112 GALONES"
    private String total;
    private long idProdcuto;// Ej: "$ 21.117"
    // === Nuevo campo ===
    private long idMovimiento;

    public long getIdTransmision() {
        return IdTransmision;
    }

    public void setIdTransmision(long idTransmision) {
        IdTransmision = idTransmision;
    }

    private long IdTransmision;

    public long getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(long idMovimiento) {
        this.idMovimiento = idMovimiento;
    }


    public long getTotalRaw() {
        return totalRaw;
    }

    public long getIdProdcuto() {
        return idProdcuto;
    }

    public void setIdProdcuto(long idProdcuto) {
        this.idProdcuto = idProdcuto;
    }


    public void setTotalRaw(long totalRaw) {
        this.totalRaw = totalRaw;
    }

    private String operador;
    private long totalRaw;
    private double cantidad;         // Valor numérico real de la cantidad (ej: 2.112)

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public String getTotalFormateado() {
        return totalFormateado;
    }

    public void setTotalFormateado(String totalFormateado) {
        this.totalFormateado = totalFormateado;
    }

    private String totalFormateado;  // Texto con formato moneda (ej: "$ 21.117")

    // -------- Para clasificar como sin resolver --------
    private boolean clienteSinAsignar;
    private String estadoDatafono;          // si tiene algo → va a sin resolver
    private long idTransaccionDatafono;
    private String proceso;

    // -------- Extra para otros usos --------
    private JsonObject atributosJson;       // Opcional: útil si necesitas lógica extendida

    // =============== GETTERS Y SETTERS ===============

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public long getNumero() {
        return numero;
    }

    public void setNumero(long numero) {
        this.numero = numero;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getPromotor() {
        return promotor;
    }

    public void setPromotor(String promotor) {
        this.promotor = promotor;
    }

    public String getCara() {
        return cara;
    }

    public void setCara(String cara) {
        this.cara = cara;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getCantidadConUnidad() {
        return cantidadConUnidad;
    }

    public void setCantidadConUnidad(String cantidadConUnidad) {
        this.cantidadConUnidad = cantidadConUnidad;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public boolean isClienteSinAsignar() {
        return clienteSinAsignar;
    }

    public void setClienteSinAsignar(boolean clienteSinAsignar) {
        this.clienteSinAsignar = clienteSinAsignar;
    }

    public String getEstadoDatafono() {
        return estadoDatafono;
    }

    public void setEstadoDatafono(String estadoDatafono) {
        this.estadoDatafono = estadoDatafono;
    }

    public long getIdTransaccionDatafono() {
        return idTransaccionDatafono;
    }

    public void setIdTransaccionDatafono(long idTransaccionDatafono) {
        this.idTransaccionDatafono = idTransaccionDatafono;
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public JsonObject getAtributosJson() {
        return atributosJson;
    }

    public void setAtributosJson(JsonObject atributosJson) {
        this.atributosJson = atributosJson;
    }
    public void setCantidadYUnidad(double cantidad, String unidad) {
        this.cantidad = cantidad;
        this.cantidadConUnidad = String.format("%,.3f %s", cantidad, unidad.toUpperCase());
    }

}
