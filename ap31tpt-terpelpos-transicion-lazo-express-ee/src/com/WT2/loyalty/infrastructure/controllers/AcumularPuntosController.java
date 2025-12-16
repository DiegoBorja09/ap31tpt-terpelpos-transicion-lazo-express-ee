/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.infrastructure.controllers;

import com.WT2.commons.domain.adapters.IController;
import com.WT2.loyalty.application.Service.AcumularPuntosService;
import com.WT2.loyalty.domain.entities.params.ParamsAcumularLoyalty;
import com.WT2.loyalty.domain.entities.response.RespuestasAcumulacion;

/**
 *
 * @author USUARIO
 */
public class AcumularPuntosController implements IController<ParamsAcumularLoyalty, RespuestasAcumulacion> {

    private AcumularPuntosService acumularPuntosService ;

    public AcumularPuntosController(AcumularPuntosService acumularPuntosService) {
        this.acumularPuntosService = acumularPuntosService;
    }

    
    public RespuestasAcumulacion execute(ParamsAcumularLoyalty paramsAcumularLoyalty) {
      return  acumularPuntosService.execute(paramsAcumularLoyalty);
    }
}
