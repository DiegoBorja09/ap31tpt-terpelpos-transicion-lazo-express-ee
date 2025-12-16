package com.bean.entradaCombustible;

import com.bean.BodegaBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntradaCombustibleBean {

    private String delivery;
    private Long idRemision;
    private String documentDate;
    private String wayBill;
    private String logisticCenter;
    private String supplyingCenter;
    private String frontierLaw;
    private String status;
    private String modificationDate;
    private String modificationHour;
    private Map<String, ProductoSAP> productoSAP;
    private Map<String, ArrayList<BodegaBean>> tanques;

    public String getDelivery() {
        return delivery;
    }

    public Long getIdRemision() {
        return idRemision;
    }

    public void setIdRemision(Long idRemision) {
        this.idRemision = idRemision;
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

    public String getFrontierLaw() {
        return frontierLaw;
    }

    public void setFrontierLaw(String frontierLaw) {
        this.frontierLaw = frontierLaw;
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

    public Map<String, ProductoSAP> getProductoSAP() {
        return productoSAP;
    }

    public void setProductoSAP(Map<String, ProductoSAP> productoSAP) {
        if (productoSAP == null) {
            System.out.println("[DEBUG] setProductoSAP: El mapa recibido es null");
        } else {
            System.out.println("[DEBUG] setProductoSAP: Tamaño del mapa recibido = " + productoSAP.size());
            if (productoSAP.size() == 0) {
                System.out.println("[DEBUG] setProductoSAP: El mapa está vacío, revisar consulta de productos");
            }
        }
        this.productoSAP = productoSAP;
    }

    public Map<String, ArrayList<BodegaBean>> getTanques() {
        return tanques;
    }

    public void setTanques(Map<String, ArrayList<BodegaBean>> tanques) {
        this.tanques = tanques;
    }

    @Override
    public String toString() {
        return "EntradaCombustibleBean{" + "delivery=" + delivery + ", idRemision=" + idRemision + ", documentDate=" + documentDate + ", wayBill=" + wayBill + ", logisticCenter=" + logisticCenter + ", supplyingCenter=" + supplyingCenter + ", frontierLaw=" + frontierLaw + ", status=" + status + ", modificationDate=" + modificationDate + ", modificationHour=" + modificationHour + ", productoSAP=" + productoSAP + ", tanques=" + tanques + '}';
    }

}
