package com.domain.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "transmision") 
public class TransmisionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "reintentos_cliente")
    private Integer reintentosCliente;

    @Column(name = "equipo_id")
    private Long equipoId;

    @Column(name = "fecha_ultima")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaUltima;

    @Column(name = "reintentos")
    private Integer reintentos;

    @Column(name = "status")
    private Integer status;

    @Column(name = "sincronizado")
    private Integer sincronizado;

    @Column(name = "fecha_generado")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaGenerado;

    @Column(name = "fecha_trasmitido")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaTrasmitido;

    @Column(name = "request", columnDefinition = "text")
    private String request;

    @Column(name = "response", columnDefinition = "text")
    private String response;

    @Column(name = "url")
    private String url;

    @Column(name = "method")
    private String method;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getReintentosCliente() { return reintentosCliente; }
    public void setReintentosCliente(Integer reintentosCliente) { this.reintentosCliente = reintentosCliente; }

    public Long getEquipoId() { return equipoId; }
    public void setEquipoId(Long equipoId) { this.equipoId = equipoId; }

    public Date getFechaUltima() { return fechaUltima; }
    public void setFechaUltima(Date fechaUltima) { this.fechaUltima = fechaUltima; }

    public Integer getReintentos() { return reintentos; }
    public void setReintentos(Integer reintentos) { this.reintentos = reintentos; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getSincronizado() { return sincronizado; }
    public void setSincronizado(Integer sincronizado) { this.sincronizado = sincronizado; }

    public Date getFechaGenerado() { return fechaGenerado; }
    public void setFechaGenerado(Date fechaGenerado) { this.fechaGenerado = fechaGenerado; }

    public Date getFechaTrasmitido() { return fechaTrasmitido; }
    public void setFechaTrasmitido(Date fechaTrasmitido) { this.fechaTrasmitido = fechaTrasmitido; }

    public String getRequest() { return request; }
    public void setRequest(String request) { this.request = request; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
}
