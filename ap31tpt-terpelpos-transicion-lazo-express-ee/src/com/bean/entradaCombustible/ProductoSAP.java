/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bean.entradaCombustible;

/**
 *
 * @author Devitech
 */
public class ProductoSAP {

    private float quantity;
    private String unit;
    private String product;
    private Integer producID;
    private String descripcion;
    private float salesCostValue;
    private long idRemisionProducto;
    private String clave;

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public float getSalesCostValue() {
        return salesCostValue;
    }

    public void setSalesCostValue(float salesCostValue) {
        this.salesCostValue = salesCostValue;
    }

    public Integer getProducID() {
        return producID;
    }

    public void setProducID(Integer producID) {
        this.producID = producID;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public long getIdRemisionProducto() {
        return idRemisionProducto;
    }

    public void setIdRemisionProducto(long idRemisionProducto) {
        this.idRemisionProducto = idRemisionProducto;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    @Override
    public String toString() {
        return "ProductoSAP{" 
                + "quantity=" + quantity 
                + ", unit=" + unit 
                + ", product=" + product 
                + ", producID=" + producID 
                + ", descripcion=" + descripcion 
                + ", salesCostValue=" + salesCostValue 
                + ", idRemisionProducto=" + idRemisionProducto 
                + ", clave=" + clave 
                + '}';
    }
    
}
