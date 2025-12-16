package com.domain.entities;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 🚀 MIGRACIÓN: Entidad JPA para tabla sap.tbl_remisiones_productos_sap
 * Representa los productos específicos de cada remisión SAP
 * 
 * ARQUITECTURA LIMPIA:
 * - Entidad de dominio pura sin lógica de negocio
 * - Mapeo JPA con relaciones a otras entidades
 * - Tabla de relación entre remisiones SAP y productos
 * 
 * @author Clean Architecture Migration
 * @version 1.0
 */
@Entity
@Table(name = "tbl_remisiones_productos_sap", schema = "sap")
public class RemisionProductoSapEntity {
    
    @Id
    @Column(name = "id_remision_producto")
    private Long idRemisionProducto;
    
    @Column(name = "id_remision_sap")
    private Long idRemisionSap;
    
    @Column(name = "id_producto")
    private Long idProducto;
    
    @Column(name = "product", length = 50)
    private String product;
    
    @Column(name = "quantity", precision = 10, scale = 3)
    private BigDecimal quantity;
    
    @Column(name = "unit", length = 10)
    private String unit;
    
    @Column(name = "sales_cost_value", precision = 15, scale = 6)
    private BigDecimal salesCostValue;
    
    @Column(name = "id_estado")
    private Integer idEstado;
    
    @Column(name = "creation_date")
    private java.time.LocalDate creationDate;
    
    @Column(name = "creation_hour")
    private java.time.LocalTime creationHour;
    
    // 🔗 Relaciones JPA
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_remision_sap", insertable = false, updatable = false)
    private RemisionSapEntity remisionSap;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", insertable = false, updatable = false)
    private ProductoEntity producto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado", insertable = false, updatable = false)
    private EstadoSapEntity estadoSap;
    
    // 🏗️ Constructores
    public RemisionProductoSapEntity() {}
    
    public RemisionProductoSapEntity(Long idRemisionSap, Long idProducto, String product, 
                                   BigDecimal quantity, String unit, Integer idEstado) {
        this.idRemisionSap = idRemisionSap;
        this.idProducto = idProducto;
        this.product = product;
        this.quantity = quantity;
        this.unit = unit;
        this.idEstado = idEstado;
    }
    
    // 🔍 Getters y Setters
    public Long getIdRemisionProducto() {
        return idRemisionProducto;
    }
    
    public void setIdRemisionProducto(Long idRemisionProducto) {
        this.idRemisionProducto = idRemisionProducto;
    }
    
    public Long getIdRemisionSap() {
        return idRemisionSap;
    }
    
    public void setIdRemisionSap(Long idRemisionSap) {
        this.idRemisionSap = idRemisionSap;
    }
    
    public Long getIdProducto() {
        return idProducto;
    }
    
    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }
    
    public String getProduct() {
        return product;
    }
    
    public void setProduct(String product) {
        this.product = product;
    }
    
    public BigDecimal getQuantity() {
        return quantity;
    }
    
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public BigDecimal getSalesCostValue() {
        return salesCostValue;
    }
    
    public void setSalesCostValue(BigDecimal salesCostValue) {
        this.salesCostValue = salesCostValue;
    }
    
    public Integer getIdEstado() {
        return idEstado;
    }
    
    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }
    
    public java.time.LocalDate getCreationDate() {
        return creationDate;
    }
    
    public void setCreationDate(java.time.LocalDate creationDate) {
        this.creationDate = creationDate;
    }
    
    public java.time.LocalTime getCreationHour() {
        return creationHour;
    }
    
    public void setCreationHour(java.time.LocalTime creationHour) {
        this.creationHour = creationHour;
    }
    
    // 🔗 Getters y Setters para relaciones
    public RemisionSapEntity getRemisionSap() {
        return remisionSap;
    }
    
    public void setRemisionSap(RemisionSapEntity remisionSap) {
        this.remisionSap = remisionSap;
    }
    
    public ProductoEntity getProducto() {
        return producto;
    }
    
    public void setProducto(ProductoEntity producto) {
        this.producto = producto;
    }
    
    public EstadoSapEntity getEstadoSap() {
        return estadoSap;
    }
    
    public void setEstadoSap(EstadoSapEntity estadoSap) {
        this.estadoSap = estadoSap;
    }
    
    // 🎯 Métodos de utilidad
    @Override
    public String toString() {
        return "RemisionProductoSapEntity{" +
                "idRemisionProducto=" + idRemisionProducto +
                ", idRemisionSap=" + idRemisionSap +
                ", idProducto=" + idProducto +
                ", product='" + product + '\'' +
                ", quantity=" + quantity +
                ", unit='" + unit + '\'' +
                ", idEstado=" + idEstado +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RemisionProductoSapEntity)) return false;
        RemisionProductoSapEntity that = (RemisionProductoSapEntity) o;
        return idRemisionProducto != null && idRemisionProducto.equals(that.idRemisionProducto);
    }
    
    @Override
    public int hashCode() {
        return idRemisionProducto != null ? idRemisionProducto.hashCode() : 0;
    }
    
    /**
     * 🔍 Método de conveniencia para obtener la descripción del estado
     */
    public String getEstadoDescripcion() {
        return estadoSap != null ? estadoSap.getDescripcion() : null;
    }
    
    /**
     * 🔍 Método de conveniencia para obtener la descripción del producto
     */
    public String getProductoDescripcion() {
        return producto != null ? producto.getDescripcion() : null;
    }
} 