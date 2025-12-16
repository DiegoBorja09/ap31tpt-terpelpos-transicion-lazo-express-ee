package com.domain.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@Entity
@Table(name = "jornadas")
public class JornadaEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "fecha_inicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInicio;

    @Column(name = "fecha_fin")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personas_id")
    private CtPerson persona;

    @Column(name = "sincronizado")
    private Integer sincronizado;

    @Column(name = "grupo_jornada")
    private Long grupoJornada;

    @Column(name = "saldo", precision = 19, scale = 2)
    private BigDecimal saldo;

    @Column(name = "surtidores_id")
    private Long surtidoresId;

    @Column(name = "equipos_id")
    private Long equiposId;

    @Column(name = "atributos", columnDefinition = "json")
    private String atributos;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public CtPerson getPersona() {
        return persona;
    }

    public void setPersona(CtPerson persona) {
        this.persona = persona;
    }

    public Integer getSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(Integer sincronizado) {
        this.sincronizado = sincronizado;
    }

    public Long getGrupoJornada() {
        return grupoJornada;
    }

    public void setGrupoJornada(Long grupoJornada) {
        this.grupoJornada = grupoJornada;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Long getSurtidoresId() {
        return surtidoresId;
    }

    public void setSurtidoresId(Long surtidoresId) {
        this.surtidoresId = surtidoresId;
    }

    public Long getEquiposId() {
        return equiposId;
    }

    public void setEquiposId(Long equiposId) {
        this.equiposId = equiposId;
    }

    public String getAtributos() {
        return atributos;
    }

    public void setAtributos(String atributos) {
        this.atributos = atributos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JornadaEntity that = (JornadaEntity) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
} 