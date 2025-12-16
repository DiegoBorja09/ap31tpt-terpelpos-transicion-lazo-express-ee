/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.infrastructure.controllers;

import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.commons.application.UseCase.RecuperarParameterFidelizacion;
import com.WT2.loyalty.application.Service.ConsultarClienteService;
import com.WT2.loyalty.application.UseCase.ConsultarClienteEndpointTerpel;
import com.WT2.loyalty.domain.entities.beans.FoundClient;
import com.WT2.loyalty.domain.entities.params.ParamsConsultarCliente;

/**
 *
 * @author USUARIO
 */
public class ConsultarClienteController {
    
    
    public FoundClient execute(ParamsConsultarCliente input){
        
        
        ConsultarClienteEndpointTerpel clienteFidelizacion = SingletonMedioPago.ConetextDependecy.getConsultarCliente();
        RecuperarParameterFidelizacion recuperarParameter = SingletonMedioPago.ConetextDependecy.getRecuperarParameter();
        ConsultarClienteService consultarClienteService  = new ConsultarClienteService(clienteFidelizacion, recuperarParameter);
        
        return consultarClienteService.execute(input);
        
        
    }
    
}
