/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.presentation.controllers;

import com.WT2.appTerpel.application.UseCase.Payment.Listing.RecoverPaymentProcessedAppterpel;
import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.appTerpel.domain.entities.PaymentProcessedParams;
import com.WT2.appTerpel.domain.entities.Sales;
import java.util.List;

/**
 *
 * @author USUARIO
 */
public class SalesAppTerpelProcessedController {

    public List<Sales> execute(PaymentProcessedParams params) {
        
        RecoverPaymentProcessedAppterpel recoverPaymentProcessedAppterpel = SingletonMedioPago.ConetextDependecy.getRecoverPaymentProcessedAppterpel();
        return recoverPaymentProcessedAppterpel.execute(params);
    }

}
