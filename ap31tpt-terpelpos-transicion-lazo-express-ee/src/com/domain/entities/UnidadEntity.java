package com.domain.entities;

import javax.persistence.*;

@Entity
@Table(name = "unidades")
public class UnidadEntity {

    @Id
    private Long id;

    private String descripcion;

    private Integer valor;

    @Column(name = "empresas_id")
    private Long empresasId;

    private String estado;

    @Column(name = "unidad_basica")
    private String unidadBasica;

    @Column(name = "alias")
    private String alias;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getValor() { return valor; }
    public void setValor(Integer valor) { this.valor = valor; }

    public Long getEmpresasId() { return empresasId; }
    public void setEmpresasId(Long empresasId) { this.empresasId = empresasId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getUnidadBasica() { return unidadBasica; }
    public void setUnidadBasica(String unidadBasica) { this.unidadBasica = unidadBasica; }

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }
}
