/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.presentation.handler;

import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.loyalty.domain.entities.beans.IdentificationClient;
import com.WT2.loyalty.domain.entities.params.ParamsAcumularLoyalty;
import com.WT2.loyalty.domain.entities.response.RespuestasAcumulacion;
import com.WT2.loyalty.presentation.dto.ParamsAcumularLoyaltyDto;
import com.bean.MovimientosBean;
import java.util.Map;

/**
 *
 * @author USUARIO
 */
public class AcomulacionPuntosHandler {

    public RespuestasAcumulacion execute(Map<String, Object> paramsAcumularRaw) {

        IMapper<Map<String, Object>, ParamsAcumularLoyaltyDto> paramsDto = SingletonMedioPago.ConetextDependecy.getParamsAcumularLoyaltyMapperDto();
        IMapper<ParamsAcumularLoyaltyDto, ParamsAcumularLoyalty> params = SingletonMedioPago.ConetextDependecy.getParamsAcumularLoyaltyMapper();
        return SingletonMedioPago.ConetextDependecy.getAcumularPuntosController().execute(params.mapTo(paramsDto.mapTo(paramsAcumularRaw)));

    }

}
