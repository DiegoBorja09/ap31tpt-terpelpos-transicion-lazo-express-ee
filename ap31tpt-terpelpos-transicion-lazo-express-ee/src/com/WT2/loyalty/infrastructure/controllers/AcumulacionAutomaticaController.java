/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.infrastructure.controllers;

import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.loyalty.application.Service.AcumulacionAutomatica;
import com.WT2.loyalty.application.UseCase.ConsultarClienteEndpointTerpel;
import com.WT2.loyalty.domain.entities.request.ConsultClientRequestBody;
import com.WT2.loyalty.domain.entities.beans.FoundClient;

/**
 *
 * @author USUARIO
 */
public class AcumulacionAutomaticaController {

    public void execute(ConsultClientRequestBody consultClientRequestBody) {
           AcumulacionAutomatica acumulacionAutomatica = SingletonMedioPago.ConetextDependecy.getAcumulacionAutomatica();
           acumulacionAutomatica.execute(consultClientRequestBody);
        
        
    }
}
