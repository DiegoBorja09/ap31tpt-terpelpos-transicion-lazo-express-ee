package com.WT2.loyalty.presentation.handler;

import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.loyalty.domain.entities.params.ParamsRedencionBono;
import com.WT2.loyalty.domain.entities.response.RespuestaRedencionBono;
import com.WT2.loyalty.infrastructure.controllers.RedencionBonoController;
import com.WT2.loyalty.presentation.mapper.ParamsRedencionBonoMapper;
import java.util.Map;

public class RedencionBonoHandler {

    RedencionBonoController redencionBonoController = new RedencionBonoController();

    public RespuestaRedencionBono execute(Map<String, Object> input) {

        IMapper<Map<String, Object>, ParamsRedencionBono> paramsRedencion = new ParamsRedencionBonoMapper();
        ParamsRedencionBono paramsRedencionBono = paramsRedencion.mapTo(input);

        return this.redencionBonoController.execute(paramsRedencionBono);
    }

}
