/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.presentation.handler;

import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.loyalty.domain.entities.request.ConsultClientRequestBody;
import com.WT2.loyalty.infrastructure.controllers.AcumulacionAutomaticaController;
import com.WT2.loyalty.presentation.dto.ConsultClientRequestBodyDto;
import java.util.Map;

/**
 *
 * @author USUARIO
 */
public class AcumulacionAutomaticaHandler {

    AcumulacionAutomaticaController acumulacionAutomaticaController = new AcumulacionAutomaticaController();
    
    public void execute(Map<String, String> input) {
        
        IMapper<Map<String, String>, ConsultClientRequestBodyDto> acumulacionDtoMapper = SingletonMedioPago.ConetextDependecy.getConsultClientRequstBodyDtoMapper();
        IMapper<ConsultClientRequestBodyDto, ConsultClientRequestBody> acumalocionMapper = SingletonMedioPago.ConetextDependecy.getConsultClientRequestBodyMapper();
        ConsultClientRequestBodyDto acumulacionRequestBodyDto = acumulacionDtoMapper.mapTo(input);
        ConsultClientRequestBody consultClientRequestBody = acumalocionMapper.mapTo(acumulacionRequestBodyDto);
        
        acumulacionAutomaticaController.execute(consultClientRequestBody);
        
    }

}
