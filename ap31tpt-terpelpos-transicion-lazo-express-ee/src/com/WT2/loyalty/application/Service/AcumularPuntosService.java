/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.application.Service;

import com.WT2.commons.domain.adapters.IService;
import com.WT2.commons.domain.adapters.IUseCase;
import com.WT2.loyalty.domain.entities.beans.LoyaltyAcumulation;
import com.WT2.loyalty.domain.entities.beans.TransactionData;
import com.WT2.loyalty.domain.entities.beans.TransactionDataSaleLoyalty;
import com.WT2.loyalty.domain.entities.params.ParamsAcumularLoyalty;
import com.WT2.loyalty.domain.entities.request.AcumulationLoyaltyRequestBody;
import com.WT2.loyalty.domain.entities.response.RespuestasAcumulacion;

/**
 *
 * @author USUARIO
 */
public class AcumularPuntosService implements IService<ParamsAcumularLoyalty, RespuestasAcumulacion> {

    IUseCase<AcumulationLoyaltyRequestBody, RespuestasAcumulacion> acomularPuntosRequestEndpoint;

    public AcumularPuntosService(IUseCase<AcumulationLoyaltyRequestBody, RespuestasAcumulacion> acomularPuntosRequestEndpoint) {
        this.acomularPuntosRequestEndpoint = acomularPuntosRequestEndpoint;
    }

    @Override
    public RespuestasAcumulacion execute(ParamsAcumularLoyalty input) {
        AcumulationLoyaltyRequestBody acumulationLoyaltyRequestBody = new AcumulationLoyaltyRequestBody();

        TransactionData transactionData = new TransactionData();
        transactionData.setFechaTransaccion(input.getFechaTransaccion());
        transactionData.setIdentificacionPuntoVenta(input.getIdentificacionPuntoVenta());
        transactionData.setOrigenVenta(input.getOrigenVenta());

        TransactionDataSaleLoyalty transactionDataSaleLoyalty = new TransactionDataSaleLoyalty();
        transactionDataSaleLoyalty.setDescuentoVenta(input.getDescuentoVenta());
        transactionDataSaleLoyalty.setIdentificacionPromotor(input.getIdentificacionPromotor());
        transactionDataSaleLoyalty.setIdentificacionVenta(input.getIdentificacionVenta());
        transactionDataSaleLoyalty.setPagoTotal(input.getPagoTotal());
        transactionDataSaleLoyalty.setTotalImpuesto(input.getTotalImpuesto());
        transactionDataSaleLoyalty.setTipoVenta(input.getTipoVenta());
        transactionDataSaleLoyalty.setValorTotalVenta(input.getPagoTotal());
        transactionDataSaleLoyalty.setOrigenVenta(input.getOrigenVenta());
        transactionDataSaleLoyalty.setIdentificacionPuntoVenta(input.getIdentificacionPuntoVenta());
        transactionDataSaleLoyalty.setMovimientoId(input.getMovimientoId());
        transactionDataSaleLoyalty.setFechaTransaccion(input.getFechaTransaccion());
        
        LoyaltyAcumulation loyaltyAcumulation = new LoyaltyAcumulation();
        loyaltyAcumulation.setIdentificacionCliente(input.getIdentificacionCliente());
        loyaltyAcumulation.setMediosPago(input.getMediosPago());
        loyaltyAcumulation.setTransactionData(transactionData);
        loyaltyAcumulation.setProductos(input.getProductos());
        loyaltyAcumulation.setSalesData(transactionDataSaleLoyalty);

        acumulationLoyaltyRequestBody.setDatosAcumulacion(loyaltyAcumulation);
        acumulationLoyaltyRequestBody.setIdIntegration(3);

       
        return acomularPuntosRequestEndpoint.execute(acumulationLoyaltyRequestBody);

    }

}
