/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.presentation;

import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.appTerpel.commons.dto.MedioPagoInTableViewDto;
import com.WT2.appTerpel.presentation.controllers.SendPaymentAppTerpelToEndPointAgente;
import com.WT2.loyalty.domain.entities.beans.ProcesosPagosFidelizacionParams;
import com.bean.MovimientosBean;
import com.firefuel.MedioPagosConfirmarViewController;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author USUARIO
 */
public class SendPaymentAppTerpelToEndPointHandler {

    private SendPaymentAppTerpelToEndPointAgente controller;

    public SendPaymentAppTerpelToEndPointHandler() {
        this.controller = new SendPaymentAppTerpelToEndPointAgente();
    }

    public void execute(DefaultTableModel defaultTableModel, MovimientosBean movimientosBean) {
        try {
            
            //            stm.setLong(1, procesosFidelizacion.getIdMov());
//            stm.setInt(2, 3);
//            stm.setInt(3, 3);
//            stm.setInt(4, 4);
//            stm.setInt(5, 1);
//            stm.setString(6, "N");
//            stm.setBoolean(7, true);
            List<MedioPagoInTableViewDto> medioDtoList = SingletonMedioPago.ConetextDependecy.getMedioPagoInTableViewDtoMapper().mapTo(defaultTableModel.getDataVector());
            ProcesosPagosFidelizacionParams params = new ProcesosPagosFidelizacionParams();
            params.setIdMov(movimientosBean.getId());
            params.setTipoIdentificador(3);
            params.setIdTipoTransaccionProceso(3);
            params.setIdEstadoProceso(4);
            params.setIdTipoNegocio(1);
            params.setEditarFidelizacion(true);
            params.setFideliza("N");
            
            
            controller.execute(medioDtoList, params);

        } catch (Exception ex) {
            Logger.getLogger(SendPaymentAppTerpelToEndPointHandler.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

}
