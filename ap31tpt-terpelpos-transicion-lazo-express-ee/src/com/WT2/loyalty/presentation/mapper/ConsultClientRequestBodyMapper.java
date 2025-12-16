/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.presentation.mapper;

import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.loyalty.domain.entities.request.ConsultClientRequestBody;
import com.WT2.loyalty.domain.entities.beans.TransactionData;
import com.WT2.loyalty.domain.entities.beans.IdentificationClient;
import com.WT2.loyalty.presentation.dto.ConsultClientRequestBodyDto;

/**
 *
 * @author USUARIO
 */
public class ConsultClientRequestBodyMapper implements  IMapper<ConsultClientRequestBodyDto, ConsultClientRequestBody>{

    @Override
    public ConsultClientRequestBody mapTo(ConsultClientRequestBodyDto input) {
        
          IdentificationClient identificationClient = new IdentificationClient();
          identificationClient.setCodigoTipoIdentificacion(input.getCodigoTipoIdentificacion());
          identificationClient.setNumeroIdentificacion(input.getNumeroIdentificacion());
        
          TransactionData customer = new TransactionData();
          ConsultClientRequestBody  acumulacionRequestBody =  new ConsultClientRequestBody();
          
          customer.setOrigenVenta(input.getOrigenVenta());
          customer.setIdentificacionPuntoVenta(input.getIdentificacionPuntoVenta());
          customer.setFechaTransaccion(input.getFechaTransaccion());
          acumulacionRequestBody.setCustomer(identificationClient);
          acumulacionRequestBody.setIdIntegracion(input.getIdIntegracion());
          acumulacionRequestBody.setTransactionData(customer);
          return acumulacionRequestBody;
    }
    
}
