/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.presentation.controllers;

import com.WT2.appTerpel.application.service.SendToMovimientoMedioPago;
import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.appTerpel.commons.dto.MedioPagoInTableViewDto;
import com.WT2.appTerpel.domain.entities.MedioPagoInTableView;
import com.WT2.loyalty.domain.entities.beans.ProcesosPagosFidelizacionParams;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author USUARIO
 */

public class SendPaymentAppTerpelToEndPointAgente {
    
    
   
    
    public void execute(List<MedioPagoInTableViewDto> medioDtoList, ProcesosPagosFidelizacionParams procesosFidelizacion) throws InterruptedException, ExecutionException, Exception {

        List<MedioPagoInTableView> medioPagoInTableView = SingletonMedioPago.ConetextDependecy.getMedioPagoInTableViewListMapper().mapTo(medioDtoList);
        SingletonMedioPago.ConetextDependecy.getAddIdMovimientoToMedioPagoView().execute(medioPagoInTableView, procesosFidelizacion.getIdMov());
        SingletonMedioPago.ConetextDependecy.getSendToMovimientoMedioPago().execute(medioPagoInTableView);
        SingletonMedioPago.ConetextDependecy.getCreateLoyaltiProcess().execute(procesosFidelizacion);
        
    }
}
