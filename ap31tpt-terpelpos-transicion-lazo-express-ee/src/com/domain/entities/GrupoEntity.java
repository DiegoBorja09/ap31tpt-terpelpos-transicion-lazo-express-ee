package com.domain.entities;

import javax.persistence.*;


@Entity
@Table(name = "grupos")
public class GrupoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String grupo;

    private String estado;

    @Column(name = "grupos_tipos_id")
    private Long gruposTiposId;

    @Column(name = "empresas_id")
    private Long empresasId;

    @Column(name = "grupos_id")
    private Long gruposId;

    @Column(name = "url_foto")
    private String urlFoto;

    @Column(columnDefinition = "json")
    private String atributos;

    // === Getters and Setters ===

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getGruposTiposId() {
        return gruposTiposId;
    }

    public void setGruposTiposId(Long gruposTiposId) {
        this.gruposTiposId = gruposTiposId;
    }

    public Long getEmpresasId() {
        return empresasId;
    }

    public void setEmpresasId(Long empresasId) {
        this.empresasId = empresasId;
    }

    public Long getGruposId() {
        return gruposId;
    }

    public void setGruposId(Long gruposId) {
        this.gruposId = gruposId;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public String getAtributos() {
        return atributos;
    }

    public void setAtributos(String atributos) {
        this.atributos = atributos;
    }
}
