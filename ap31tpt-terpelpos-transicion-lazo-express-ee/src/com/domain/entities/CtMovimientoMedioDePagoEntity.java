package com.domain.entities;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ct_movimientos_medios_pagos")
public class CtMovimientoMedioDePagoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ct_medios_pagos_id")
    private Long ctMediosPagosId;

    @Column(name = "ct_movimientos_id") 
    private Long ctMovimientosId;

    @Column(name = "valor_recibido", precision = 19, scale = 2)
    private BigDecimal valorRecibido;

    @Column(name = "valor_cambio", precision = 19, scale = 2)
    private BigDecimal valorCambio;

    @Column(name = "valor_total", precision = 19, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "trm", precision = 19, scale = 2)
    private BigDecimal trm;

    @Column(name = "codigo_dian")
    private Integer codigoDian;

    @Column(name = "ing_pago_datafono")
    private Boolean ingPagoDatafono;

    @Column(name = "id_transaccion", precision = 19, scale = 2)
    private BigDecimal idTransaccion;

    @Column(name = "id_adquiriente", precision = 19, scale = 2)
    private BigDecimal idAdquiriente;

    @Column(name = "tipo_cuenta")
    private String tipoCuenta;

    @Column(name = "numero_comprobante")
    private String numeroComprobante;

    @Column(name = "moneda_local", length = 1)
    private String monedaLocal;

    @Column(name = "bin")
    private String bin;

    @Column(name = "numero_recibo")
    private String numeroRecibo;

    @Column(name = "franquicia")
    private String franquicia;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getCtMediosPagosId() {
        return ctMediosPagosId;
    }

    public void setCtMediosPagosId(Long ctMediosPagosId) {
        this.ctMediosPagosId = ctMediosPagosId;
    }

    public Long getCtMovimientosId() {
        return ctMovimientosId;
    }

    public void setCtMovimientosId(Long ctMovimientosId) {
        this.ctMovimientosId = ctMovimientosId;
    }

    public BigDecimal getValorRecibido() {
        return valorRecibido;
    }

    public void setValorRecibido(BigDecimal valorRecibido) {
        this.valorRecibido = valorRecibido;
    }

    public BigDecimal getValorCambio() {
        return valorCambio;
    }

    public void setValorCambio(BigDecimal valorCambio) {
        this.valorCambio = valorCambio;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getTrm() {
        return trm;
    }

    public void setTrm(BigDecimal trm) {
        this.trm = trm;
    }

    public Integer getCodigoDian() {
        return codigoDian;
    }

    public void setCodigoDian(Integer codigoDian) {
        this.codigoDian = codigoDian;
    }

    public Boolean getIngPagoDatafono() {
        return ingPagoDatafono;
    }

    public void setIngPagoDatafono(Boolean ingPagoDatafono) {
        this.ingPagoDatafono = ingPagoDatafono;
    }

    public BigDecimal getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(BigDecimal idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public BigDecimal getIdAdquiriente() {
        return idAdquiriente;
    }

    public void setIdAdquiriente(BigDecimal idAdquiriente) {
        this.idAdquiriente = idAdquiriente;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public String getNumeroComprobante() {
        return numeroComprobante;
    }

    public void setNumeroComprobante(String numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }

    public String getMonedaLocal() {
        return monedaLocal;
    }

    public void setMonedaLocal(String monedaLocal) {
        this.monedaLocal = monedaLocal;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getNumeroRecibo() {
        return numeroRecibo;
    }

    public void setNumeroRecibo(String numeroRecibo) {
        this.numeroRecibo = numeroRecibo;
    }

    public String getFranquicia() {
        return franquicia;
    }

    public void setFranquicia(String franquicia) {
        this.franquicia = franquicia;
    }
}
