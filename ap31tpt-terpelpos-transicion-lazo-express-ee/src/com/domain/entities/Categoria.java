package com.domain.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "grupos")
public class Categoria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "grupo")
    private String grupo;
    
    @OneToMany(mappedBy = "categoria")
    private List<GrupoEntity> gruposEntidad;

    // Getters y Setters
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

    public List<GrupoEntity> getGruposEntidad() {
        return gruposEntidad;
    }

    public void setGruposEntidad(List<GrupoEntity> gruposEntidad) {
        this.gruposEntidad = gruposEntidad;
    }
} 