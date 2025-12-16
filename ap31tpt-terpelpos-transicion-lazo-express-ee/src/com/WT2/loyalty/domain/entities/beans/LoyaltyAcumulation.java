/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.domain.entities.beans;

import java.util.List;

/**
 *
 * @author USUARIO
 */
public class LoyaltyAcumulation {

    private IdentificationClient identificacionCliente;
    private List<ProductsLoyalty> productos;
    private List<MediosPagoLoyalty> mediosPago;
    private TransactionDataSaleLoyalty salesData;
    private TransactionData transactionData;

    public List<MediosPagoLoyalty> getMediosPago() {
        return mediosPago;
    }

    public void setMediosPago(List<MediosPagoLoyalty> mediosPago) {
        this.mediosPago = mediosPago;
    }

    public IdentificationClient getIdentificacionCliente() {
        return identificacionCliente;
    }

    public void setIdentificacionCliente(IdentificationClient identificacionCliente) {
        this.identificacionCliente = identificacionCliente;
    }

    public List<ProductsLoyalty> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductsLoyalty> productos) {
        this.productos = productos;
    }

    public TransactionDataSaleLoyalty getSalesData() {
        return salesData;
    }

    public void setSalesData(TransactionDataSaleLoyalty salesData) {
        this.salesData = salesData;
    }

    public TransactionData getTransactionData() {
        return transactionData;
    }

    public void setTransactionData(TransactionData transactionData) {
        this.transactionData = transactionData;
    }

}
