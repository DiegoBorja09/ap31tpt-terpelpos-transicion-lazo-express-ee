package com.domain.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "datafonos", schema = "datafonos")
public class DatafonosEntity {

    @Id
    @Column(name = "id_datafono")
    private Long idDatafono;

    @Column(name = "id_empresa")
    private Long idEmpresa;

    @Column(name = "id_adquiriente") 
    private Long idAdquiriente;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "serial")
    private String serial;

    @Column(name = "plaqueta")
    private String plaqueta;

    @Column(name = "codigo_terminal")
    private String codigoTerminal;

    @Column(name = "ind_activo")
    private Short indActivo;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(name = "id_usuario_registro")
    private Long idUsuarioRegistro;

    @Column(name = "fecha_ult_actualizacion")
    private LocalDateTime fechaUltActualizacion;

    @Column(name = "id_usuario_ult_actualizacion")
    private Long idUsuarioUltActualizacion;

    public Long getIdDatafono() {
        return idDatafono;
    }

    public void setIdDatafono(Long idDatafono) {
        this.idDatafono = idDatafono;
    }

    public Long getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Long idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public Long getIdAdquiriente() {
        return idAdquiriente;
    }

    public void setIdAdquiriente(Long idAdquiriente) {
        this.idAdquiriente = idAdquiriente;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getPlaqueta() {
        return plaqueta;
    }

    public void setPlaqueta(String plaqueta) {
        this.plaqueta = plaqueta;
    }

    public String getCodigoTerminal() {
        return codigoTerminal;
    }

    public void setCodigoTerminal(String codigoTerminal) {
        this.codigoTerminal = codigoTerminal;
    }

    public Short getIndActivo() {
        return indActivo;
    }

    public void setIndActivo(Short indActivo) {
        this.indActivo = indActivo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Long getIdUsuarioRegistro() {
        return idUsuarioRegistro;
    }

    public void setIdUsuarioRegistro(Long idUsuarioRegistro) {
        this.idUsuarioRegistro = idUsuarioRegistro;
    }

    public LocalDateTime getFechaUltActualizacion() {
        return fechaUltActualizacion;
    }

    public void setFechaUltActualizacion(LocalDateTime fechaUltActualizacion) {
        this.fechaUltActualizacion = fechaUltActualizacion;
    }

    public Long getIdUsuarioUltActualizacion() {
        return idUsuarioUltActualizacion;
    }

    public void setIdUsuarioUltActualizacion(Long idUsuarioUltActualizacion) {
        this.idUsuarioUltActualizacion = idUsuarioUltActualizacion;
    }
}