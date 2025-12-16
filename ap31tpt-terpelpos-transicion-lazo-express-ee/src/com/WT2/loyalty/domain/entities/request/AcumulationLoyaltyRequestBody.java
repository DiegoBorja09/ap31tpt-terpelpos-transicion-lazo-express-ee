/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.domain.entities.request;

import com.WT2.loyalty.domain.entities.beans.IdentificationClient;
import com.WT2.loyalty.domain.entities.beans.LoyaltyAcumulation;
import com.WT2.loyalty.domain.entities.beans.MediosPagoLoyalty;
import com.WT2.loyalty.domain.entities.beans.TransactionDataSaleLoyalty;
import com.bean.MediosPagosBean;
import java.util.List;

/**
 *
 * @author USUARIO
 */
public class AcumulationLoyaltyRequestBody {

    private LoyaltyAcumulation datosAcumulacion;
    private int IdIntegration;

    public LoyaltyAcumulation getDatosAcumulacion() {
        return datosAcumulacion;
    }

    public void setDatosAcumulacion(LoyaltyAcumulation datosAcumulacion) {
        this.datosAcumulacion = datosAcumulacion;
    }

    public int getIdIntegration() {
        return IdIntegration;
    }

    public void setIdIntegration(int IdIntegration) {
        this.IdIntegration = IdIntegration;
    }


    
}
