/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.application.UseCase.Payment.Procesing;

import com.WT2.appTerpel.domain.entities.Payment;
import com.WT2.commons.domain.adapters.IRepository;
import com.WT2.payment.domian.valueObject.PaymentStatus;
import com.WT2.commons.domain.valueObject.ProcessStatus;
import com.WT2.commons.domain.adapters.IUseCase;

/**
 *
 * @author USUARIO
 */
public class ValidateIsAppTerpelPaymentSuccess  implements IUseCase<Long, Boolean> {

    private  IRepository<Long, Payment> paymentRepository;

    public ValidateIsAppTerpelPaymentSuccess( IRepository<Long, Payment> paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    
    @Override
    public Boolean execute(Long input) {
        
        Payment payment = paymentRepository.getData(input);
        boolean estadoProceso = (payment.getEstadoProcesoDescripcion().equals(ProcessStatus.CONFIRMADA) 
                                || payment.getEstadoProcesoDescripcion().equals(ProcessStatus.PENDIENTE_REENVIO) 
                                || payment.getEstadoProcesoDescripcion().equals(ProcessStatus.PENDIENTE_CONFIRMACION) );
        boolean permitir = estadoProceso  && payment.getEstadoPagoDescripcion().equals(PaymentStatus.APROBADO);
        return permitir;
    }
}
