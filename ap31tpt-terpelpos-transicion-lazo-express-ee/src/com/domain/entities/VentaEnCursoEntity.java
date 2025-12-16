package com.domain.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "lt_ventas_curso", schema = "public")
public class VentaEnCursoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_autorizacion")
    private BigDecimal codigoAutorizacion;

    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    @Column(name = "negocio")
    private String negocio;

    @Column(name = "estado")
    private String estado;

    // Constructors
    public VentaEnCursoEntity() {
    }

    public VentaEnCursoEntity(String negocio, String estado) {
        this.negocio = negocio;
        this.estado = estado;
        this.fecha = new Date(); // Auto-asignar fecha actual
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getCodigoAutorizacion() {
        return codigoAutorizacion;
    }

    public void setCodigoAutorizacion(BigDecimal codigoAutorizacion) {
        this.codigoAutorizacion = codigoAutorizacion;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getNegocio() {
        return negocio;
    }

    public void setNegocio(String negocio) {
        this.negocio = negocio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "VentaEnCursoEntity{" +
                "id=" + id +
                ", codigoAutorizacion=" + codigoAutorizacion +
                ", fecha=" + fecha +
                ", negocio='" + negocio + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
} 