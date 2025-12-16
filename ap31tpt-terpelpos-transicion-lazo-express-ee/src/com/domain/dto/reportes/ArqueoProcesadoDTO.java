package com.domain.dto.reportes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DTO principal que contiene toda la información procesada del arqueo de promotor.
 * Este DTO será enviado al microservicio de impresión.
 * 
 * @author Clean Architecture
 * @version 1.0
 */
public class ArqueoProcesadoDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // Información del promotor
    private String promotor;
    private Long promotorId;
    private Long turnoId;
    private Date fechaInicio;
    
    // Ventas
    private List<VentaCombustibleDTO> ventasCombustible;
    private List<VentaProductoDTO> ventasCanastilla;
    private List<VentaProductoDTO> ventasMarket;
    private List<VentaProductoDTO> ventasComplementarios;
    
    // Medios de pago
    private List<MedioPagoResumenDTO> mediosPago;
    
    // Totales
    private TotalesArqueoDTO totales;
    
    // Metadata
    private int numeroTransaccionesCombustible;
    private int numeroTransaccionesCanastilla;
    private int numeroTransaccionesMarket;
    private int numeroTransaccionesComplementarios;
    
    public ArqueoProcesadoDTO() {
        this.ventasCombustible = new ArrayList<>();
        this.ventasCanastilla = new ArrayList<>();
        this.ventasMarket = new ArrayList<>();
        this.ventasComplementarios = new ArrayList<>();
        this.mediosPago = new ArrayList<>();
        this.totales = new TotalesArqueoDTO();
    }
    
    // Getters y Setters
    public String getPromotor() {
        return promotor;
    }
    
    public void setPromotor(String promotor) {
        this.promotor = promotor;
    }
    
    public Long getPromotorId() {
        return promotorId;
    }
    
    public void setPromotorId(Long promotorId) {
        this.promotorId = promotorId;
    }
    
    public Long getTurnoId() {
        return turnoId;
    }
    
    public void setTurnoId(Long turnoId) {
        this.turnoId = turnoId;
    }
    
    public Date getFechaInicio() {
        return fechaInicio;
    }
    
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    
    public List<VentaCombustibleDTO> getVentasCombustible() {
        return ventasCombustible;
    }
    
    public void setVentasCombustible(List<VentaCombustibleDTO> ventasCombustible) {
        this.ventasCombustible = ventasCombustible;
    }
    
    public List<VentaProductoDTO> getVentasCanastilla() {
        return ventasCanastilla;
    }
    
    public void setVentasCanastilla(List<VentaProductoDTO> ventasCanastilla) {
        this.ventasCanastilla = ventasCanastilla;
    }
    
    public List<VentaProductoDTO> getVentasMarket() {
        return ventasMarket;
    }
    
    public void setVentasMarket(List<VentaProductoDTO> ventasMarket) {
        this.ventasMarket = ventasMarket;
    }
    
    public List<VentaProductoDTO> getVentasComplementarios() {
        return ventasComplementarios;
    }
    
    public void setVentasComplementarios(List<VentaProductoDTO> ventasComplementarios) {
        this.ventasComplementarios = ventasComplementarios;
    }
    
    public List<MedioPagoResumenDTO> getMediosPago() {
        return mediosPago;
    }
    
    public void setMediosPago(List<MedioPagoResumenDTO> mediosPago) {
        this.mediosPago = mediosPago;
    }
    
    public TotalesArqueoDTO getTotales() {
        return totales;
    }
    
    public void setTotales(TotalesArqueoDTO totales) {
        this.totales = totales;
    }
    
    public int getNumeroTransaccionesCombustible() {
        return numeroTransaccionesCombustible;
    }
    
    public void setNumeroTransaccionesCombustible(int numeroTransaccionesCombustible) {
        this.numeroTransaccionesCombustible = numeroTransaccionesCombustible;
    }
    
    public int getNumeroTransaccionesCanastilla() {
        return numeroTransaccionesCanastilla;
    }
    
    public void setNumeroTransaccionesCanastilla(int numeroTransaccionesCanastilla) {
        this.numeroTransaccionesCanastilla = numeroTransaccionesCanastilla;
    }
    
    public int getNumeroTransaccionesMarket() {
        return numeroTransaccionesMarket;
    }
    
    public void setNumeroTransaccionesMarket(int numeroTransaccionesMarket) {
        this.numeroTransaccionesMarket = numeroTransaccionesMarket;
    }
    
    public int getNumeroTransaccionesComplementarios() {
        return numeroTransaccionesComplementarios;
    }
    
    public void setNumeroTransaccionesComplementarios(int numeroTransaccionesComplementarios) {
        this.numeroTransaccionesComplementarios = numeroTransaccionesComplementarios;
    }
    
    @Override
    public String toString() {
        return "ArqueoProcesadoDTO{" +
                "promotor='" + promotor + '\'' +
                ", promotorId=" + promotorId +
                ", turnoId=" + turnoId +
                ", fechaInicio=" + fechaInicio +
                ", ventasCombustible=" + ventasCombustible.size() + " items" +
                ", ventasCanastilla=" + ventasCanastilla.size() + " items" +
                ", ventasMarket=" + ventasMarket.size() + " items" +
                ", mediosPago=" + mediosPago.size() + " items" +
                ", totales=" + totales +
                '}';
    }
}

