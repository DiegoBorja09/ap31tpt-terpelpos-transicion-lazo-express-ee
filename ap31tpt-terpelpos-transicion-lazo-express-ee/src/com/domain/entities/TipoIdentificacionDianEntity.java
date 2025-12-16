package com.domain.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "identificacion_dian", schema = "facturacion_electronica")
public class TipoIdentificacionDianEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "tipo_de_identificacion")
    private String tipoDeIdentificacion;

    @Column(name = "codigo_identificacion")
    private Integer codigoIdentificacion;

    @Column(name = "aplica_fidelizacion")
    private Boolean aplicaFidelizacion;

    @Column(name = "caracteres_permitidos")
    private String caracteresPermitidos;

    @Column(name = "limite_caracteres")
    private Integer limiteCaracteres;

    @Column(name = "fecha_creacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipoDeIdentificacion() {
        return tipoDeIdentificacion;
    }

    public void setTipoDeIdentificacion(String tipoDeIdentificacion) {
        this.tipoDeIdentificacion = tipoDeIdentificacion;
    }

    public Integer getCodigoIdentificacion() {
        return codigoIdentificacion;
    }

    public void setCodigoIdentificacion(Integer codigoIdentificacion) {
        this.codigoIdentificacion = codigoIdentificacion;
    }

    public Boolean getAplicaFidelizacion() {
        return aplicaFidelizacion;
    }

    public void setAplicaFidelizacion(Boolean aplicaFidelizacion) {
        this.aplicaFidelizacion = aplicaFidelizacion;
    }

    public String getCaracteresPermitidos() {
        return caracteresPermitidos;
    }

    public void setCaracteresPermitidos(String caracteresPermitidos) {
        this.caracteresPermitidos = caracteresPermitidos;
    }

    public Integer getLimiteCaracteres() {
        return limiteCaracteres;
    }

    public void setLimiteCaracteres(Integer limiteCaracteres) {
        this.limiteCaracteres = limiteCaracteres;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
