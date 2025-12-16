package com.WT2.loyalty.infrastructure.controllers;

import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.loyalty.application.Service.RedencionBonoService;
import com.WT2.loyalty.application.UseCase.RedimirBono;
import com.WT2.loyalty.domain.entities.params.ParamsRedencionBono;
import com.WT2.loyalty.domain.entities.response.RespuestaRedencionBono;

public class RedencionBonoController {

    public RespuestaRedencionBono execute(ParamsRedencionBono params) {

        RedimirBono redimirBono = SingletonMedioPago.ConetextDependecy.getRedimirBono();

        RedencionBonoService redencionBonoService = new RedencionBonoService(redimirBono);

        return redencionBonoService.execute(params);
        
    }

}
