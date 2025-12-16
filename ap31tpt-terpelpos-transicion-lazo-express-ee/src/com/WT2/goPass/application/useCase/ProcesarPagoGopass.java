/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.goPass.application.useCase;

import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.commons.domain.adapters.IHttpClientRepository;
import com.WT2.commons.domain.adapters.IRepository;
import com.WT2.commons.domain.adapters.IUseCase;
import com.WT2.goPass.domain.entity.beans.PaymentGopassParams;
import com.WT2.goPass.domain.entity.response.PagoGopassResponse;
import com.WT2.payment.application.usesCase.EnviandoMedioPago;
import com.WT2.payment.domian.entities.PaymentResponse;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 *
 * @author USUARIO
 */
public class ProcesarPagoGopass {

    IHttpClientRepository<PagoGopassResponse> httpClient;
    IRepository<PaymentGopassParams, Object> repository;
    EnviandoMedioPago enviandoMedioPago;

    public ProcesarPagoGopass(IHttpClientRepository<PagoGopassResponse> httpClient, IRepository<PaymentGopassParams, Object> iRepository, EnviandoMedioPago enviandoMedioPago) {

        this.httpClient = httpClient;
        this.repository = iRepository;
        this.enviandoMedioPago = enviandoMedioPago;

    }

    public PaymentResponse execute(PaymentGopassParams input) throws ExecutionException, InterruptedException {

        PaymentResponse response = new PaymentResponse();
        response.setEstadoPago("X");
        response.setMensaje("Error de comunicación");

        this.repository.updateData(input);
        
        boolean isNotDone = true;
        boolean isNotCancelled = true;
        boolean isNotTimeToStop = true;
        Long initialTime = System.currentTimeMillis();
        
        Future<PaymentResponse> notifyPaymentFuture = this.enviandoMedioPago.execute(input.getIdentificadorVentaTerpel());

        while (isNotDone && isNotCancelled && isNotTimeToStop) {

                isNotDone = !notifyPaymentFuture.isDone();
                isNotCancelled = !notifyPaymentFuture.isCancelled();
                long currentTime = System.currentTimeMillis();

                if (currentTime - initialTime > input.getTimeOut()) {
                    isNotTimeToStop = false;
                }
            }

            if (!isNotDone) {
                response = notifyPaymentFuture.get();
            }
        
           
        return response;
    }

}
