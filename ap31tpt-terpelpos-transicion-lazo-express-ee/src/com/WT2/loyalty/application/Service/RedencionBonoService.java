package com.WT2.loyalty.application.Service;

import com.WT2.commons.domain.adapters.IService;
import com.WT2.commons.domain.adapters.IUseCase;
import com.WT2.loyalty.domain.entities.params.ParamsRedencionBono;
import com.WT2.loyalty.domain.entities.request.RedencionBonoRequest;
import com.WT2.loyalty.domain.entities.response.RespuestaRedencionBono;

public class RedencionBonoService implements IService<ParamsRedencionBono, RespuestaRedencionBono> {
    
    IUseCase<RedencionBonoRequest, RespuestaRedencionBono> redencionBono;
    
    public RedencionBonoService(IUseCase<RedencionBonoRequest, RespuestaRedencionBono> redencionBono) {
        this.redencionBono = redencionBono;
    }
    
    @Override
    public RespuestaRedencionBono execute(ParamsRedencionBono input) {
        RedencionBonoRequest redencionBonoRequest = new RedencionBonoRequest();
        
        redencionBonoRequest.setDatosVenta(input.getDatosVenta());
        redencionBonoRequest.setIdentificacionClienteRedencion(input.getIdentificacionClienteRedencion());
        redencionBonoRequest.setMediosPagoRedencion(input.getMediosPagoRedencion());
        
        return redencionBono.execute(redencionBonoRequest);
        
    }
    
}
