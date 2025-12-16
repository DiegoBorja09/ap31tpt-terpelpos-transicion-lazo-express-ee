package com.domain.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "transmisiones_remision")
public class TransmisionRemisionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transmicion_remision")
    private Long id;

    @Column(name = "request", columnDefinition = "text")
    private String request;

    @Column(name = "id_movimiento")
    private Long idMovimiento;

    @Column(name = "sincronizado")
    private Integer sincronizado;

    @Column(name = "fecha_registro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    @Column(name = "ano")
    private Integer ano;

    @Column(name = "mes")
    private Integer mes;

    @Column(name = "dia")
    private Integer dia;

    // Getters y Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRequest() { return request; }
    public void setRequest(String request) { this.request = request; }

    public Long getIdMovimiento() { return idMovimiento; }
    public void setIdMovimiento(Long idMovimiento) { this.idMovimiento = idMovimiento; }

    public Integer getSincronizado() { return sincronizado; }
    public void setSincronizado(Integer sincronizado) { this.sincronizado = sincronizado; }

    public Date getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }

    public Integer getMes() { return mes; }
    public void setMes(Integer mes) { this.mes = mes; }

    public Integer getDia() { return dia; }
    public void setDia(Integer dia) { this.dia = dia; }
}
