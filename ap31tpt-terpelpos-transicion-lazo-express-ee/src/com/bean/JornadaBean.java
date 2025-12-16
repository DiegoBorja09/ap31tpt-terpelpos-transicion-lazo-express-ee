/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bean;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

/**
 *
 * @author novus
 */
public class JornadaBean {

    private boolean sincronizado;
    private boolean iniciada;
    private long grupoJornada;
    private long personaId;
    private boolean existeContadores;
    private float saldo;
    private boolean ultimo;
    private Date fechaInicial;
    private Date fechaFinal;
    private boolean primerRegistro;
    private int cantidadVentas;
    private float totalVentas;
    private float totalVentasCanastilla;
    private int cantidadVentasCanastilla;
    private Date fechaImpresion;
    private ArrayList<MovimientosBean> ventasCanastilla;

    public ArrayList<MovimientosBean> getVentasCanastilla() {
        return ventasCanastilla;
    }

    public void setVentasCanastilla(ArrayList<MovimientosBean> ventasCanastilla) {
        this.ventasCanastilla = ventasCanastilla;
    }

    public int getCantidadVentasCanastilla() {
        return cantidadVentasCanastilla;
    }

    public void setCantidadVentasCanastilla(int cantidadVentasCanastilla) {
        this.cantidadVentasCanastilla = cantidadVentasCanastilla;
    }

    public float getTotalVentasCanastilla() {
        return totalVentasCanastilla;
    }

    public void setTotalVentasCanastilla(float totalVentasCanastilla) {
        this.totalVentasCanastilla = totalVentasCanastilla;
    }

    private ArrayList<VentasBean> calibraciones;
    private ArrayList<VentasBean> consumoPropio;
    private JsonObject medidasTanquesIniciales;
    private JsonObject medidasTanquesFinales;

    public JsonObject getMedidasTanquesIniciales() {
        return medidasTanquesIniciales;
    }

    public void setMedidasTanquesIniciales(JsonObject medidasTanquesIniciales) {
        this.medidasTanquesIniciales = medidasTanquesIniciales;
    }

    public JsonObject getMedidasTanquesFinales() {
        return medidasTanquesFinales;
    }

    public void setMedidasTanquesFinales(JsonObject medidasTanquesFinales) {
        this.medidasTanquesFinales = medidasTanquesFinales;
    }

    public ArrayList<VentasBean> getCalibraciones() {
        return calibraciones;
    }

    public void setCalibraciones(ArrayList<VentasBean> calibraciones) {
        this.calibraciones = calibraciones;
    }

    public ArrayList<VentasBean> getConsumoPropio() {
        return consumoPropio;
    }

    public void setConsumoPropio(ArrayList<VentasBean> consumoPropio) {
        this.consumoPropio = consumoPropio;
    }

    public ArrayList<VentasBean> getPredeterminadas() {
        return predeterminadas;
    }

    public void setPredeterminadas(ArrayList<VentasBean> predeterminadas) {
        this.predeterminadas = predeterminadas;
    }

    private ArrayList<VentasBean> predeterminadas;

    private ArrayList<ProductoBean> ventasProductos = new ArrayList<>();

    public ArrayList<ProductoBean> getVentasProductos() {
        return ventasProductos;
    }

    public void setVentasProductos(ArrayList<ProductoBean> ventasProductos) {
        this.ventasProductos = ventasProductos;
    }

    private ArrayList<VentasBean> ventas = new ArrayList<>();

    public ArrayList<VentasBean> getVentas() {
        return ventas;
    }

    public void setVentas(ArrayList<VentasBean> ventas) {
        this.ventas = ventas;
    }

    private PersonaBean persona;
    private ArrayList<MediosPagosBean> medios = new ArrayList<>();
    private TreeMap<Long, ArrayList<MediosPagosBean>> mediosxsurtido = new TreeMap<>();

    public TreeMap<Long, ArrayList<MediosPagosBean>> getMediosxsurtido() {
        return mediosxsurtido;
    }

    public void setMediosxsurtido(TreeMap<Long, ArrayList<MediosPagosBean>> mediosxsurtido) {
        this.mediosxsurtido = mediosxsurtido;
    }

    public ArrayList<MediosPagosBean> getMedios() {
        return medios;
    }

    public void setMedios(ArrayList<MediosPagosBean> medios) {
        this.medios = medios;
    }

    public PersonaBean getPersona() {
        return persona;
    }

    public void setPersona(PersonaBean persona) {
        this.persona = persona;
    }

    private ArrayList<ProductoBean> inventarios = new ArrayList<>();
    private ArrayList<Surtidor> lecturasIniciales = new ArrayList<>();
    private ArrayList<Surtidor> lecturasFinales = new ArrayList<>();

    public long getGrupoJornada() {
        return grupoJornada;
    }

    public void setGrupoJornada(long grupoJornada) {
        this.grupoJornada = grupoJornada;
    }

    public long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(long personaId) {
        this.personaId = personaId;
    }

    public boolean isIniciada() {
        return iniciada;
    }

    public void setIniciada(boolean iniciada) {
        this.iniciada = iniciada;
    }

    public boolean isExisteContadores() {
        return existeContadores;
    }

    public void setExisteContadores(boolean existeContadores) {
        this.existeContadores = existeContadores;
    }

    public boolean isSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(boolean sincronizado) {
        this.sincronizado = sincronizado;
    }

    public float getSaldo() {
        return saldo;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }

    public ArrayList<ProductoBean> getInventarios() {
        return inventarios;
    }

    public void setInventarios(ArrayList<ProductoBean> inventarios) {
        this.inventarios = inventarios;
    }

    public boolean isUltimo() {
        return ultimo;
    }

    public void setUltimo(boolean ultimo) {
        this.ultimo = ultimo;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public boolean isPrimerRegistro() {
        return primerRegistro;
    }

    public void setPrimerRegistro(boolean primerRegistro) {
        this.primerRegistro = primerRegistro;
    }

    public ArrayList<Surtidor> getLecturasIniciales() {
        return lecturasIniciales;
    }

    public void setLecturasIniciales(ArrayList<Surtidor> lecturas) {
        this.lecturasIniciales = lecturas;
    }

    public ArrayList<Surtidor> getLecturasFinales() {
        return lecturasFinales;
    }

    public void setLecturasFinales(ArrayList<Surtidor> lecturas) {
        this.lecturasFinales = lecturas;
    }

    public int getCantidadVentas() {
        return cantidadVentas;
    }

    public void setCantidadVentas(int cantidadVentas) {
        this.cantidadVentas = cantidadVentas;
    }

    public float getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(float totalVentas) {
        this.totalVentas = totalVentas;
    }

    /**
     * @return the fechaImpresion
     */
    public Date getFechaImpresion() {
        return fechaImpresion;
    }

    /**
     * @param fechaImpresion the fechaImpresion to set
     */
    public void setFechaImpresion(Date fechaImpresion) {
        this.fechaImpresion = fechaImpresion;
    }

}
