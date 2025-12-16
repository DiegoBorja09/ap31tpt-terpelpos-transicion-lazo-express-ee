/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.commons.mappers;

import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.appTerpel.commons.dto.PaymentRequestDto;
import com.WT2.payment.domian.entities.PaymentRequest;

/**
 *
 * @author USUARIO
 */
public class PaymentRequestDtoMapper implements IMapper<PaymentRequestDto,PaymentRequest>{

    @Override
    public PaymentRequest mapTo(PaymentRequestDto input) {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setIdentificadorMovimiento(input.getIdentificadorMovimiento());
        paymentRequest.setMedioDescription(input.getMedioDescription());
        return paymentRequest;
       
    }
    
}
