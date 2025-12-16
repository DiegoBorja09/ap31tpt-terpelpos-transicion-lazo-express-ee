package com.WT2.loyalty.presentation.mapper;

import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.loyalty.domain.entities.beans.DatosVenta;
import com.WT2.loyalty.domain.entities.beans.IdentificacionClienteRedencion;
import com.WT2.loyalty.domain.entities.beans.MediosPagoRedencion;
import com.WT2.loyalty.domain.entities.params.ParamsRedencionBono;
import java.util.List;
import java.util.Map;

public class ParamsRedencionBonoMapper implements IMapper<Map<String, Object>, ParamsRedencionBono> {
    
    @Override
    public ParamsRedencionBono mapTo(Map<String, Object> input) {
        ParamsRedencionBono params = new ParamsRedencionBono();
        params.setDatosVenta((DatosVenta) input.get("datosVenta"));
        params.setIdentificacionClienteRedencion((IdentificacionClienteRedencion) input.get("identificacionClienteRedencion"));
        params.setMediosPagoRedencion((List<MediosPagoRedencion>) input.get("mediosPagoRedencion"));
        return params;
    }
    
}
