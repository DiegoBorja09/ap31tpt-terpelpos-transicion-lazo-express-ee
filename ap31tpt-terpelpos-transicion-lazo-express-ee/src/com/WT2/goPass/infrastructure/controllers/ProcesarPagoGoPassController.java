/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.goPass.infrastructure.controllers;

import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.goPass.application.useCase.ProcesarPagoGopass;
import com.WT2.goPass.domain.entity.beans.PaymentGopassParams;
import com.WT2.loyalty.domain.entities.beans.ProcesosPagosFidelizacionParams;
import com.WT2.payment.domian.entities.PaymentResponse;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author USUARIO
 */
public class ProcesarPagoGoPassController {
    
    
  public PaymentResponse execute(PaymentGopassParams paymentGopassParams) throws ExecutionException, InterruptedException{
      ProcesosPagosFidelizacionParams procesosFidelizacion = new ProcesosPagosFidelizacionParams();
      procesosFidelizacion.setIdMov(paymentGopassParams.getIdentificadorVentaTerpel().getIdentificadorMovimiento());
      procesosFidelizacion.setTipoIdentificador(3);
      procesosFidelizacion.setIdTipoTransaccionProceso(3);
      procesosFidelizacion.setIdEstadoProceso(1);
      procesosFidelizacion.setIdTipoNegocio(1);
      procesosFidelizacion.setFideliza("S");
      procesosFidelizacion.setEditarFidelizacion(false);
      SingletonMedioPago.ConetextDependecy.getCreateLoyaltiProcess().execute(procesosFidelizacion);
      ProcesarPagoGopass pagoGopass = SingletonMedioPago.ConetextDependecy.getProcesarPagoGopass();
      return pagoGopass.execute(paymentGopassParams);
    
  }
    
}
