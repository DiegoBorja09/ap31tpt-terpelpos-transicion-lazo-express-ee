/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.facade.facturacionelectronica;

import com.google.gson.JsonObject;

/**
 *
 * @author Devitech
 */
public class VentaFacturacionElectronica {
    private String fecha;
    private String fechaISO;
    private String prefijo;
    private int consecutivo;
    private long id_venta;
    private int consecutivoInicial;
    private int consecutivoActual;
    private int consecutivoFinal;
    private int bodegas_id;
    private long empresas_id;
    private int operacion;
    private String movimiento_estado;
    private int consecutivo_id;
    private long persona_id;
    private String persona_nit;
    private String persona_nombre;
    private String tercero_id;
    private boolean consumo_propio;
    private long tercero_nit;
    private String tercero_nombre;
    private String tercero_correo;
    private int tercero_tipo_persona;
    private int tercero_tipo_documento;
    private String tercero_responsabilidad_fiscal;
    private String tercero_codigo_sap;
    private String nombresPersona;
    private String identificacionPersona;
    private double costo_total;
    private double venta_total;
    private double descuento_total;
    private String origen_id;
    private String impreso;
    private String create_date;
    private double impuesto_total;
    private double total_base_imponible;
    private double total_bruto;
    private boolean contingencia;
    private JsonObject cliente;
    private String cajero;
    private int tipo_negocio;
    private String placa;

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFechaISO() {
        return fechaISO;
    }

    public void setFechaISO(String fechaISO) {
        this.fechaISO = fechaISO;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public int getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(int consecutivo) {
        this.consecutivo = consecutivo;
    }

    public long getId_venta() {
        return id_venta;
    }

    public void setId_venta(long id_venta) {
        this.id_venta = id_venta;
    }

    public int getConsecutivoInicial() {
        return consecutivoInicial;
    }

    public void setConsecutivoInicial(int consecutivoInicial) {
        this.consecutivoInicial = consecutivoInicial;
    }

    public int getConsecutivoActual() {
        return consecutivoActual;
    }

    public void setConsecutivoActual(int consecutivoActual) {
        this.consecutivoActual = consecutivoActual;
    }

    public int getConsecutivoFinal() {
        return consecutivoFinal;
    }

    public void setConsecutivoFinal(int consecutivoFinal) {
        this.consecutivoFinal = consecutivoFinal;
    }

    public int getBodegas_id() {
        return bodegas_id;
    }

    public void setBodegas_id(int bodegas_id) {
        this.bodegas_id = bodegas_id;
    }

    public long getEmpresas_id() {
        return empresas_id;
    }

    public void setEmpresas_id(long empresas_id) {
        this.empresas_id = empresas_id;
    }

    public int getOperacion() {
        return operacion;
    }

    public void setOperacion(int operacion) {
        this.operacion = operacion;
    }

    public String getMovimiento_estado() {
        return movimiento_estado;
    }

    public void setMovimiento_estado(String movimiento_estado) {
        this.movimiento_estado = movimiento_estado;
    }

    public int getConsecutivo_id() {
        return consecutivo_id;
    }

    public void setConsecutivo_id(int consecutivo_id) {
        this.consecutivo_id = consecutivo_id;
    }

    public long getPersona_id() {
        return persona_id;
    }

    public void setPersona_id(long persona_id) {
        this.persona_id = persona_id;
    }

    public String getPersona_nit() {
        return persona_nit;
    }

    public void setPersona_nit(String persona_nit) {
        this.persona_nit = persona_nit;
    }

    public String getPersona_nombre() {
        return persona_nombre;
    }

    public void setPersona_nombre(String persona_nombre) {
        this.persona_nombre = persona_nombre;
    }

    public String getTercero_id() {
        return tercero_id;
    }

    public void setTercero_id(String tercero_id) {
        this.tercero_id = tercero_id;
    }

    public boolean getConsumo_propio() {
        return consumo_propio;
    }

    public void setConsumo_propio(boolean consumo_propio) {
        this.consumo_propio = consumo_propio;
    }

    public long getTercero_nit() {
        return tercero_nit;
    }

    public void setTercero_nit(long tercero_nit) {
        this.tercero_nit = tercero_nit;
    }

    public String getTercero_nombre() {
        return tercero_nombre;
    }

    public void setTercero_nombre(String tercero_nombre) {
        this.tercero_nombre = tercero_nombre;
    }

    public String getTercero_correo() {
        return tercero_correo;
    }

    public void setTercero_correo(String tercero_correo) {
        this.tercero_correo = tercero_correo;
    }

    public int getTercero_tipo_persona() {
        return tercero_tipo_persona;
    }

    public void setTercero_tipo_persona(int tercero_tipo_persona) {
        this.tercero_tipo_persona = tercero_tipo_persona;
    }

    public int getTercero_tipo_documento() {
        return tercero_tipo_documento;
    }

    public void setTercero_tipo_documento(int tercero_tipo_documento) {
        this.tercero_tipo_documento = tercero_tipo_documento;
    }

    public String getTercero_responsabilidad_fiscal() {
        return tercero_responsabilidad_fiscal;
    }

    public void setTercero_responsabilidad_fiscal(String tercero_responsabilidad_fiscal) {
        this.tercero_responsabilidad_fiscal = tercero_responsabilidad_fiscal;
    }

    public String getTercero_codigo_sap() {
        return tercero_codigo_sap;
    }

    public void setTercero_codigo_sap(String tercero_codigo_sap) {
        this.tercero_codigo_sap = tercero_codigo_sap;
    }

    public String getNombresPersona() {
        return nombresPersona;
    }

    public void setNombresPersona(String nombresPersona) {
        this.nombresPersona = nombresPersona;
    }

    public String getIdentificacionPersona() {
        return identificacionPersona;
    }

    public void setIdentificacionPersona(String identificacionPersona) {
        this.identificacionPersona = identificacionPersona;
    }

    public double getCosto_total() {
        return costo_total;
    }

    public void setCosto_total(double costo_total) {
        this.costo_total = costo_total;
    }

    public double getVenta_total() {
        return venta_total;
    }

    public void setVenta_total(double venta_total) {
        this.venta_total = venta_total;
    }

    public double getDescuento_total() {
        return descuento_total;
    }

    public void setDescuento_total(double descuento_total) {
        this.descuento_total = descuento_total;
    }

    public String getOrigen_id() {
        return origen_id;
    }

    public void setOrigen_id(String origen_id) {
        this.origen_id = origen_id;
    }

    public String getImpreso() {
        return impreso;
    }

    public void setImpreso(String impreso) {
        this.impreso = impreso;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public double getImpuesto_total() {
        return impuesto_total;
    }

    public void setImpuesto_total(double impuesto_total) {
        this.impuesto_total = impuesto_total;
    }

    public double getTotal_base_imponible() {
        return total_base_imponible;
    }

    public void setTotal_base_imponible(double total_base_imponible) {
        this.total_base_imponible = total_base_imponible;
    }

    public double getTotal_bruto() {
        return total_bruto;
    }

    public void setTotal_bruto(double total_bruto) {
        this.total_bruto = total_bruto;
    }

    public boolean isContingencia() {
        return contingencia;
    }

    public void setContingencia(boolean contingencia) {
        this.contingencia = contingencia;
    }

    public JsonObject getCliente() {
        return cliente;
    }

    public void setCliente(JsonObject cliente) {
        this.cliente = cliente;
    }

    public String getCajero() {
        return cajero;
    }

    public void setCajero(String cajero) {
        this.cajero = cajero;
    }

    public int getTipoNegocio() {
        return tipo_negocio;
    }

    public void setTipoNegocio(int tipoNegocio) {
        this.tipo_negocio = tipoNegocio;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }
 
}
