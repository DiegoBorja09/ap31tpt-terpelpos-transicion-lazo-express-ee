package com.domain.entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.List;

/**
 * 🚀 MIGRACIÓN: Entidad JPA para tabla sap.tbl_remisiones_sap
 * Representa la información principal de una remisión SAP
 * 
 * ARQUITECTURA LIMPIA:
 * - Entidad de dominio pura sin lógica de negocio
 * - Mapeo JPA con anotaciones estándar
 * - Corresponde con EntradaCombustibleBean en funcionalidad
 * - Relaciones bidireccionales con productos
 * 
 * @author Clean Architecture Migration
 * @version 1.0
 */
@Entity
@Table(name = "tbl_remisiones_sap", schema = "sap")
public class RemisionSapEntity {
    
    @Id
    @Column(name = "id_remision_sap")
    private Long idRemisionSap;
    
    @Column(name = "delivery", nullable = false, length = 50)
    private String delivery;
    
    @Column(name = "document_date", length = 20)
    private String documentDate;
    
    @Column(name = "way_bill", length = 50)
    private String wayBill;
    
    @Column(name = "logistic_center", length = 100)
    private String logisticCenter;
    
    @Column(name = "supplying_center", length = 100)
    private String supplyingCenter;
    
    @Column(name = "status", length = 10)
    private String status;
    
    @Column(name = "modification_date", length = 20)
    private String modificationDate;
    
    @Column(name = "modification_hour", length = 10)
    private String modificationHour;
    
    @Column(name = "frontier_law", length = 50)
    private String frontierLaw;
    
    @Column(name = "creation_date")
    private LocalDate creationDate;
    
    @Column(name = "creation_hour")
    private LocalTime creationHour;
    
    @Column(name = "id_estado")
    private Integer idEstado;
    
    // 🔗 NUEVA: Relación bidireccional con productos de la remisión
    @OneToMany(mappedBy = "remisionSap", fetch = FetchType.LAZY)
    private List<RemisionProductoSapEntity> productos;
    
    // 🏗️ Constructores
    public RemisionSapEntity() {}
    
    public RemisionSapEntity(String delivery) {
        this.delivery = delivery;
    }
    
    // 🔍 Getters y Setters
    public Long getIdRemisionSap() {
        return idRemisionSap;
    }
    
    public void setIdRemisionSap(Long idRemisionSap) {
        this.idRemisionSap = idRemisionSap;
    }
    
    public String getDelivery() {
        return delivery;
    }
    
    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }
    
    public String getDocumentDate() {
        return documentDate;
    }
    
    public void setDocumentDate(String documentDate) {
        this.documentDate = documentDate;
    }
    
    public String getWayBill() {
        return wayBill;
    }
    
    public void setWayBill(String wayBill) {
        this.wayBill = wayBill;
    }
    
    public String getLogisticCenter() {
        return logisticCenter;
    }
    
    public void setLogisticCenter(String logisticCenter) {
        this.logisticCenter = logisticCenter;
    }
    
    public String getSupplyingCenter() {
        return supplyingCenter;
    }
    
    public void setSupplyingCenter(String supplyingCenter) {
        this.supplyingCenter = supplyingCenter;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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
    
    public String getFrontierLaw() {
        return frontierLaw;
    }
    
    public void setFrontierLaw(String frontierLaw) {
        this.frontierLaw = frontierLaw;
    }
    
    public LocalDate getCreationDate() {
        return creationDate;
    }
    
    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }
    
    public LocalTime getCreationHour() {
        return creationHour;
    }
    
    public void setCreationHour(LocalTime creationHour) {
        this.creationHour = creationHour;
    }
    
    public Integer getIdEstado() {
        return idEstado;
    }
    
    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }
    
    // 🔗 NUEVO: Getter y setter para relación con productos
    public List<RemisionProductoSapEntity> getProductos() {
        return productos;
    }
    
    public void setProductos(List<RemisionProductoSapEntity> productos) {
        this.productos = productos;
    }
    
    // 🎯 Métodos de utilidad
    @Override
    public String toString() {
        return "RemisionSapEntity{" +
                "idRemisionSap=" + idRemisionSap +
                ", delivery='" + delivery + '\'' +
                ", documentDate='" + documentDate + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RemisionSapEntity)) return false;
        RemisionSapEntity that = (RemisionSapEntity) o;
        return idRemisionSap != null && idRemisionSap.equals(that.idRemisionSap);
    }
    
    @Override
    public int hashCode() {
        return idRemisionSap != null ? idRemisionSap.hashCode() : 0;
    }
    
    /**
     * 🎯 NUEVO: Método de conveniencia para obtener el número de productos
     */
    public int getNumeroProductos() {
        return productos != null ? productos.size() : 0;
    }
} 