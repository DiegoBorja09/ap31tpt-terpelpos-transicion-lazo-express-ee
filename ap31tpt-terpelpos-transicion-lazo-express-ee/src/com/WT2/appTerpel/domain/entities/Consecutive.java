/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.domain.entities;

import java.util.Date;

/**
 *
 * @author USUARIO
 */
public class Consecutive {

    private long id;
    private long empresaId;
    private int tipoDocumento;
    private String prefijo;
    private Date fecha_inicio;
    private Date fecha_fin;
    private long consecutivoInicial;
    private long consecutivoActual;
    private long consecutivoFinal;
    private String estado;
    private String resolucion;
    private String observaciones;
    private long equipoId;
    private ConsecutiveAtribute consecutiveAtribute;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(long empresaId) {
        this.empresaId = empresaId;
    }

    public int getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(int tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public Date getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(Date fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public Date getFecha_fin() {
        return fecha_fin;
    }

    public void setFecha_fin(Date fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    public long getConsecutivoInicial() {
        return consecutivoInicial;
    }

    public void setConsecutivoInicial(long consecutivoInicial) {
        this.consecutivoInicial = consecutivoInicial;
    }

    public long getConsecutivoActual() {
        return consecutivoActual;
    }

    public void setConsecutivoActual(long consecutivoActual) {
        this.consecutivoActual = consecutivoActual;
    }

    public long getConsecutivoFinal() {
        return consecutivoFinal;
    }

    public void setConsecutivoFinal(long consecutivoFinal) {
        this.consecutivoFinal = consecutivoFinal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getResolucion() {
        return resolucion;
    }

    public void setResolucion(String resolucion) {
        this.resolucion = resolucion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public long getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(long equipoId) {
        this.equipoId = equipoId;
    }

    public ConsecutiveAtribute getConsecutiveAtribute() {
        return consecutiveAtribute;
    }

    public void setConsecutiveAtribute(ConsecutiveAtribute consecutiveAtribute) {
        this.consecutiveAtribute = consecutiveAtribute;
    }
    
    

}
