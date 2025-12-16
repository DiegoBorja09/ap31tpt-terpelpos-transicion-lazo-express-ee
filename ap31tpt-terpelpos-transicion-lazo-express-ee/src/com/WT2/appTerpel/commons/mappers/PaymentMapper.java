/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.commons.mappers;

import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.appTerpel.commons.dto.PaymentDTO;
import com.WT2.appTerpel.domain.entities.Payment;
import com.WT2.payment.domian.valueObject.PaymentStatus;
import com.WT2.commons.domain.valueObject.ProcessStatus;

/**
 *
 * @author USUARIO
 */
public class PaymentMapper implements IMapper<PaymentDTO, Payment> {

    @Override
    public Payment mapTo(PaymentDTO input) {
        
        Payment payment = new Payment();
        payment.setIdPago(input.getId_pago());
        payment.setIdEstadoProceso(input.getId_estado());
        payment.setIdEstadoPago(input.getId_estatus());
        payment.setEstadoProcesoDescripcion(input.getEstado_proceso_descripcion());
        payment.setEstadoPagoDescripcion(input.getEstado_venta_descripcion());
        payment.setIdTipoIntegracion(input.getId_tipo_integracion());
        payment.setIdMovimiento(input.getId_movimiento());
        return payment;
    }

}
