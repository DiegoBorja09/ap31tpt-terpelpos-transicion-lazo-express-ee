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
import java.util.Objects;

/**
 *
 * @author USUARIO
 */
public class ValidateIfDeleteAppTerpelPayment implements IUseCase<Long, Boolean> {

    private IRepository<Long, Payment> paymentRepository;

    public ValidateIfDeleteAppTerpelPayment( IRepository<Long, Payment> paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    
    @Override
    public Boolean execute(Long input) {
        
        Payment payment = paymentRepository.getData(input);
        if(payment.getEstadoPagoDescripcion().equals(PaymentStatus.NO_ENCONTRADO)){
         return true;
        }
        boolean statusPayment = payment.getEstadoPagoDescripcion().equals(PaymentStatus.RECHAZADO) || payment.getEstadoPagoDescripcion().equals(PaymentStatus.RECHAZADO_FAIL) || payment.getEstadoPagoDescripcion().equals(PaymentStatus.EXPIRADO);
        boolean statusProcess = Objects.equals(payment.getIdEstadoProceso(), ProcessStatus.ID_CONFIRMADA)
                || Objects.equals(payment.getIdEstadoProceso(), ProcessStatus.ID_PROCESADO)
                || Objects.equals(payment.getIdEstadoProceso(), ProcessStatus.ID_PENDIENTE_CONFIRMACION)
                || Objects.equals(payment.getIdEstadoProceso(), ProcessStatus.ID_PENDIENTE_REENVIO);
        boolean letDelete = statusProcess && statusPayment;
        return letDelete;
    }

}
