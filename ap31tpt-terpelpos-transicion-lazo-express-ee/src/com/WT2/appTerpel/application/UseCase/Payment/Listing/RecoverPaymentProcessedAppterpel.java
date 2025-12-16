/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.application.UseCase.Payment.Listing;

import com.WT2.commons.domain.adapters.IRepository;
import com.WT2.appTerpel.domain.entities.PaymentProcessedParams;
import com.WT2.appTerpel.domain.entities.Sales;
import java.util.List;
import com.WT2.commons.domain.adapters.IUseCase;

/**
 *
 * @author USUARIO
 */
public class RecoverPaymentProcessedAppterpel implements IUseCase<PaymentProcessedParams, List<Sales>> {

    private IRepository<PaymentProcessedParams, List<Sales>> SalesAppTerpelAppTerpelProcess;

    public RecoverPaymentProcessedAppterpel(IRepository<PaymentProcessedParams, List<Sales>> SalesAppTerpelAppTerpelProcess) {
        this.SalesAppTerpelAppTerpelProcess = SalesAppTerpelAppTerpelProcess;
    }

    @Override
    public List<Sales> execute(PaymentProcessedParams input) {
        return SalesAppTerpelAppTerpelProcess.getData(input);
    }

}
