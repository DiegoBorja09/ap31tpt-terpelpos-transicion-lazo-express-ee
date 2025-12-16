/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bean;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author novus
 */
public class MediosPagosBean {

    private long idRegistro;
    private long id;
    private long idMedioPago = 0;
    private String franquicia = "";
    private long idAdquiriente = 0;
    private long idTransaccion = 0;
    private String numeroRecibo = "";
    private String bin = "";
    private String tipoDeCuenta = "";
    private String descripcion;
    private boolean credito;
    private boolean isCambio;
    private boolean comprobante;
    private float minimo_valor;
    private float maximo_valor;
    private String estado;
    private boolean seleccionado;
    private float trm = 0;
    private int codigoDian = 0;
    private float cambio;
    private float recibido;
    private String voucher;
    private float valor;
    private int cantidad;
    private JsonObject atributos;
    private boolean isPagoExterno;
    private boolean pagosExternoValidado;
    private boolean RedencionMillas;
    public String codDian;
    private String codigoAdquiriente;
    private boolean confirmacionBono;
    private boolean isBono;
    private List<AtributosBono> Bonos_Vive_Terpel;

    public String getCodDian() {
        return codDian;
    }

    public void setCodDian(String codDian) {
        this.codDian = codDian;
    }

    public boolean isRedencionMillas() {
        return RedencionMillas;
    }

    public void setRedencionMillas(boolean RedencionMillas) {
        this.RedencionMillas = RedencionMillas;
    }

    private ArrayList<BonoViveTerpel> BonosViveTerpel;

    public ArrayList<BonoViveTerpel> getBonosViveTerpel() {
        return BonosViveTerpel;
    }

    public void setBonosViveTerpel(ArrayList<BonoViveTerpel> BonosViveTerpel) {
        this.BonosViveTerpel = BonosViveTerpel;
    }

    public boolean isIsCambio() {
        return isCambio;
    }

    public void setIsCambio(boolean isCambio) {
        this.isCambio = isCambio;
    }

    public JsonObject getAtributos() {
        return atributos;
    }

    public void setAtributos(JsonObject atributos) {
        this.atributos = atributos;
    }

    public float getRecibido() {
        return recibido;
    }

    public float getCambio() {
        return cambio;
    }

    public void setCambio(float cambio) {
        this.cambio = cambio;
    }

    public void setRecibido(float recibido) {
        this.recibido = recibido;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public MediosPagosBean() {
    }

    public MediosPagosBean(long id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isCredito() {
        return credito;
    }

    public void setCredito(boolean credito) {
        this.credito = credito;
    }

    public boolean isCambio() {
        return isCambio;
    }

    public void setCambio(boolean isCambio) {
        this.isCambio = isCambio;
    }

    public boolean isComprobante() {
        return comprobante;
    }

    public void setComprobante(boolean comprobante) {
        this.comprobante = comprobante;
    }

    public float getMinimo_valor() {
        return minimo_valor;
    }

    public void setMinimo_valor(float minimo_valor) {
        this.minimo_valor = minimo_valor;
    }

    public float getMaximo_valor() {
        return maximo_valor;
    }

    public void setMaximo_valor(float maximo_valor) {
        this.maximo_valor = maximo_valor;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public String getVoucher() {
        return voucher;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }

    public long getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(long idRegistro) {
        this.idRegistro = idRegistro;
    }

    public boolean isIsPagoExterno() {
        return isPagoExterno;
    }

    public void setIsPagoExterno(boolean isPagoExterno) {
        this.isPagoExterno = isPagoExterno;
    }

    public boolean isPagosExternoValidado() {
        return pagosExternoValidado;
    }

    public void setPagosExternoValidado(boolean pagosExternoValidado) {
        this.pagosExternoValidado = pagosExternoValidado;
    }

    public String getCodigoAdquiriente() {
        return codigoAdquiriente;
    }

    public void setCodigoAdquiriente(String codigoAdquiriente) {
        this.codigoAdquiriente = codigoAdquiriente;
    }

    public long getIdMedioPago() {
        return idMedioPago;
    }

    public void setIdMedioPago(long idMedioPago) {
        this.idMedioPago = idMedioPago;
    }

    public String getFranquicia() {
        return franquicia;
    }

    public void setFranquicia(String franquicia) {
        this.franquicia = franquicia;
    }

    public long getIdAdquiriente() {
        return idAdquiriente;
    }

    public void setIdAdquiriente(long idAdquiriente) {
        this.idAdquiriente = idAdquiriente;
    }

    public long getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(long idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public String getNumeroRecibo() {
        return numeroRecibo;
    }

    public void setNumeroRecibo(String numeroRecibo) {
        this.numeroRecibo = numeroRecibo;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getTipoDeCuenta() {
        return tipoDeCuenta;
    }

    public void setTipoDeCuenta(String tipoDeCuenta) {
        this.tipoDeCuenta = tipoDeCuenta;
    }

    public float getTrm() {
        return trm;
    }

    public void setTrm(float trm) {
        this.trm = trm;
    }

    public int getCodigoDian() {
        return codigoDian;
    }

    public void setCodigoDian(int codigoDian) {
        this.codigoDian = codigoDian;
    }

    public boolean isConfirmacionBono() {
        return confirmacionBono;
    }

    public void setConfirmacionBono(boolean confirmacionBono) {
        this.confirmacionBono = confirmacionBono;
    }

    public boolean isIsBono() {
        return isBono;
    }

    public void setIsBono(boolean isBono) {
        this.isBono = isBono;
    }

    public List<AtributosBono> getBonos_Vive_Terpel() {
        return Bonos_Vive_Terpel;
    }

    public void setBonos_Vive_Terpel(List<AtributosBono> Bonos_Vive_Terpel) {
        this.Bonos_Vive_Terpel = Bonos_Vive_Terpel;
    }

    @Override
    public String toString() {
        return "MediosPagosBean{" + "idRegistro=" + idRegistro + ", id=" + id + ", descripcion=" + descripcion + ", credito=" + credito + ", isCambio=" + isCambio + ", comprobante=" + comprobante + ", minimo_valor=" + minimo_valor + ", maximo_valor=" + maximo_valor + ", estado=" + estado + ", seleccionado=" + seleccionado + ", cambio=" + cambio + ", recibido=" + recibido + ", voucher=" + voucher + ", valor=" + valor + ", cantidad=" + cantidad + ", atributos=" + atributos + ", isPagoExterno=" + isPagoExterno + ", pagosExternoValidado=" + pagosExternoValidado + ", RedencionMillas=" + RedencionMillas + ", codDian=" + codDian + ", BonosViveTerpel=" + BonosViveTerpel + '}';
    }

}
