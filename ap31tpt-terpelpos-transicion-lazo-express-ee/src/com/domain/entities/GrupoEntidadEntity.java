package com.domain.entities;

import javax.persistence.*;

/**
 * Entidad JPA para la tabla grupos_entidad
 * Representa la relación muchos a muchos entre grupos y entidades (productos)
 */
@Entity
@Table(name = "grupos_entidad", schema = "public")
public class GrupoEntidadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "grupo_id")
    private Long grupoId;

    @Column(name = "entidad_id") 
    private Long entidadId;

    // Relaciones JPA
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id", insertable = false, updatable = false)
    private GrupoEntity grupo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entidad_id", insertable = false, updatable = false)
    private ProductoEntity producto;

    // Constructores
    public GrupoEntidadEntity() {
    }

    public GrupoEntidadEntity(Long grupoId, Long entidadId) {
        this.grupoId = grupoId;
        this.entidadId = entidadId;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public Long getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(Long entidadId) {
        this.entidadId = entidadId;
    }

    public GrupoEntity getGrupo() {
        return grupo;
    }

    public void setGrupo(GrupoEntity grupo) {
        this.grupo = grupo;
    }

    public ProductoEntity getProducto() {
        return producto;
    }

    public void setProducto(ProductoEntity producto) {
        this.producto = producto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GrupoEntidadEntity that = (GrupoEntidadEntity) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "GrupoEntidadEntity{" +
                "id=" + id +
                ", grupoId=" + grupoId +
                ", entidadId=" + entidadId +
                '}';
    }
} 