/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.domain.entities.request;

import com.WT2.commons.domain.entity.Request;
import com.WT2.loyalty.domain.entities.beans.IdentificationClient;
import com.WT2.loyalty.domain.entities.beans.TransactionData;

/**
 *
 * @author USUARIO
 */
public class ConsultClientRequestBody {

    private TransactionData transactionData;
    private IdentificationClient customer;
    private Integer idIntegracion;

    public TransactionData getTransactionData() {
        return transactionData;
    }

    public void setTransactionData(TransactionData transactionData) {
        this.transactionData = transactionData;
    }

    public IdentificationClient getCustomer() {
        return customer;
    }

    public void setCustomer(IdentificationClient customer) {
        this.customer = customer;
    }

    public Integer getIdIntegracion() {
        return idIntegracion;
    }

    public void setIdIntegracion(Integer idIntegracion) {
        this.idIntegracion = idIntegracion;
    }

}
