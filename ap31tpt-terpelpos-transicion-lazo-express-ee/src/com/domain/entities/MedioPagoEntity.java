package com.domain.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "medios_pagos")
public class MedioPagoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "id_sincronizado")
    private Long idSincronizado;

    @Column(name = "mp_atributos", columnDefinition = "json")
    private String mpAtributos;

    @Column(name = "codigo_adquiriente")
    private String codigoAdquiriente;

    @Column(name = "id_medio_pago_recurso")
    private String idMedioPagoRecurso;

    @Column(name = "base64")
    private String base64;

    @Column(name = "id_medio_pago_recurso_seleccionado")
    private String idMedioPagoRecursoSeleccionado;

    @Column(name = "base64_seleleccionado")
    private String base64Seleccionado;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "estado")
    private String estado;

    // Constructor por defecto
    public MedioPagoEntity() {
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdSincronizado() {
        return idSincronizado;
    }

    public void setIdSincronizado(Long idSincronizado) {
        this.idSincronizado = idSincronizado;
    }

    public String getMpAtributos() {
        return mpAtributos;
    }

    public void setMpAtributos(String mpAtributos) {
        this.mpAtributos = mpAtributos;
    }

    public String getCodigoAdquiriente() {
        return codigoAdquiriente;
    }

    public void setCodigoAdquiriente(String codigoAdquiriente) {
        this.codigoAdquiriente = codigoAdquiriente;
    }

    public String getIdMedioPagoRecurso() {
        return idMedioPagoRecurso;
    }

    public void setIdMedioPagoRecurso(String idMedioPagoRecurso) {
        this.idMedioPagoRecurso = idMedioPagoRecurso;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public String getIdMedioPagoRecursoSeleccionado() {
        return idMedioPagoRecursoSeleccionado;
    }

    public void setIdMedioPagoRecursoSeleccionado(String idMedioPagoRecursoSeleccionado) {
        this.idMedioPagoRecursoSeleccionado = idMedioPagoRecursoSeleccionado;
    }

    public String getBase64Seleccionado() {
        return base64Seleccionado;
    }

    public void setBase64Seleccionado(String base64Seleccionado) {
        this.base64Seleccionado = base64Seleccionado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "MedioPagoEntity{" +
                "id=" + id +
                ", idSincronizado=" + idSincronizado +
                ", descripcion='" + descripcion + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
} 