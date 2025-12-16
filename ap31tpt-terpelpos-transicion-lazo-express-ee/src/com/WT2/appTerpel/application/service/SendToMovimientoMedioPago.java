/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.application.service;

import com.WT2.payment.application.usesCase.EnviandoMedioPago;
import com.WT2.appTerpel.application.UseCase.Payment.Procesing.FindSelecetedPaymentAppTerpel;
import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.appTerpel.domain.entities.MedioPagoInTableView;
import com.WT2.appTerpel.domain.entities.NotificationPayment;
import com.WT2.payment.domian.entities.PaymentRequest;
import com.WT2.appTerpel.domain.valueObject.MediosPagosDescription;
import com.WT2.commons.domain.adapters.IUseCase;
import com.WT2.payment.domian.entities.PaymentResponse;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author USUARIO
 */
public class SendToMovimientoMedioPago implements IUseCase<List<MedioPagoInTableView>, PaymentResponse>{

    private FindSelecetedPaymentAppTerpel validarSeleccionMedioAppTerpel;

    public SendToMovimientoMedioPago() {
        this.validarSeleccionMedioAppTerpel = new FindSelecetedPaymentAppTerpel();
    }

    @Override
    public PaymentResponse execute(List<MedioPagoInTableView> input)  {

        MedioPagoInTableView isAppTerpel = this.validarSeleccionMedioAppTerpel.execute(input);
        PaymentResponse notificationPayment = new PaymentResponse();
       

        if (isAppTerpel.validateIsPayment(MediosPagosDescription.APPTERPEL)) {
            EnviandoMedioPago enviandoMedioPago =SingletonMedioPago.ConetextDependecy.getEnvioMedioPago();
            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setIdentificadorMovimiento(isAppTerpel.getIdentificadorMovimeinto());
            paymentRequest.setMedioDescription(isAppTerpel.getMedio());
            Future<PaymentResponse> future = enviandoMedioPago.execute(paymentRequest);
            if (future.isDone()) {
                try {
                    notificationPayment = future.get();
                } catch (InterruptedException ex) {
                    Logger.getLogger(SendToMovimientoMedioPago.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(SendToMovimientoMedioPago.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        //Devolver respuesta
        return notificationPayment;

    }
}
