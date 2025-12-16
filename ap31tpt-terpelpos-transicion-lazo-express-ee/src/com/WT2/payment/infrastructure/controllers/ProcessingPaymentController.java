/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.payment.infrastructure.controllers;

import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.appTerpel.domain.entities.NotificationPayment;
import com.WT2.payment.application.usesCase.EnviandoMedioPago;
import com.WT2.payment.domian.entities.PaymentRequest;
import com.WT2.payment.domian.entities.PaymentResponse;
import java.util.concurrent.Future;

/**
 *
 * @author USUARIO
 */
public class ProcessingPaymentController {

    public Future<PaymentResponse> execute(PaymentRequest input) {
        EnviandoMedioPago enviandoMedioPago = SingletonMedioPago.ConetextDependecy.getEnvioMedioPago();
        return enviandoMedioPago.execute(input);
    }

}
