package com.domain.entities;

import javax.persistence.*;

/**
 * 🚀 MIGRACIÓN: Entidad JPA para resultado de historial de remisiones
 * Representa la información combinada del historial de remisiones SAP
 * 
 * ARQUITECTURA LIMPIA:
 * - Entidad de dominio pura sin lógica de negocio
 * - Corresponde con EntradaCombustibleHistorialBean en funcionalidad
 * - Utilizada para consultas nativas que combinan múltiples tablas
 * 
 * @author Clean Architecture Migration
 * @version 1.0
 */
@Entity
@Table(name = "historial_remision_view") // Tabla virtual para query results
public class HistorialRemisionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID temporal para JPA
    
    @Column(name = "delivery", length = 50)
    private String delivery;
    
    @Column(name = "product", length = 200)
    private String product;
    
    @Column(name = "quantity")
    private Float quantity;
    
    @Column(name = "unit", length = 20)
    private String unit;
    
    @Column(name = "creation_date", length = 20)
    private String creationDate;
    
    @Column(name = "creation_hour", length = 10)
    private String creationHour;
    
    @Column(name = "modification_date", length = 20)
    private String modificationDate;
    
    @Column(name = "modification_hour", length = 10)
    private String modificationHour;
    
    @Column(name = "status", length = 50)
    private String status;
    
    // 🏗️ Constructores
    public HistorialRemisionEntity() {}
    
    public HistorialRemisionEntity(String delivery, String product, Float quantity, String unit, 
                                   String creationDate, String creationHour, String modificationDate, 
                                   String modificationHour, String status) {
        this.delivery = delivery;
        this.product = product;
        this.quantity = quantity;
        this.unit = unit;
        this.creationDate = creationDate;
        this.creationHour = creationHour;
        this.modificationDate = modificationDate;
        this.modificationHour = modificationHour;
        this.status = status;
    }
    
    // 🔍 Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDelivery() {
        return delivery;
    }
    
    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }
    
    public String getProduct() {
        return product;
    }
    
    public void setProduct(String product) {
        this.product = product;
    }
    
    public Float getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public String getCreationDate() {
        return creationDate;
    }
    
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
    
    public String getCreationHour() {
        return creationHour;
    }
    
    public void setCreationHour(String creationHour) {
        this.creationHour = creationHour;
    }
    
    public String getModificationDate() {
        return modificationDate;
    }
    
    public void setModificationDate(String modificationDate) {
        this.modificationDate = modificationDate;
    }
    
    public String getModificationHour() {
        return modificationHour;
    }
    
    public void setModificationHour(String modificationHour) {
        this.modificationHour = modificationHour;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    // 🎯 Métodos de utilidad
    @Override
    public String toString() {
        return "HistorialRemisionEntity{" +
                "delivery='" + delivery + '\'' +
                ", product='" + product + '\'' +
                ", quantity=" + quantity +
                ", unit='" + unit + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HistorialRemisionEntity)) return false;
        HistorialRemisionEntity that = (HistorialRemisionEntity) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
} 