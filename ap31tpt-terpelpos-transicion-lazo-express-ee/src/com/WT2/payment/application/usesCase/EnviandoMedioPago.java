/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.payment.application.usesCase;

import com.WT2.commons.domain.adapters.IHttpClientRepository;
import com.WT2.appTerpel.domain.entities.NotificationPayment;
import com.WT2.payment.domian.entities.PaymentRequest;
import com.WT2.appTerpel.domain.valueObject.Endpoints;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import com.WT2.commons.domain.adapters.IUseCase;
import com.WT2.commons.domain.entity.Request;
import com.WT2.commons.domain.valueObject.HttpContentType;
import com.WT2.commons.domain.valueObject.HttpMethod;
import com.WT2.payment.domian.entities.PaymentResponse;

/**
 *
 * @author USUARIO
 */
public class EnviandoMedioPago implements IUseCase<PaymentRequest, Future<PaymentResponse>> {

    private IHttpClientRepository<PaymentResponse> httpClient;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public EnviandoMedioPago(IHttpClientRepository<PaymentResponse> httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Future<PaymentResponse> execute(PaymentRequest input) {
        Request<PaymentRequest> request = new Request<>();
        request.setBody(input);
        request.setMethod(HttpMethod.POST);
        request.setContent(HttpContentType.JSON);
        Future<PaymentResponse> fr = executor.submit(() -> {
            return httpClient.send(Endpoints.PAYMENTORQUESTOR, request, PaymentResponse.class).getResponse();
        });
        
       
        return fr;

    }

}
