/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.presentation.handler;

import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.loyalty.domain.entities.beans.FoundClient;
import com.WT2.loyalty.domain.entities.params.ParamsConsultarCliente;
import com.WT2.loyalty.infrastructure.controllers.ConsultarClienteController;
import com.WT2.loyalty.presentation.mapper.ParamsConsultarClienteMapper;
import java.util.Map;

/**
 *
 * @author USUARIO
 */
public class ConsultarClienteHandler {

    ConsultarClienteController clienteController = new ConsultarClienteController();

    public FoundClient execute(Map<String, String> input) {
        
        
         IMapper<Map<String, String>, ParamsConsultarCliente> paramsConsultarClienteParams = new ParamsConsultarClienteMapper();
         ParamsConsultarCliente paramsConsultarCliente = paramsConsultarClienteParams.mapTo(input);
      
          return clienteController.execute(paramsConsultarCliente);

    }
}
