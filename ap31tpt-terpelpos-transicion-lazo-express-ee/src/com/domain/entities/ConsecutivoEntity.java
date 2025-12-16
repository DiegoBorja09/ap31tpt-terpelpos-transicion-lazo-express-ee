package com.domain.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "consecutivos", schema = "public")
public class ConsecutivoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresas_id")
    private Long empresasId;

    @Column(name = "tipo_documento")
    private Integer tipoDocumento;

    private String prefijo;

    @Column(name = "fecha_inicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInicio;

    @Column(name = "fecha_fin")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaFin;

    @Column(name = "consecutivo_inicial")
    private Long consecutivoInicial;

    @Column(name = "consecutivo_actual")
    private Long consecutivoActual;

    @Column(name = "consecutivo_final")
    private Long consecutivoFinal;

    private String estado;

    private String resolucion;

    private String observaciones;

    @Column(name = "equipos_id")
    private Long equiposId;

    @Column(name = "cs_atributos", columnDefinition = "json")
    private String csAtributos;

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmpresasId() {
        return empresasId;
    }

    public void setEmpresasId(Long empresasId) {
        this.empresasId = empresasId;
    }

    public Integer getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(Integer tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Long getConsecutivoInicial() {
        return consecutivoInicial;
    }

    public void setConsecutivoInicial(Long consecutivoInicial) {
        this.consecutivoInicial = consecutivoInicial;
    }

    public Long getConsecutivoActual() {
        return consecutivoActual;
    }

    public void setConsecutivoActual(Long consecutivoActual) {
        this.consecutivoActual = consecutivoActual;
    }

    public Long getConsecutivoFinal() {
        return consecutivoFinal;
    }

    public void setConsecutivoFinal(Long consecutivoFinal) {
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

    public Long getEquiposId() {
        return equiposId;
    }

    public void setEquiposId(Long equiposId) {
        this.equiposId = equiposId;
    }

    public String getCsAtributos() {
        return csAtributos;
    }

    public void setCsAtributos(String csAtributos) {
        this.csAtributos = csAtributos;
    }
}
