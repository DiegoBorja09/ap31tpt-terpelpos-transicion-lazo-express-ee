package com.domain.entities;

import javax.persistence.*;
import java.util.List;

/**
 * 🚀 MIGRACIÓN: Entidad JPA para tabla sap.tbl_estados_sap
 * Representa los estados de las remisiones y productos SAP
 * 
 * ARQUITECTURA LIMPIA:
 * - Entidad de dominio pura sin lógica de negocio
 * - Mapeo JPA con relaciones bidireccionales
 * - Referenciada por RemisionProductoSapEntity
 * 
 * @author Clean Architecture Migration
 * @version 1.0
 */
@Entity
@Table(name = "tbl_estados_sap", schema = "sap")
public class EstadoSapEntity {
    
    @Id
    @Column(name = "id_estado")
    private Integer idEstado;
    
    @Column(name = "descripcion", length = 100)
    private String descripcion;
    
    
    // 🔗 Relación bidireccional con RemisionProductoSapEntity
    @OneToMany(mappedBy = "estadoSap", fetch = FetchType.LAZY)
    private List<RemisionProductoSapEntity> remisionesProductos;
    
    // 🏗️ Constructores
    public EstadoSapEntity() {}
    
    public EstadoSapEntity(Integer idEstado, String descripcion) {
        this.idEstado = idEstado;
        this.descripcion = descripcion;
    }
    
    // 🔍 Getters y Setters
    public Integer getIdEstado() {
        return idEstado;
    }
    
    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
     
    public List<RemisionProductoSapEntity> getRemisionesProductos() {
        return remisionesProductos;
    }
    
    public void setRemisionesProductos(List<RemisionProductoSapEntity> remisionesProductos) {
        this.remisionesProductos = remisionesProductos;
    }
    
    // 🎯 Métodos de utilidad
    @Override
    public String toString() {
        return "EstadoSapEntity{" +
                "idEstado=" + idEstado +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EstadoSapEntity)) return false;
        EstadoSapEntity that = (EstadoSapEntity) o;
        return idEstado != null && idEstado.equals(that.idEstado);
    }
    
    @Override
    public int hashCode() {
        return idEstado != null ? idEstado.hashCode() : 0;
    }
    
} 